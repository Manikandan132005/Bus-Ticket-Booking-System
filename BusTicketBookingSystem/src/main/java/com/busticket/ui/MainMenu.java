package com.busticket.ui;

import com.busticket.model.Booking;
import com.busticket.model.Schedule;
import com.busticket.model.User;
import com.busticket.service.BookingService;
import com.busticket.service.UserService;
import com.busticket.util.ConsoleUtil;
import com.busticket.util.DBConnection;

import java.sql.SQLException;
import java.util.List;
public class MainMenu {

    private final UserService    userService    = new UserService();
    private final BookingService bookingService = new BookingService();
    private User loggedInUser = null;

    public void start() {
        ConsoleUtil.printBanner();
        boolean running = true;
        while (running) {
            try {
                if (loggedInUser == null) running = guestMenu();
                else                      running = userMenu();
            } catch (Exception e) {
                ConsoleUtil.error("Unexpected error: " + e.getMessage());
            }
        }
        DBConnection.closeConnection();
        System.out.println();
        ConsoleUtil.success("Thank you for using Bus Ticket Booking System. Goodbye!");
    }

    // ==============================================================
    //  GUEST MENU
    // ==============================================================
    private boolean guestMenu() throws SQLException {
        ConsoleUtil.section("MAIN MENU");
        System.out.println("  1. Register");
        System.out.println("  2. Login");
        System.out.println("  3. View Upcoming Schedules");
        System.out.println("  4. Exit");
        ConsoleUtil.divider();
        int choice = ConsoleUtil.readInt("Enter choice: ", 1, 4);

        switch (choice) {
            case 1 -> registerFlow();
            case 2 -> loginFlow();
            case 3 -> viewAllSchedules();
            case 4 -> { return false; }
        }
        return true;
    }

    // ==============================================================
    //  USER MENU
    // ==============================================================
    private boolean userMenu() throws SQLException {
        ConsoleUtil.section("WELCOME, " + loggedInUser.getName().toUpperCase());
        System.out.println("  1. Search Buses");
        System.out.println("  2. View All Upcoming Buses");
        System.out.println("  3. Book Ticket");
        System.out.println("  4. My Bookings");
        System.out.println("  5. Cancel Booking");
        System.out.println("  6. View Booking Audit Log");
        System.out.println("  7. My Profile");
        System.out.println("  8. Logout");
        ConsoleUtil.divider();
        int choice = ConsoleUtil.readInt("Enter choice: ", 1, 8);

        switch (choice) {
            case 1 -> searchBuses();
            case 2 -> viewAllSchedules();
            case 3 -> bookTicketFlow();
            case 4 -> myBookings();
            case 5 -> cancelBookingFlow();
            case 6 -> auditLogFlow();
            case 7 -> myProfile();
            case 8 -> { loggedInUser = null; ConsoleUtil.info("Logged out."); }
        }
        return true;
    }

    // ==============================================================
    //  FLOWS
    // ==============================================================

    private void registerFlow() throws SQLException {
        ConsoleUtil.section("REGISTER");
        String name     = ConsoleUtil.readString("  Full Name   : ");
        String email    = ConsoleUtil.readString("  Email       : ");
        String phone    = ConsoleUtil.readString("  Phone       : ");
        String password = ConsoleUtil.readString("  Password    : ");
        try {
            User u = userService.register(name, email, phone, password);
            ConsoleUtil.success("Registered successfully! User ID: " + u.getUserId());
        } catch (IllegalArgumentException e) {
            ConsoleUtil.error(e.getMessage());
        }
    }

    private void loginFlow() throws SQLException {
        ConsoleUtil.section("LOGIN");
        String email    = ConsoleUtil.readString("  Email    : ");
        String password = ConsoleUtil.readString("  Password : ");
        try {
            loggedInUser = userService.login(email, password);
            ConsoleUtil.success("Welcome back, " + loggedInUser.getName() + "!");
        } catch (IllegalArgumentException e) {
            ConsoleUtil.error(e.getMessage());
        }
    }

    private void viewAllSchedules() throws SQLException {
        ConsoleUtil.section("UPCOMING SCHEDULES");
        List<Schedule> schedules = bookingService.getAllSchedules();
        if (schedules.isEmpty()) {
            ConsoleUtil.info("No upcoming schedules found.");
        } else {
            schedules.forEach(s -> System.out.println("  " + s));
        }
    }

    private void searchBuses() throws SQLException {
        ConsoleUtil.section("SEARCH BUSES");
        String src = ConsoleUtil.readString("  From (Source)      : ");
        String dst = ConsoleUtil.readString("  To   (Destination) : ");
        try {
            List<Schedule> results = bookingService.searchSchedules(src, dst);
            if (results.isEmpty()) {
                ConsoleUtil.info("No buses found for " + src + " → " + dst);
            } else {
                System.out.println();
                results.forEach(s -> System.out.println("  " + s));
            }
        } catch (IllegalArgumentException e) {
            ConsoleUtil.error(e.getMessage());
        }
    }

    private void bookTicketFlow() throws SQLException {
        ConsoleUtil.section("BOOK TICKET");
        viewAllSchedules();
        System.out.println();
        int scheduleId = ConsoleUtil.readPositiveInt("  Enter Schedule ID : ");
        int seats      = ConsoleUtil.readInt("  Number of Seats   : ", 1, 10);
        try {
            Booking b = bookingService.bookTickets(loggedInUser.getUserId(), scheduleId, seats);
            System.out.println();
            ConsoleUtil.success("Booking Confirmed!");
            System.out.printf("  %-20s : #%d%n",  "Booking ID",     b.getBookingId());
            System.out.printf("  %-20s : %d%n",   "Seats Booked",   b.getSeatsBooked());
            System.out.printf("  %-20s : ₹%.2f%n","Total Fare",     b.getTotalFare());
            System.out.printf("  %-20s : %s%n",   "Status",         b.getBookingStatus());
        } catch (IllegalArgumentException | SecurityException e) {
            ConsoleUtil.error(e.getMessage());
        }
    }

    private void myBookings() throws SQLException {
        ConsoleUtil.section("MY BOOKINGS");
        List<Booking> bookings = bookingService.getMyBookings(loggedInUser.getUserId());
        if (bookings.isEmpty()) {
            ConsoleUtil.info("You have no bookings yet.");
        } else {
            bookings.forEach(b -> System.out.println("  " + b));
        }
    }

    private void cancelBookingFlow() throws SQLException {
        ConsoleUtil.section("CANCEL BOOKING");
        myBookings();
        System.out.println();
        int bookingId = ConsoleUtil.readPositiveInt("  Enter Booking ID to cancel : ");
        try {
            boolean ok = bookingService.cancelBooking(bookingId, loggedInUser.getUserId());
            if (ok) ConsoleUtil.success("Booking #" + bookingId + " cancelled. Seats returned.");
            else    ConsoleUtil.error("Cancellation failed.");
        } catch (IllegalArgumentException | SecurityException e) {
            ConsoleUtil.error(e.getMessage());
        }
    }

    private void auditLogFlow() throws SQLException {
        ConsoleUtil.section("BOOKING AUDIT LOG");
        int bookingId = ConsoleUtil.readPositiveInt("  Enter Booking ID : ");
        try {
            bookingService.printAuditLog(bookingId);
        } catch (Exception e) {
            ConsoleUtil.error(e.getMessage());
        }
    }

    private void myProfile() throws SQLException {
        ConsoleUtil.section("MY PROFILE");
        User u = userService.getProfile(loggedInUser.getUserId());
        if (u == null) { ConsoleUtil.error("Profile not found."); return; }
        System.out.printf("  %-15s : %d%n",  "User ID",    u.getUserId());
        System.out.printf("  %-15s : %s%n",  "Name",       u.getName());
        System.out.printf("  %-15s : %s%n",  "Email",      u.getEmail());
        System.out.printf("  %-15s : %s%n",  "Phone",      u.getPhone());
        System.out.printf("  %-15s : %s%n",  "Joined",     u.getCreatedAt());
    }


}
