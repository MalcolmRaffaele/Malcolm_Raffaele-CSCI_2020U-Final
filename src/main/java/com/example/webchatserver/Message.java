package com.example.webchatserver;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private String username;
    private String content;
    private String timestamp;

    public Message(String username, String content) {
        this.username = username;
        this.content = content;
        // Set the timestamp to the current time when the message is created
        this.timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    // Convert the message to a JSON-like string for easy parsing and broadcasting
    public String toJson() {
        return "{" +
                "\"username\":\"" + username + '\"' +
                ", \"content\":\"" + content + '\"' +
                ", \"timestamp\":\"" + timestamp + '\"' +
                '}';
    }
}
