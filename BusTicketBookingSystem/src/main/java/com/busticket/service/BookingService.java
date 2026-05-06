package com.busticket.service;

import com.busticket.dao.BookingDAO;
import com.busticket.dao.ScheduleDAO;
import com.busticket.model.Booking;
import com.busticket.model.Schedule;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class BookingService {

    private final BookingDAO  bookingDAO  = new BookingDAO();
    private final ScheduleDAO scheduleDAO = new ScheduleDAO();

    // ----------------------------------------------------------------
    //  Book tickets
    // ----------------------------------------------------------------
    public Booking bookTickets(int userId, int scheduleId, int seats) throws SQLException {
        if (seats < 1) throw new IllegalArgumentException("Must book at least 1 seat.");
        if (seats > 10) throw new IllegalArgumentException("Cannot book more than 10 seats at once.");

        Schedule schedule = scheduleDAO.findById(scheduleId);
        if (schedule == null) throw new IllegalArgumentException("Schedule not found.");
        if (!"SCHEDULED".equals(schedule.getStatus()))
            throw new IllegalArgumentException("This schedule is no longer available.");
        if (schedule.getDepartureTime().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Cannot book a past schedule.");
        if (schedule.getAvailableSeats() < seats)
            throw new IllegalArgumentException("Only " + schedule.getAvailableSeats() + " seat(s) available.");

        BigDecimal fare = BigDecimal.valueOf(schedule.getFarePerSeat() * seats);
        return bookingDAO.createBooking(userId, scheduleId, seats, fare, scheduleDAO);
    }

    // ----------------------------------------------------------------
    //  Cancel booking
    // ----------------------------------------------------------------
    public boolean cancelBooking(int bookingId, int userId) throws SQLException {
        Booking b = bookingDAO.findById(bookingId);
        if (b == null) throw new IllegalArgumentException("Booking not found.");
        if (b.getUserId() != userId)
            throw new SecurityException("You can only cancel your own bookings.");
        if ("CANCELLED".equals(b.getBookingStatus()))
            throw new IllegalArgumentException("Booking is already cancelled.");
        return bookingDAO.cancelBooking(bookingId, scheduleDAO);
    }

    // ----------------------------------------------------------------
    //  My bookings
    // ----------------------------------------------------------------
    public List<Booking> getMyBookings(int userId) throws SQLException {
        return bookingDAO.getBookingsByUser(userId);
    }

    // ----------------------------------------------------------------
    //  Single booking detail
    // ----------------------------------------------------------------
    public Booking getBookingDetail(int bookingId) throws SQLException {
        return bookingDAO.findById(bookingId);
    }

    // ----------------------------------------------------------------
    //  Search schedules
    // ----------------------------------------------------------------
    public List<Schedule> searchSchedules(String src, String dst) throws SQLException {
        if (src.isBlank() || dst.isBlank())
            throw new IllegalArgumentException("Source and destination required.");
        return scheduleDAO.searchSchedules(src, dst);
    }

    // ----------------------------------------------------------------
    //  All upcoming schedules
    // ----------------------------------------------------------------
    public List<Schedule> getAllSchedules() throws SQLException {
        return scheduleDAO.getAllUpcoming();
    }

    // ----------------------------------------------------------------
    //  Print audit log
    // ----------------------------------------------------------------
    public void printAuditLog(int bookingId) throws SQLException {
        bookingDAO.printAuditLog(bookingId);
    }
}
