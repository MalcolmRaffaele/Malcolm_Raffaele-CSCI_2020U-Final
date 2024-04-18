let ws;
let currentUser = "Me"; // This should be dynamically set to the actual user's username

// Function to create a new chat room
async function newRoom() {
  let callURL = "http://localhost:8080/WSChatServer-1.0-SNAPSHOT/chat-servlet";
  try {
    let response = await fetch(callURL, { method: "GET" });
    let roomCode = await response.text();
    console.log("New room code:", roomCode);
    enterRoom(roomCode);
    fetchRooms(); // Fetch and render the updated list of rooms
    document.querySelector("#add-user").classList.add("active"); // Make the add-user form visible
  } catch (error) {
    console.error("Error creating new room:", error);
  }
}

// Function to enter a chat room
async function enterRoom(code) {
  ws = new WebSocket(
    `ws://localhost:8080/WSChatServer-1.0-SNAPSHOT/ws/${code}`
  );
  ws.onopen = () => {
    console.log("Connected to room:", code);
    fetchMessageHistory(code); // Fetch and render the message history
  };
  ws.onmessage = (event) => {
    let message = JSON.parse(event.data);
    displayMessage(message);
  };
  ws.onerror = (error) => console.error("WebSocket Error:", error);
  ws.onclose = () => console.log("Disconnected from room:", code);
}

async function fetchMessageHistory(roomCode) {
  let callURL = `http://localhost:8080/WSChatServer-1.0-SNAPSHOT/get-messages?roomCode=${roomCode}`;
  try {
    let response = await fetch(callURL, { method: "GET" });
    if (response.ok) {
      let messages = await response.json();
      clearMessages();
      messages.forEach((message) => displayMessage(message));
    } else {
      console.error("Failed to fetch message history");
    }
  } catch (error) {
    console.error("Error fetching message history:", error);
  }
}

function clearMessages() {
  document.querySelector("#chat-messages").innerHTML = "";
}

// Function to display messages in the UI, considering direction
function displayMessage(message) {
  let direction = message.username === currentUser ? "outgoing" : "incoming";
  let messagesList = document.querySelector("#chat-messages");
  let messageElement = document.createElement("li");
  messageElement.classList.add("message", direction);

  let time = message.time || new Date().toLocaleTimeString(); // Use the message's timestamp if available

  messageElement.innerHTML = `
		 <p class="meta"><strong>${message.username}</strong> <span>${time}</span></p>
		 <p class="text">${message.content}</p>
	`;
  messagesList.appendChild(messageElement);
  messagesList.scrollTop = messagesList.scrollHeight; // Scroll to the latest message
}

// Function to send a new message
function sendMessage(event) {
  event.preventDefault();
  let messageInput = document.querySelector("#message");
  if (messageInput.value.trim() === "") return;

  let message = { username: currentUser, content: messageInput.value };
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify(message));
    messageInput.value = "";
  } else {
    console.error("WebSocket is not open. Unable to send message.");
  }
}

async function createUser(username) {
  let callURL = "http://localhost:8080/WSChatServer-1.0-SNAPSHOT/create-user";
  try {
    let response = await fetch(callURL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ username: username }),
    });
    if (response.ok) {
      console.log("User created successfully");
      currentUser = username; // Update the currentUser variable

      // Hide the add-user form
      document.querySelector("#add-user").classList.remove("active");

      // Optionally, perform additional actions upon successful user creation
    } else {
      console.error("Failed to create user");
    }
  } catch (error) {
    console.error("Error creating user:", error);
  }
}

async function fetchRooms() {
  let callURL = "http://localhost:8080/WSChatServer-1.0-SNAPSHOT/rooms";
  try {
    let response = await fetch(callURL, { method: "GET" });
    let rooms = await response.json();
    renderRooms(rooms);
  } catch (error) {
    console.error("Error fetching rooms:", error);
  }
}

function renderRooms(rooms) {
  let roomList = document.querySelector("ul#room-list");
  roomList.innerHTML = ""; // Clear existing rooms

  rooms.forEach((room) => {
    let template = `<li class='room-item' data-code='${room.code}'>
            <p>
                <span class="room-name">${room.code}</span>
                <span class="user-count whisper-voice">${room.userCount}</span>
            </p>
            <button class="join-room whisper-voice">Join</button>
        </li>`;
    roomList.innerHTML += template;
  });
}

// event listeners

// create a new room
// document.querySelector("button#create-room").addEventListener("click", newRoom);

// create user

// document
//   .querySelector("form#add-user")
//   .addEventListener("submit", async (event) => {
//     event.preventDefault();
//     let usernameInput = event.target.querySelector("input#username");
//     let username = usernameInput.value.trim();
//     if (username === "") {
//       console.error("Username is required");
//       return;
//     }
//     console.log("Creating user", username);
//
//     // Call the createUser function
//     await createUser(username);
//   });
//
// // join a room
// document.querySelector("ul#room-list").addEventListener("click", (event) => {
//   if (event.target.classList.contains("join-room")) {
//     let roomCode = event.target.parentElement.dataset.code;
//     enterRoom(roomCode);
//   }
// });
//
// // send message
// document.querySelector("form#chat-box").addEventListener("submit", sendMessage);

window.onload = function () {
  let tiles = document.getElementsByClassName("board")[0].children,
      modal = document.getElementsByClassName("modal")[0],
      message = document.getElementsByClassName("message")[0],
      resetBtn = document.getElementsByClassName("reset_btn")[0],
      indicator = document.getElementsByClassName("player_indicator")[0],
      turn = 0,
      player = "",
      moves = [];

  indicator.textContent = "Player O it is your turn.";

function markBoard(e) {
  if (turn == 1 || turn % 2 !== 0) {
    player = "X";
  } else {
    player = "O";
  }
  if (!document.getElementById("token").checked && player == "O" ||
      document.getElementById("token").checked && player == "X") {

    moves.push({ tile: e.target.textContent, player: player });

    for (let i = 0; i < tiles.length; i++) {
      if (
          e.target.textContent == tiles[i].textContent &&
          tiles[i].textContent !== "X" &&
          tiles[i].textContent !== "O"
      ) {

        tiles[i].style.transition = "all 1s ease";
        tiles[i].textContent = player;
        tiles[i].classList.add("new-board_tile");
        turn++;

        if (player == "X") {
          indicator.textContent = "Player O it is your turn.";
        } else {
          indicator.textContent = "Player X it is your turn.";
        }
      }
    }
  } else {
    if (player == "X") {
      indicator.textContent = "Sorry player O it is player X's turn.";
    } else {
      indicator.textContent = "Sorry player X it is player O's turn.";
    }
  }

  if (turn >= 3) {
    determineWinner();
  }
}

function determineWinner() {
  let playerMoves = [],
      playerTiles = [],
      playerWon = false;

  playerMoves.push(moves.filter(mark => mark.player == player));

  for (let i = 0; i < playerMoves[0].length; i++) {
    playerTiles.push(playerMoves[0][i].tile);
  }

  if (
      playerTiles.includes("1") &&
      playerTiles.includes("2") &&
      playerTiles.includes("3")
  ) {
    playerWon = true;
  } else if (
      playerTiles.includes("4") &&
      playerTiles.includes("5") &&
      playerTiles.includes("6")
  ) {
    playerWon = true;
  } else if (
      playerTiles.includes("7") &&
      playerTiles.includes("8") &&
      playerTiles.includes("9")
  ) {
    playerWon = true;
  } else if (
      playerTiles.includes("1") &&
      playerTiles.includes("5") &&
      playerTiles.includes("9")
  ) {
    playerWon = true;
  } else if (
      playerTiles.includes("3") &&
      playerTiles.includes("5") &&
      playerTiles.includes("7")
  ) {
    playerWon = true;
  } else if (
      playerTiles.includes("1") &&
      playerTiles.includes("4") &&
      playerTiles.includes("7")
  ) {
    playerWon = true;
  } else if (
      playerTiles.includes("2") &&
      playerTiles.includes("5") &&
      playerTiles.includes("8")
  ) {
    playerWon = true;
  } else if (
      playerTiles.includes("3") &&
      playerTiles.includes("6") &&
      playerTiles.includes("9")
  ) {
    playerWon = true;
  } else {
    playerWon = false;
  }

  if (playerWon == true) {
    message.textContent = `Player ${player} Won!`;
    modal.style.opacity = 1;
    modal.style.display = "flex";
    modal.style.zIndex = 99;
    indicator.textContent = "";
  } else if (turn == 9) {
    message.textContent = "Tie game nobody won.";
    modal.style.opacity = 1;
    modal.style.display = "flex";
    modal.style.zIndex = 99;
    indicator.textContent = "";
  }
}

for (let i = 0; i < tiles.length; i++) {
  tiles[i].addEventListener("click", markBoard);
}

function resetBoard() {
  moves = [];
  turn = 0;
  indicator.textContent = "Player O it is your turn.";

  modal.style.opacity = 0;
  modal.style.transition = "opacity 1s ease";
  modal.style.display = "none";

  for (let i = 0; i < tiles.length; i++) {
    tiles[i].classList.remove("new-board_tile");
    tiles[i].classList.add("board_tile");
    tiles[i].style.transition = "none";
    tiles[i].textContent = [i + 1];
  }
}

resetBtn.addEventListener("click", resetBoard);
};