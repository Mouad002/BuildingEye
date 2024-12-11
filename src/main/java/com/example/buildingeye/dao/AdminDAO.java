// Classe AdminDAO pour gérer l'accès à la base de données
package com.example.buildingeye.dao;

import com.example.buildingeye.functional.SingletonConnection;
import com.example.buildingeye.models.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    private final Connection connection;

    public AdminDAO() {
        this.connection = SingletonConnection.getConnection();
    }

    // Méthode pour vérifier si l'admin existe
    public boolean isAdminExists(String username, String password) {
        String query = "SELECT * FROM admins WHERE username = ? AND password = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
