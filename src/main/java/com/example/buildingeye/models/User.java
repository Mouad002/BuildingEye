package com.example.buildingeye.models;

public class User {
    private int id;
    private String username;
    private byte[] faceEmbedding; // Store as byte array

    public User(int id, String username, byte[] faceEmbedding) {
        this.id = id;
        this.username = username;
        this.faceEmbedding = faceEmbedding;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public byte[] getFaceEmbedding() { return faceEmbedding; }
    public void setFaceEmbedding(byte[] faceEmbedding) { this.faceEmbedding = faceEmbedding; }

}
