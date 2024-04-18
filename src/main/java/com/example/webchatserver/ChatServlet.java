package com.example.webchatserver;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.concurrent.ConcurrentHashMap;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

/**
 * Servlet that handles both creating new chat rooms and listing existing rooms.
 */
@WebServlet(name = "ChatServlet", urlPatterns = {"/chat-servlet", "/rooms", "/create-user", "/get-messages"})


public class ChatServlet extends HttpServlet {

	private static final ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Determine the action based on the URL pattern that was requested
        String path = request.getServletPath();
        
        if ("/chat-servlet".equals(path)) {
            createChatRoom(response);
        } else if ("/rooms".equals(path)) {
            listChatRooms(response);
        } else if ("/get-messages".equals(path)) {
			getMessageHistory(request, response);
	  }
    }

	 @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Determine the action based on the URL pattern that was requested
        String path = request.getServletPath();
        
        if ("/create-user".equals(path)) {
            createUser(request, response);
        }
    }

    private void createChatRoom(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setHeader("Access-Control-Allow-Origin", "*");

        // Create a new chat room
        ChatRoom chatRoom = new ChatRoom(); // This automatically generates a unique room code
        String roomCode = chatRoom.getCode();

        // Add the new chat room to the server's list of chat rooms
        ChatServer.addRoom(chatRoom);

        // Send the room's unique code as the response's content
        PrintWriter out = response.getWriter();
        out.println(roomCode);
    }

    private void listChatRooms(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Collection<ChatRoom> rooms = ChatServer.getRooms();
        JSONArray roomsArray = new JSONArray();

        for (ChatRoom room : rooms) {
            JSONObject roomJson = new JSONObject();
            roomJson.put("code", room.getCode());
            roomJson.put("userCount", room.getUsers().size());
            roomsArray.put(roomJson);
        }

        PrintWriter out = response.getWriter();
        out.write(roomsArray.toString());
    }

	 private void createUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		StringBuilder jsonBuilder = new StringBuilder();
		String line;
		try (BufferedReader reader = request.getReader()) {
			 while ((line = reader.readLine()) != null) {
				  jsonBuilder.append(line);
			 }
		}
		String json = jsonBuilder.toString();
		JSONObject jsonObject = new JSONObject(json);
		String username = jsonObject.getString("username");
  
		// Here, simply add the username to the map for demonstration purposes
		// In a real application, you'd likely be adding this to a database
		// and handling it according to your application's logic
		users.put(username, "Active"); // Example status to indicate the user is active
  
		// Respond to the client
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject responseJson = new JSONObject();
		responseJson.put("status", "success");
		responseJson.put("username", username);
		out.write(responseJson.toString());
  }

	 private void getMessageHistory(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String roomCode = request.getParameter("roomCode");
		// Retrieve the ChatRoom instance by its code
		ChatRoom chatRoom = ChatServer.getRoomByCode(roomCode); // Assuming ChatServer has a method to get rooms by code
  
		if (chatRoom == null) {
			 response.sendError(HttpServletResponse.SC_NOT_FOUND, "Room not found");
			 return;
		}
  
		List<Message> messages = chatRoom.getMessages(); // Call getMessages on the ChatRoom instance
  
		JSONArray messagesArray = new JSONArray();
		for (Message message : messages) {
			 JSONObject messageJson = new JSONObject();
			 messageJson.put("username", message.getUsername());
			 messageJson.put("content", message.getContent());
			 messageJson.put("time", message.getTimestamp()); // Assuming you have a timestamp for messages
			 messagesArray.put(messageJson);
		}
  
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(messagesArray.toString());
  
}
}