package com.example.buildingeye.dao;

import com.example.buildingeye.models.User;
import com.example.buildingeye.functional.SingletonConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {

    private final Connection connection;

    public UserDAO() {
        this.connection = SingletonConnection.getConnection();
    }

    public boolean addUser(User user) {
        String query = "INSERT INTO users (username, face_embedding) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, user.getUsername());
            ps.setBytes(2, user.getFaceEmbedding()); // Store face embedding as byte array

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
