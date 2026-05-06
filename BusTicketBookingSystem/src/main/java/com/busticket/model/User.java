package com.busticket.model;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private String password;
    private LocalDateTime createdAt;

    public User() {}

    public User(String name, String email, String phone, String password) {
        this.name     = name;
        this.email    = email;
        this.phone    = phone;
        this.password = password;
    }

    // Getters & Setters
    public int    getUserId()   { return userId; }
    public void   setUserId(int userId) { this.userId = userId; }

    public String getName()    { return name; }
    public void   setName(String name) { this.name = name; }

    public String getEmail()   { return email; }
    public void   setEmail(String email) { this.email = email; }

    public String getPhone()   { return phone; }
    public void   setPhone(String phone) { this.phone = phone; }

    public String getPassword()  { return password; }
    public void   setPassword(String p) { this.password = p; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }

    @Override
    public String toString() {
        return String.format("[User #%d] %s | %s | %s", userId, name, email, phone);
    }
}
