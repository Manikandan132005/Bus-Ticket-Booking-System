-- ============================================================
--  Bus Ticket Booking System - Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS bus_booking_db;
USE bus_booking_db;

-- -------------------------------------------------------
-- Table: users
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id    INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    phone      VARCHAR(15)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------------------------------
-- Table: buses
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS buses (
    bus_id         INT AUTO_INCREMENT PRIMARY KEY,
    bus_number     VARCHAR(20)  NOT NULL UNIQUE,
    bus_name       VARCHAR(100) NOT NULL,
    total_seats    INT          NOT NULL,
    bus_type       ENUM('AC','NON_AC','SLEEPER','SEMI_SLEEPER') NOT NULL,
    fare_per_seat  DECIMAL(10,2) NOT NULL
);

-- -------------------------------------------------------
-- Table: routes
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS routes (
    route_id    INT AUTO_INCREMENT PRIMARY KEY,
    source      VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    distance_km INT          NOT NULL
);

-- -------------------------------------------------------
-- Table: schedules
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS schedules (
    schedule_id     INT AUTO_INCREMENT PRIMARY KEY,
    bus_id          INT  NOT NULL,
    route_id        INT  NOT NULL,
    departure_time  DATETIME NOT NULL,
    arrival_time    DATETIME NOT NULL,
    available_seats INT      NOT NULL,
    status          ENUM('SCHEDULED','CANCELLED','COMPLETED') DEFAULT 'SCHEDULED',
    FOREIGN KEY (bus_id)   REFERENCES buses(bus_id)   ON DELETE CASCADE,
    FOREIGN KEY (route_id) REFERENCES routes(route_id) ON DELETE CASCADE
);

-- -------------------------------------------------------
-- Table: bookings
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS bookings (
    booking_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT            NOT NULL,
    schedule_id    INT            NOT NULL,
    seats_booked   INT            NOT NULL DEFAULT 1,
    total_fare     DECIMAL(10,2)  NOT NULL,
    booking_status ENUM('CONFIRMED','CANCELLED','PENDING') DEFAULT 'CONFIRMED',
    booked_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)     REFERENCES users(user_id)         ON DELETE CASCADE,
    FOREIGN KEY (schedule_id) REFERENCES schedules(schedule_id) ON DELETE CASCADE
);

-- -------------------------------------------------------
-- Table: booking_audit  (tracks every status change)
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS booking_audit (
    audit_id    INT AUTO_INCREMENT PRIMARY KEY,
    booking_id  INT          NOT NULL,
    old_status  VARCHAR(20),
    new_status  VARCHAR(20)  NOT NULL,
    changed_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    remarks     VARCHAR(255),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
);

-- ============================================================
--  Seed Data
-- ============================================================

INSERT INTO buses (bus_number, bus_name, total_seats, bus_type, fare_per_seat) VALUES
('TN01AB1234', 'Chennai Express',  40, 'AC',          850.00),
('TN02CD5678', 'Coimbatore Rider', 45, 'NON_AC',      450.00),
('TN03EF9012', 'Night Cruiser',    36, 'SLEEPER',     1100.00),
('TN04GH3456', 'Metro Link',       50, 'SEMI_SLEEPER', 650.00);

INSERT INTO routes (source, destination, distance_km) VALUES
('Chennai',    'Coimbatore', 500),
('Chennai',    'Madurai',    460),
('Coimbatore', 'Madurai',    210),
('Chennai',    'Bangalore',  350);

INSERT INTO schedules (bus_id, route_id, departure_time, arrival_time, available_seats, status) VALUES
(1, 1, DATE_ADD(NOW(), INTERVAL  1 DAY) + INTERVAL  6 HOUR, DATE_ADD(NOW(), INTERVAL  1 DAY) + INTERVAL 14 HOUR, 40, 'SCHEDULED'),
(2, 2, DATE_ADD(NOW(), INTERVAL  1 DAY) + INTERVAL  8 HOUR, DATE_ADD(NOW(), INTERVAL  1 DAY) + INTERVAL 16 HOUR, 45, 'SCHEDULED'),
(3, 3, DATE_ADD(NOW(), INTERVAL  2 DAY) + INTERVAL 22 HOUR, DATE_ADD(NOW(), INTERVAL  3 DAY) + INTERVAL  6 HOUR, 36, 'SCHEDULED'),
(4, 4, DATE_ADD(NOW(), INTERVAL  1 DAY) + INTERVAL  7 HOUR, DATE_ADD(NOW(), INTERVAL  1 DAY) + INTERVAL 13 HOUR, 50, 'SCHEDULED'),
(1, 2, DATE_ADD(NOW(), INTERVAL  3 DAY) + INTERVAL  9 HOUR, DATE_ADD(NOW(), INTERVAL  3 DAY) + INTERVAL 17 HOUR, 40, 'SCHEDULED');
