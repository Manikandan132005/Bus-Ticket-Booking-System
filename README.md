# 🚌 Bus Ticket Booking System
**Tech Stack:** Java 21 · JDBC · MySQL


## Database Tables

| Table            | Purpose                                     |
|------------------|---------------------------------------------|
| `users`          | Registered passengers                       |
| `buses`          | Bus master data (name, type, fare)          |
| `routes`         | Source → Destination routes                 |
| `schedules`      | Bus schedule with available seat tracking   |
| `bookings`       | Confirmed / cancelled bookings              |
| `booking_audit`  | Audit trail of every booking status change  |


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
