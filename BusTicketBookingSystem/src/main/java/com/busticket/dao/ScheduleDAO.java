package com.busticket.dao;

import com.busticket.model.Schedule;
import com.busticket.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {

    private static final String BASE_QUERY =
        "SELECT s.*, b.bus_name, b.bus_number, b.bus_type, b.fare_per_seat, " +
        "       r.source, r.destination " +
        "FROM schedules s " +
        "JOIN buses  b ON s.bus_id   = b.bus_id " +
        "JOIN routes r ON s.route_id = r.route_id ";

    // ----------------------------------------------------------------
    //  Search available schedules by source & destination
    // ----------------------------------------------------------------
    public List<Schedule> searchSchedules(String source, String destination) throws SQLException {
        String sql = BASE_QUERY +
            "WHERE LOWER(r.source) = LOWER(?) " +
            "  AND LOWER(r.destination) = LOWER(?) " +
            "  AND s.available_seats > 0 " +
            "  AND s.status = 'SCHEDULED' " +
            "  AND s.departure_time > NOW() " +
            "ORDER BY s.departure_time";

        List<Schedule> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, source);
            ps.setString(2, destination);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ----------------------------------------------------------------
    //  All upcoming schedules
    // ----------------------------------------------------------------
    public List<Schedule> getAllUpcoming() throws SQLException {
        String sql = BASE_QUERY +
            "WHERE s.status = 'SCHEDULED' AND s.departure_time > NOW() " +
            "ORDER BY s.departure_time";
        List<Schedule> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ----------------------------------------------------------------
    //  Find by ID
    // ----------------------------------------------------------------
    public Schedule findById(int scheduleId) throws SQLException {
        String sql = BASE_QUERY + "WHERE s.schedule_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ----------------------------------------------------------------
    //  Decrement available seats (called within a transaction)
    // ----------------------------------------------------------------
    public boolean decrementSeats(Connection conn, int scheduleId, int seats) throws SQLException {
        String sql = "UPDATE schedules SET available_seats = available_seats - ? " +
                     "WHERE schedule_id = ? AND available_seats >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seats);
            ps.setInt(2, scheduleId);
            ps.setInt(3, seats);
            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    //  Increment available seats (on cancellation)
    // ----------------------------------------------------------------
    public boolean incrementSeats(Connection conn, int scheduleId, int seats) throws SQLException {
        String sql = "UPDATE schedules SET available_seats = available_seats + ? " +
                     "WHERE schedule_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seats);
            ps.setInt(2, scheduleId);
            return ps.executeUpdate() > 0;
        }
    }

    // ---- Mapper ----
    private Schedule mapRow(ResultSet rs) throws SQLException {
        Schedule sc = new Schedule();
        sc.setScheduleId(rs.getInt("schedule_id"));
        sc.setBusId(rs.getInt("bus_id"));
        sc.setRouteId(rs.getInt("route_id"));
        sc.setAvailableSeats(rs.getInt("available_seats"));
        sc.setStatus(rs.getString("status"));
        sc.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
        sc.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());
        sc.setBusName(rs.getString("bus_name"));
        sc.setBusNumber(rs.getString("bus_number"));
        sc.setBusType(rs.getString("bus_type"));
        sc.setFarePerSeat(rs.getDouble("fare_per_seat"));
        sc.setSource(rs.getString("source"));
        sc.setDestination(rs.getString("destination"));
        return sc;
    }
}
