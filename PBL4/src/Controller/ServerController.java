package Controller;
import Model.GameRoom;
import Model.Player;
import Model.Room;
import Model.History;
import Model.DatabaseManager;

import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerController {
    private final DatabaseManager databaseManager;
    private final HashMap<String, Player> onlinePlayers; // Quản lý người chơi đang online
    private final RoomController roomController;
    private final Map<String, PrintWriter> playerWriters;
    
    public ServerController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.onlinePlayers = new HashMap<>();
        this.roomController = new RoomController();
        this.playerWriters = new HashMap<>();
    }

    public void handleClient(Socket clientSocket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            
            String request;
            while ((request = reader.readLine()) != null) {
                processRequest(request, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private void processRequest(String request, PrintWriter writer){
        System.out.println("Received request: " + request);
        if (request.startsWith("CHECK_USERNAME")) {
            handleCheckUsername(request, writer);
        } else if (request.startsWith("REGISTER")) {
            handleRegister(request, writer);
        } else if (request.startsWith("LOGIN")) {
            handleLogin(request, writer);
        } else if (request.startsWith("LOGOUT")) {
            handleLogout(request, writer);
        } else if (request.startsWith("GET_PLAYER_STATS")) {
            handleGetPlayerStats(request, writer);
        } else if (request.startsWith("UPDATE_PLAYER_STATS")) {
            handleUpdatePlayerStats(request, writer);
        }else if (request.startsWith("GET_ROOMS")) {
            handleGetRooms(writer);
        } else if (request.startsWith("JOIN_ROOM")) {
            handleJoinRoom(request, writer);
        } else if (request.startsWith("CHECK_ROOM")) {
            handleCheckRoom(request, writer);
        } else if (request.startsWith("START_GAME")) {
            handleStartGame(request, writer);
        } else if (request.startsWith("LEAVE_ROOM")) {
            handleLeaveRoom(request, writer);
        } else if (request.startsWith("SAVE_HISTORY")) {
            handleSaveHistory(request, writer);
        } else if (request.startsWith("GET_HISTORY")) {
            handleGetHistory(request, writer);
        } else if (request.startsWith("MOVE")){
            handleMove(request, writer);
        } else if (request.startsWith("EXIT_GAME")){
            handleExitGame(request, writer);
        } else {
            writer.println("UNKNOWN_COMMAND");
            System.out.println("Unknown command received: " + request);
        }
    }
    
    // ---------- Các phương thức xử lý người dùng ----------
    private void handleCheckUsername(String request, PrintWriter writer) {
        String[] parts = request.split(" ");
        if (parts.length < 2) {
            writer.println("CHECK_USERNAME_FAILURE Invalid format");
            return;
        }
        String username = parts[1];

        if (databaseManager.usernameExists(username)) {
            writer.println("USERNAME_EXISTS");
        } else {
            writer.println("USERNAME_OK");
        }
    }

    private void handleRegister(String request, PrintWriter writer) {
        String[] parts = request.split(" ");
        if (parts.length < 3) {
            writer.println("REGISTER_FAILURE Invalid format");
            return;
        }
        String username = parts[1];
        String password = parts[2];

        if (!databaseManager.usernameExists(username)) {
            boolean success = databaseManager.registerUser(username, password);
            if (success) {
                writer.println("REGISTER_SUCCESS");
                System.out.println("User registered successfully: " + username);
            } else {
                writer.println("REGISTER_FAILURE Could not register user");
            }
        } else {
            writer.println("REGISTER_FAILURE Username already exists");
        }
    }

    private void handleLogin(String request, PrintWriter writer) {
        String[] parts = request.split(" ");
        if (parts.length < 3) {
            writer.println("LOGIN_FAILURE Invalid format");
            return;
        }
        String username = parts[1];
        String password = parts[2];

        Player player = databaseManager.getUserByUsername(username);

        if (player != null && player.getPassword().equals(password)) {
            if (onlinePlayers.containsKey(username)) {
                writer.println("LOGIN_FAILURE Already logged in");
            } else {
                onlinePlayers.put(username, player); // Mark player as online
                playerWriters.put(username, writer); // Store player's PrintWriter
                writer.println("LOGIN_SUCCESS " + username);
                System.out.println("User logged in: " + username);
            }
        } else {
            writer.println("LOGIN_FAILURE Invalid credentials");
        }
    }

    private void handleLogout(String request, PrintWriter writer) {
        String[] parts = request.split(" ");
        if (parts.length < 2) {
            writer.println("LOGOUT_FAILURE Invalid format");
            return;
        }
        String username = parts[1];

        if (onlinePlayers.containsKey(username)) {
            onlinePlayers.remove(username);
            writer.println("LOGOUT_SUCCESS");
            System.out.println("User logged out: " + username);
        } else {
            writer.println("LOGOUT_FAILURE Player not logged in");
        }
    }

    // ---------- Các phương thức xử lý phòng (Room) ----------
    private void handleGetRooms(PrintWriter writer) {
         // Gửi trạng thái tất cả các phòng hiện có
        roomController.logRoomsState();
        writer.println("GET_ROOMS_SUCCESS");
    }

    private void handleJoinRoom(String request, PrintWriter writer) {
        String[] parts = request.split(" ");
        if (parts.length < 2) {
            writer.println("JOIN_ROOM_FAILURE Invalid format");
            return;
        }
        String username = parts[1];
        synchronized (roomController) {
            GameRoom gameRoom = roomController.getOrCreateRoom();
            Player player = onlinePlayers.get(username);

            if (player == null) {
                writer.println("JOIN_ROOM_FAILURE Player not logged in");
                return;
            }

            if (gameRoom.addPlayer(player)) {
                writer.println("JOIN_ROOM_SUCCESS " + gameRoom.getRoomId());
                System.out.println(username + " joined room " + gameRoom.getRoomId());

                // Check if the room is ready to start
                if (gameRoom.isFull() && !gameRoom.isActive()) {
                    gameRoom.startGame();
                    notifyPlayersInRoom(gameRoom, "GAME_STARTED " + gameRoom.getNumbersAsString());
                }
            } else {
                writer.println("JOIN_ROOM_FAILURE Room is full");
            }
        }
    }

    private void handleCheckRoom(String request, PrintWriter writer){
        String[] parts = request.split(" ");
        if (parts.length < 4) {
            writer.println("CHECK_ROOM_FAILURE Invalid format");
            return;
        }

        String username = parts[1];
        int idRoom = Integer.parseInt(parts[2]);
        String nameRoom = parts[3];
        Player player = onlinePlayers.get(username);
        
        // Kiểm tra phòng trong cơ sở dữ liệu
        
        Room room = databaseManager.getRoomStatus(idRoom, nameRoom);
        if (room == null) {
            // Create new room
            room = new Room(idRoom, nameRoom, "open");
            databaseManager.addRoom(idRoom, nameRoom);
            writer.println("ROOM_CREATED " + idRoom + " " + nameRoom);
        } else {
            // Join existing room
            writer.println("ROOM_JOINED " + idRoom + " " + nameRoom);
        }

        GameRoom gameRoom = roomController.getOrCreateRoom(idRoom, nameRoom);
        gameRoom.addPlayer(player);
        writer.println("ROOM_READY " + gameRoom.getRoomId());
        if (gameRoom.isFull()) {
            gameRoom.startGame();
            notifyPlayersInRoom(gameRoom, "GAME_STARTED " + gameRoom.getRoomId() + " " + gameRoom.getNumbersAsString());
        }
    }

    private void handleStartGame(String request, PrintWriter writer) {
        String[] parts = request.split(" ");
        if(parts.length < 2){
            writer.println("START_GAME_FAILURE Invalid format");
            return;
        }
        
        String username = parts[1];
        Player player = onlinePlayers.get(username);
        
        if(player == null){
            writer.println("START_GAME_FAILURE Player not logged in");
            return;
        }
        
        synchronized (roomController){
            //Tìm phòng chờ hoặc tạo phòng mới
            GameRoom gameRoom = roomController.getOrCreateRoom();
            
            if(gameRoom.addPlayer(player)){
                writer.println("START_GAME_SUCCESS " + gameRoom.getRoomId() + " " + gameRoom.getNumbersAsString());
                System.out.println(username + " added to room: " + gameRoom.getRoomId());
                
                while (gameRoom.isFull() && !gameRoom.isActive()) {
                    // Khi phòng đầy, kích hoạt trò chơi
                    gameRoom.startGame();
                    System.out.println("Check 1");
                    notifyPlayersInRoom(gameRoom, "GAME_STARTED " + gameRoom.getNumbersAsString());
                    System.out.println("Check 2");
                }                
            } else {
                writer.println("START_GAME_FAILURE Room is full");
            }
        }
    }

    private void handleLeaveRoom(String request, PrintWriter writer) {
        String[] parts = request.split(" ");
        if (parts.length < 3) {
            writer.println("LEAVE_ROOM_FAILURE Invalid format");
            return;
        }
        String username = parts[1];
        int roomId;
        try {
            roomId = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            writer.println("LEAVE_ROOM_FAILURE Invalid room ID");
            return;
        }

        GameRoom room = findRoomByPlayer(username);
        if (room == null) {
            writer.println("LEAVE_ROOM_FAILURE Room not found");
        } else {
            room.getPlayers().removeIf(player -> player.getUsername().equals(username));
            writer.println("LEAVE_ROOM_SUCCESS " + room.getRoomId());
            System.out.println(username + " left room: " + room.getRoomId());
            if (room.getPlayers().isEmpty()) {
                room.setStatus("open");
            } else {
                // Mark the remaining player as the winner
                Player remainingPlayer = room.getPlayers().get(0);
                notifyPlayersInRoom(room, "GAME_OVER " + remainingPlayer.getUsername() + " wins!");
                room.getPlayers().clear();
                room.setStatus("open");
            }
        }
    }

    private void handleUpdatePlayerStats(String request, PrintWriter writer){
        String[] parts = request.split(" ");
        if (parts.length < 4) {
            writer.println("UPDATE_PLAYER_STATS_FAILURE Invalid format");
            return;
        }
        String username = parts[1];
        int score;
        boolean isWinner;
        try {
            score = Integer.parseInt(parts[2]);
            isWinner = Integer.parseInt(parts[3]) == 1;
        } catch (NumberFormatException e) {
            writer.println("UPDATE_PLAYER_STATS_FAILURE Invalid score or winner format");
            return;
        }
        boolean success = databaseManager.updatePlayerStats(username, score, isWinner);
        if (success) {
            writer.println("UPDATE_PLAYER_STATS_SUCCESS");
        } else {
            writer.println("UPDATE_PLAYER_STATS_FAILURE Update failed");
        }
    }
    
    // ---------- Các phương thức xử lý lịch sử chơi (History) ----------
    private void handleSaveHistory(String request, PrintWriter writer) {
        String[] parts = request.split(" ");
        if (parts.length < 5) {
            writer.println("SAVE_HISTORY_FAILURE Invalid format");
            return;
        }
        int userId, roomId, score;
        try {
            userId = Integer.parseInt(parts[1]);
            roomId = Integer.parseInt(parts[2]);
            score = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            writer.println("SAVE_HISTORY_FAILURE Invalid data");
            return;
        }
        Timestamp playTime = Timestamp.valueOf(parts[4]);

        History history = new History(0, userId, roomId, score, playTime);
        if (databaseManager.addHistory(history)) {
            writer.println("SAVE_HISTORY_SUCCESS");
            System.out.println("History saved: User " + userId + " Room " + roomId + " Score " + score);
        } else {
            writer.println("SAVE_HISTORY_FAILURE Database error");
        }
    }

    private void handleGetHistory(String request, PrintWriter writer) {
        String[] parts = request.split(" ");
        if (parts.length < 2) {
            writer.println("GET_HISTORY_FAILURE Invalid format");
            return;
        }
        int userId;
        try {
            userId = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            writer.println("GET_HISTORY_FAILURE Invalid user ID");
            return;
        }

        List<History> histories = databaseManager.getHistoryByUserId(userId);
        if (histories.isEmpty()) {
            writer.println("GET_HISTORY_FAILURE No history found");
        } else {
            writer.println("GET_HISTORY_SUCCESS");
            for (History history : histories) {
                writer.println(history.getId() + " " + history.getRoomId() + " " + history.getScore() + " " + history.getPlayTime());
            }
        }
    }

    private void handleMove(String request, PrintWriter writer) {
        String[] parts = request.split(" ");
        if (parts.length < 3) {
            writer.println("MOVE_FAILURE Invalid format");
            return;
        }

        String username = parts[1];
        int number;
        try {
            number = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            writer.println("MOVE_FAILURE Invalid number format");
            return;
        }

        // Tìm phòng chứa người chơi
        GameRoom room = findRoomByPlayer(username);
        if (room == null || !room.isActive()) {
            writer.println("MOVE_FAILURE Game not active or room not found");
            return;
        }

        synchronized (room) {
            if (room.selectNumber(number, username)) {
                notifyPlayersInRoom(room, "MOVE_SUCCESS " + username + " " + number);

                // Update scores
                Map<String, Integer> scores = room.getScores();
                notifyPlayersInRoom(room, "SCORE " + scores.get(room.getPlayers().get(0).getUsername()) +
                        " " + scores.get(room.getPlayers().get(1).getUsername()));

                // Check game end condition
                if (room.allNumbersSelected()) {
                    String winner = room.determineWinner();
                    notifyPlayersInRoom(room, "GAME_OVER " + winner);
                    saveScoresToHistory(room);
                    room.resetGame(); // Đặt lại trạng thái trò chơi
                    roomController.removeRoom(room); // Xóa phòng khỏi controller
                    System.out.println("Room cleared and set to open.");
                }
            } else {
                writer.println("MOVE_FAILURE Wrong number");
            }
        }
    }

    private void handleExitGame(String request, PrintWriter writer) {
        String[] parts = request.split(" ");
        if (parts.length < 2) {
            writer.println("EXIT_GAME_FAILURE Invalid format");
            System.out.println("EXIT_GAME_FAILURE: Request format incorrect: " + request);
            return;
        }
        String username = parts[1];

        GameRoom room = findRoomByPlayer(username);
        if (room == null) {
            writer.println("EXIT_GAME_FAILURE Room not found");
            System.out.println("EXIT_GAME_FAILURE: Room not found for user: " + username);
            return;
        }

        synchronized (room) {
            // Loại người chơi ra khỏi phòng
            room.getPlayers().removeIf(player -> player.getUsername().equals(username));
            writer.println("EXIT_GAME_SUCCESS");
            
            if (!room.getPlayers().isEmpty()) {
                // Nếu còn lại người chơi khác, thông báo "GAME_OVER"
                Player remainingPlayer = room.getPlayers().get(0);
                PrintWriter remainingPlayerWriter = getPlayerWriter(remainingPlayer.getUsername());
                if (remainingPlayerWriter != null) {
                    remainingPlayerWriter.println("GAME_OVER");
                }
            }

            room.resetGame();
            roomController.removeRoom(room);
            System.out.println("Room cleared and set to open.");
        }
    }

    private void handleGetPlayerStats(String request, PrintWriter writer){
        String[] parts = request.split(" ");
        if (parts.length < 2) {
            writer.println("GET_PLAYER_STATS_FAILURE Invalid format");
            return;
        }
        String username = parts[1];
        Player player = databaseManager.getUserByUsername(username);
        if (player != null) {
            writer.println("GET_PLAYER_STATS_SUCCESS " + player.getId() + " " + player.getUsername() + " " + player.getHighScore() + " " + player.getGamesPlayed() + " " + player.getGamesWon());
        } else {
            writer.println("GET_PLAYER_STATS_FAILURE User not found");
        }
    }
    
    // ---------- Các hàm phụ -----
    private void notifyPlayersInRoom(GameRoom gameRoom, String message) {
        System.out.println(message);
        for (Player player : gameRoom.getPlayers()) {
            PrintWriter playerWriter = getPlayerWriter(player.getUsername());
            if (playerWriter != null) {
                playerWriter.println(message);
                System.out.println(message);
            } 
        }
    }

    private PrintWriter getPlayerWriter(String username) {
        System.out.println(username);
        return playerWriters.get(username);
    }
    
    private synchronized GameRoom findRoomByPlayer(String username) {
        List<GameRoom> rooms = roomController.getAllRooms();
        if (rooms == null) {
            return null;
        }

        for (GameRoom room : rooms) {
            List<Player> players = room.getPlayers();
            if (players == null) {
                continue;
            }

            for (Player player : players) {
                if (player.getUsername().equals(username)) {
                    return room;
                }
            }
        }
        return null;
    }

    private void saveScoresToHistory(GameRoom room) {
        Timestamp playTime = new Timestamp(System.currentTimeMillis());
        for (Player player : room.getPlayers()) {
            int score = room.getScores().get(player.getUsername());
            History history = new History(player.getId(), room.getRoomId(), score, playTime);
            databaseManager.addHistory(history);
        }
    }
}