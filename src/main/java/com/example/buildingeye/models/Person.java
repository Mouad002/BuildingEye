package com.example.buildingeye.models;

public class Person {
    private int id;
    private String username;
    private byte[] embeddings;
    private boolean authorized;

    public Person() {}

    public Person(int id, String username, byte[] embeddings, boolean authorized) {
        this.id = id;
        this.username = username;
        this.embeddings = embeddings;
        this.authorized = authorized;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getEmbeddings() {
        return embeddings;
    }

    public void setEmbeddings(byte[] embeddings) {
        this.embeddings = embeddings;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}
