package com.busticket;

import com.busticket.ui.MainMenu;

/**
 * Bus Ticket Booking System
 * ──────────────────────────
 * Tech Stack : Java 17+  |  JDBC  |  MySQL
 *
 * Run steps:
 *   1. Create DB & run sql/schema.sql
 *   2. Set DB credentials in src/main/resources/db.properties
 *   3. Add mysql-connector-j-*.jar to classpath
 *   4. javac  all .java files, then  java com.busticket.App
 */
public class App {
    public static void main(String[] args) {
        MainMenu menu=new MainMenu();
        menu.start();
    }
}
