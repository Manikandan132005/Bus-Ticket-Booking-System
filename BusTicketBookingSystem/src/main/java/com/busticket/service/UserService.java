package com.busticket.service;

import com.busticket.dao.UserDAO;
import com.busticket.model.User;

import java.sql.SQLException;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public User register(String name, String email, String phone, String password) throws SQLException {
        if (userDAO.emailExists(email))
            throw new IllegalArgumentException("Email already registered: " + email);
        if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank())
            throw new IllegalArgumentException("All fields are required.");
        if (!email.contains("@"))
            throw new IllegalArgumentException("Invalid email format.");
        if (phone.length() < 10)
            throw new IllegalArgumentException("Phone must be at least 10 digits.");

        User user = new User(name, email, phone, password);
        boolean ok = userDAO.registerUser(user);
        if (!ok) throw new SQLException("Registration failed.");
        return user;
    }

    public User login(String email, String password) throws SQLException {
        if (email.isBlank() || password.isBlank())
            throw new IllegalArgumentException("Email and password required.");
        User user = userDAO.login(email, password);
        if (user == null)
            throw new IllegalArgumentException("Invalid email or password.");
        return user;
    }

    public User getProfile(int userId) throws SQLException {
        return userDAO.findById(userId);
    }

    public boolean updateProfile(User user) throws SQLException {
        return userDAO.updateUser(user);
    }
}
