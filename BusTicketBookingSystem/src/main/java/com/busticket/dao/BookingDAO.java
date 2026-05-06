package com.busticket.dao;

import com.busticket.model.Booking;
import com.busticket.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private static final String BASE_QUERY =
        "SELECT bk.*, u.name AS user_name, " +
        "       r.source, r.destination, b.bus_name, s.departure_time " +
        "FROM bookings bk " +
        "JOIN users     u ON bk.user_id     = u.user_id " +
        "JOIN schedules s ON bk.schedule_id = s.schedule_id " +
        "JOIN routes    r ON s.route_id     = r.route_id " +
        "JOIN buses b ON s.bus_id= b.bus_id ";

    // ----------------------------------------------------------------
    //  Create booking  (transactional: booking + seat decrement + audit)
    // ----------------------------------------------------------------
    public Booking createBooking(int userId, int scheduleId,
                                 int seats, BigDecimal totalFare,
                                 ScheduleDAO scheduleDAO) throws SQLException {

        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            // 1. Decrement seats
            boolean ok = scheduleDAO.decrementSeats(conn, scheduleId, seats);
            if (!ok) {
                conn.rollback();
                throw new SQLException("Not enough seats available.");
            }

            // 2. Insert booking
            String insertBooking =
                "INSERT INTO bookings (user_id, schedule_id, seats_booked, total_fare, booking_status) " +
                "VALUES (?, ?, ?, ?, 'CONFIRMED')";
            int newId;
            try (PreparedStatement ps = conn.prepareStatement(insertBooking, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setInt(2, scheduleId);
                ps.setInt(3, seats);
                ps.setBigDecimal(4, totalFare);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    newId = keys.getInt(1);
                }
            }

            // 3. Audit entry
            insertAudit(conn, newId, null, "CONFIRMED", "Booking created");

            conn.commit();

            Booking b = new Booking();
            b.setBookingId(newId);
            b.setUserId(userId);
            b.setScheduleId(scheduleId);
            b.setSeatsBooked(seats);
            b.setTotalFare(totalFare);
            b.setBookingStatus("CONFIRMED");
            return b;

        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ----------------------------------------------------------------
    //  Cancel booking  (transactional: status update + seat restore + audit)
    // ----------------------------------------------------------------
    public boolean cancelBooking(int bookingId, ScheduleDAO scheduleDAO) throws SQLException {
        // Fetch booking first (outside transaction)
        Booking existing = findById(bookingId);
        if (existing == null) throw new SQLException("Booking not found.");
        if ("CANCELLED".equals(existing.getBookingStatus()))
            throw new SQLException("Booking already cancelled.");

        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            // 1. Update status
            String upd = "UPDATE bookings SET booking_status='CANCELLED' WHERE booking_id=?";
            try (PreparedStatement ps = conn.prepareStatement(upd)) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }

            // 2. Restore seats
            scheduleDAO.incrementSeats(conn, existing.getScheduleId(), existing.getSeatsBooked());

            // 3. Audit
            insertAudit(conn, bookingId, "CONFIRMED", "CANCELLED", "User cancellation");

            conn.commit();
            return true;
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ----------------------------------------------------------------
    //  Bookings for a user
    // ----------------------------------------------------------------
    public List<Booking> getBookingsByUser(int userId) throws SQLException {
        String sql = BASE_QUERY + "WHERE bk.user_id = ? ORDER BY bk.booked_at DESC";
        List<Booking> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ----------------------------------------------------------------
    //  Find booking by ID
    // ----------------------------------------------------------------
    public Booking findById(int bookingId) throws SQLException {
        String sql = BASE_QUERY + "WHERE bk.booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ----------------------------------------------------------------
    //  All bookings (admin)
    // ----------------------------------------------------------------
    public List<Booking> getAllBookings() throws SQLException {
        String sql = BASE_QUERY + "ORDER BY bk.booked_at DESC";
        List<Booking> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ----------------------------------------------------------------
    //  Audit log for a booking
    // ----------------------------------------------------------------
    public void printAuditLog(int bookingId) throws SQLException {
        String sql = "SELECT * FROM booking_audit WHERE booking_id = ? ORDER BY changed_at";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.printf("  %-5s %-12s %-12s %-20s %s%n",
                        "ID", "OLD_STATUS", "NEW_STATUS", "CHANGED_AT", "REMARKS");
                System.out.println("  " + "-".repeat(68));
                while (rs.next()) {
                    System.out.printf("  %-5d %-12s %-12s %-20s %s%n",
                            rs.getInt("audit_id"),
                            rs.getString("old_status") == null ? "—" : rs.getString("old_status"),
                            rs.getString("new_status"),
                            rs.getTimestamp("changed_at").toLocalDateTime(),
                            rs.getString("remarks"));
                }
            }
        }
    }

    // ---- Private helpers ----
    private void insertAudit(Connection conn, int bookingId,
                             String oldStatus, String newStatus,
                             String remarks) throws SQLException {
        String sql = "INSERT INTO booking_audit (booking_id, old_status, new_status, remarks) " +
                     "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setString(2, oldStatus);
            ps.setString(3, newStatus);
            ps.setString(4, remarks);
            ps.executeUpdate();
        }
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("booking_id"));
        b.setUserId(rs.getInt("user_id"));
        b.setScheduleId(rs.getInt("schedule_id"));
        b.setSeatsBooked(rs.getInt("seats_booked"));
        b.setTotalFare(rs.getBigDecimal("total_fare"));
        b.setBookingStatus(rs.getString("booking_status"));
        Timestamp ts = rs.getTimestamp("booked_at");
        if (ts != null) b.setBookedAt(ts.toLocalDateTime());
        b.setUserName(rs.getString("user_name"));
        b.setSource(rs.getString("source"));
        b.setDestination(rs.getString("destination"));
        b.setBusName(rs.getString("bus_name"));
        Timestamp dep = rs.getTimestamp("departure_time");
        if (dep != null) b.setDepartureTime(dep.toLocalDateTime());
        return b;
    }
}
