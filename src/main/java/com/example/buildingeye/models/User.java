package com.example.buildingeye.models;

/**
 * Represents a user with an ID, username, and face embedding.
 */
public class User {
    private int id;
    private String username;
    private byte[] faceEmbedding; // Store as byte array

    /**
     * Constructs a new User with the given ID, username, and face embedding.
     *
     * @param id           the ID of the user
     * @param username     the username of the user
     * @param faceEmbedding the face embedding of the user
     */
    public User(int id, String username, byte[] faceEmbedding) {
        this.id = id;
        this.username = username;
        this.faceEmbedding = faceEmbedding;
    }

    /**
     * Constructs a new User with the given username and face embedding.
     *
     * @param username     the username of the user
     * @param faceEmbedding the face embedding of the user
     */
    public User(String username, byte[] faceEmbedding) {
        this.username = username;
        this.faceEmbedding = faceEmbedding;
    }

    // Getters and setters
    /**
     * Gets the ID of the user.
     *
     * @return the ID of the user
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the user.
     *
     * @param id the new ID of the user
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the username of the user.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username the new username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the face embedding of the user.
     *
     * @return the face embedding of the user
     */
    public byte[] getFaceEmbedding() {
        return faceEmbedding;
    }

    /**
     * Sets the face embedding of the user.
     *
     * @param faceEmbedding the new face embedding of the user
     */
    public void setFaceEmbedding(byte[] faceEmbedding) {
        this.faceEmbedding = faceEmbedding;
    }
}
