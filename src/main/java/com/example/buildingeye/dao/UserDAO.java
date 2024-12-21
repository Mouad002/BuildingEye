package com.example.buildingeye.dao;

import com.example.buildingeye.models.User;
import com.example.buildingeye.functional.SingletonConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final Connection connection;

    public UserDAO() {
        this.connection = SingletonConnection.getConnection();
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getBytes("face_embedding")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error getting users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    public boolean addUser(User user) {
        String query = "INSERT INTO users (username, face_embedding) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, user.getUsername());
            byte[] embedding = user.getFaceEmbedding();
            System.out.println("Embedding before database insertion - Length: " + (embedding != null ? embedding.length : "null"));
            ps.setBytes(2, embedding); // Store face embedding as byte array

            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows affected by insert: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows affected by delete: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
