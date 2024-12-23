package com.example.buildingeye.functional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class SingletonConnection {
    private static final Connection connection;

    static {
        try {
            Properties properties = MyConfig.loadConfig();
            String url = properties.getProperty("db.url");
            String username = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");
            connection = DriverManager.getConnection("jdbc:sqlite:buildingeye.db");
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void destroyConnection() throws SQLException{
        connection.close();
    }

    private static void createTables() {
        String query1 = "create table if not exists admins(id integer not null primary key autoincrement, username text not null, password text not null);";
        String query2 = "create table if not exists users(id integer not null primary key autoincrement, username text not null, face_embedding blob not null, created_at integer null, updated_at integer null, deleted_at integer null)";
        String query3 = "insert into admins(id, username, password) values(1, 'admin', 'admin');";
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement(query1);
            ps.executeUpdate();
            ps = connection.prepareStatement(query2);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement(query3);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("user already inserted");
        }
    }
}
