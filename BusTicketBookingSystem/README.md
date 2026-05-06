# 🚌 Bus Ticket Booking System
**Tech Stack:** Java 21 · JDBC · MySQL

## Project Structure
~~~
BusTicketBookingSystem/
├── pom.xml
├── sql/
│   └── schema.sql                  ← DB schema + seed data
└── src/main/
    ├── resources/
    │   └── db.properties           ← DB credentials
    └── java/com/busticket/
        ├── App.java                ← Entry point
        ├── model/
        │   ├── User.java
        │   ├── Bus.java
        │   ├── Route.java
        │   ├── Schedule.java
        │   └── Booking.java
        ├── dao/
        │   ├── UserDAO.java        ← JDBC queries for users
        │   ├── ScheduleDAO.java    ← JDBC queries for schedules
        │   └── BookingDAO.java     ← Transactional booking + audit
        ├── service/
        │   ├── UserService.java    ← Business logic: register / login
        │   └── BookingService.java ← Business logic: book / cancel / search
        ├── util/
        │   ├── DBConnection.java   ← Singleton JDBC connection manager
        │   └── ConsoleUtil.java    ← Coloured console helpers
        └── ui/
            └── MainMenu.java       ← Interactive console menu
```

---

## Database Tables

| Table            | Purpose                                     |
|------------------|---------------------------------------------|
| `users`          | Registered passengers                       |
| `buses`          | Bus master data (name, type, fare)          |
| `routes`         | Source → Destination routes                 |
| `schedules`      | Bus schedule with available seat tracking   |
| `bookings`       | Confirmed / cancelled bookings              |
| `booking_audit`  | Audit trail of every booking status change  |

---

## Setup & Run

### 1 – Database
```sql
-- in MySQL Workbench / CLI
SOURCE /path/to/sql/schema.sql;
```

### 2 – Configure credentials
Edit `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/bus_booking_db?useSSL=false&serverTimezone=UTC
db.username=root
db.password=yourpassword
```

### 3a – Run with Maven (recommended)
```bash
mvn clean package -q
java -jar target/bus-ticket-booking-1.0.0-jar-with-dependencies.jar
```

### 3b – Run manually (without Maven)
```bash
# Download mysql-connector-j-8.3.0.jar and place in lib/
javac -cp "lib/mysql-connector-j-8.3.0.jar" -d out \
      $(find src -name "*.java")
cp src/main/resources/db.properties out/
java -cp "out:lib/mysql-connector-j-8.3.0.jar" com.busticket.App
```

## Features

| Feature                  | Details                                                       |
|--------------------------|---------------------------------------------------------------|
| **User Registration**    | Validates email uniqueness, phone length                      |
| **Secure Login**         | Email + password authentication                               |
| **Bus Search**           | Filter by source & destination, shows only future schedules   |
| **Ticket Booking**       | ACID transaction: seat decrement + booking + audit in one TX  |
| **Booking Cancellation** | Restores seats atomically; records audit entry                |
| **Audit Log**            | Every status change stored in `booking_audit`                 |
| **Real-time Seat Count** | `available_seats` updated immediately on book / cancel        |

## Key Design Decisions

- **Singleton DBConnection** – one connection object reused across DAOs.
- **JDBC Transactions** – `setAutoCommit(false)` wraps seat-decrement + booking insert + audit insert so they either all succeed or all roll back.
- **Layered Architecture** – `ui → service → dao → db`, keeping SQL out of the UI.
- **Audit Trail** – `booking_audit` table captures every old/new status with a timestamp, enabling full history of changes.
