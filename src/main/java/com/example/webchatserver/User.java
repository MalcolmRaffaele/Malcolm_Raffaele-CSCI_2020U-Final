package com.example.webchatserver;

import jakarta.websocket.Session;

public class User {
    private String id; // Typically associated with the WebSocket session ID
    private String username; // The display name chosen by the user
    private Session session; // The WebSocket session between the server and the user's client

    // Constructor that initializes the user with their ID, username, and session
    public User(String id, String username, Session session) {
        this.id = id;
        this.username = username;
        this.session = session;
    }

    // Getter for the user's ID
    public String getId() {
        return id;
    }

    // Setter for the user's ID
    public void setId(String id) {
        this.id = id;
    }

    // Getter for the user's username
    public String getUsername() {
        return username;
    }

    // Setter for the user's username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for the user's WebSocket session
    public Session getSession() {
        return session;
    }

    // Setter for the user's WebSocket session
    public void setSession(Session session) {
        this.session = session;
    }
}
