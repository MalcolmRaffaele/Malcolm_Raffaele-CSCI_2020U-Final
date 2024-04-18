package com.example.webchatserver;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/ws/{roomID}")
public class ChatServer {

    private static final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    @OnOpen
    public void open(@PathParam("roomID") String roomID, Session session) {
        ChatRoom chatRoom = chatRooms.get(roomID);
        if (chatRoom == null) {
            // Inform the user that the room does not exist or handle accordingly
            try {
                session.getBasicRemote().sendText("Room does not exist.");
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Room does not exist."));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        String userId = session.getId();
        // Add user with the session to the chat room
        chatRoom.addUser(userId, "Anonymous", session); // Updated to include the session

        // Broadcast to the room that a new user has joined
      //   broadcastMessage(chatRoom.getCode(), new Message("Server", "A new user has joined the chat room.").toJson());
    }

    @OnClose
    public void close(Session session, @PathParam("roomID") String roomID) {
        ChatRoom chatRoom = chatRooms.get(roomID);
        if (chatRoom != null) {
            String userId = session.getId();
            chatRoom.removeUser(userId);

            // Broadcast that a user has left
            broadcastMessage(roomID, new Message("Server", "A user has left the chat room.").toJson());
        }
    }

    @OnMessage
    public void handleMessage(String message, Session session, @PathParam("roomID") String roomID) {
        ChatRoom chatRoom = chatRooms.get(roomID);
        if (chatRoom != null) {
            String userId = session.getId();
            User user = chatRoom.getUser(userId);

            JSONObject jsonMessage = new JSONObject(message);
            String content = jsonMessage.getString("content");
            Message newMessage = new Message(user.getUsername(), content);

            chatRoom.addMessage(newMessage.getUsername(), newMessage.getContent());
            broadcastMessage(roomID, newMessage.toJson());
        }
    }

    private void broadcastMessage(String roomID, String messageJson) {
		ChatRoom chatRoom = chatRooms.get(roomID);
		if (chatRoom != null) {
			 JSONObject jsonMessage = new JSONObject(messageJson);
			 String username = jsonMessage.getString("username");
			 String content = jsonMessage.getString("content");
			 chatRoom.addMessage(username, content); // Use ChatRoom's addMessage to add the message
  
			 chatRoom.getUsers().values().forEach(user -> {
				  try {
						Session userSession = user.getSession();
						if (userSession.isOpen()) {
							 userSession.getBasicRemote().sendText(messageJson);
						}
				  } catch (IOException e) {
						e.printStackTrace();
				  }
			 });
		}
  }

    // Method to add a new chat room to the server
    public static void addRoom(ChatRoom chatRoom) {
        chatRooms.put(chatRoom.getCode(), chatRoom);
    }

	 // Method to get all rooms
    public static Collection<ChatRoom> getRooms() {
        return chatRooms.values();
    }

	 public static ChatRoom getRoomByCode(String roomCode) {
		  return chatRooms.get(roomCode);
	 }


}
