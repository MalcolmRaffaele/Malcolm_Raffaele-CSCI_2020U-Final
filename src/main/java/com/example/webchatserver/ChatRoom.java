package com.example.webchatserver;

import org.apache.commons.lang3.RandomStringUtils;
import jakarta.websocket.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoom {
    private String code; // Unique identifier for the chat room
    private Map<String, User> users = new HashMap<>(); // Map of user IDs to User objects
    private List<Message> messages = new ArrayList<>(); // List of Message objects

    // Static map to keep track of all existing chat room codes to ensure uniqueness
    private static final Map<String, Boolean> existingCodes = new HashMap<>();

    public ChatRoom() {
        this.code = generateUniqueRoomCode();
    }

    // Adjusted to include Session in addUser
    public void addUser(String id, String username, Session session) {
        if (!users.containsKey(id)) {
            users.put(id, new User(id, username, session));
        }
    }

    public void removeUser(String id) {
        users.remove(id);
    }

    public void updateUsername(String id, String newUsername) {
        User user = users.get(id);
        if (user != null) {
            user.setUsername(newUsername);
        }
    }

    public void addMessage(String username, String content) {
		if (!"Server".equals(username)) { // Check if the username is not "Server"
		messages.add(new Message(username, content));
  }
    }

    private String generateUniqueRoomCode() {
        String code;
        do {
            code = RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        } while (existingCodes.containsKey(code));
        existingCodes.put(code, true);
        return code;
    }

    public List<String> getMessagesJson() {
        List<String> jsonMessages = new ArrayList<>();
        for (Message message : messages) {
            jsonMessages.add(message.toJson());
        }
        return jsonMessages;
    }

    // Getters
    public String getCode() {
        return code;
    }

    public Map<String, User> getUsers() {
        return new HashMap<>(users);
    }

    public User getUser(String id) {
        return users.get(id);
    }

	 public List<Message> getMessages() {
		  return new ArrayList<>(messages);
	 }

	 
}
