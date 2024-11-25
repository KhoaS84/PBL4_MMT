/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DEMO.SERVER.CONTROLLER;

import DEMO.SERVER.MODEL.GameRoom;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author KHOA
 */
public class Server_GameNumber {
    private static final int PORT = 11111;  // Cổng mà server lắng nghe
    private static List<GameRoom> rooms = new ArrayList<>();  // Danh sách các phòng chơi
    private static final String LOG_FILE = "server_log.txt";
    
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port "+PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();  // Chấp nhận kết nối mới từ client
                System.out.println("A new player has connected.");
                
                // Tìm phòng trống hoặc tạo phòng mới
                GameRoom room = getOrCreateRoom();
                synchronized (room) {
                    int playerId = room.getNextPlayerId(); // Lấy ID tiếp theo cho người chơi
                    System.out.println("Assigning player to Room ID " + room.getRoomId() + ", Player ID " + playerId);
                    WaitingHandler waitingHandler = new WaitingHandler(clientSocket, room, playerId);

                    // Thêm client vào phòng
                    if (room.addPlayer(waitingHandler)) {
                        System.out.println("Player " + playerId + " added to Room ID " + room.getRoomId());
                    } else {
                        System.out.println("Failed to add player " + playerId + " to Room ID " + room.getRoomId());
                    }

                    waitingHandler.start(); // Bắt đầu luồng để xử lý client
                }
                logRoomsState(); // Ghi lại trạng thái của tất cả các phòng
            }
        } catch (IOException e) {
            
        }
    }

    // Tìm phòng trống hoặc tạo phòng mới nếu không có phòng nào trống
    private static GameRoom getOrCreateRoom() {
        synchronized (rooms) {
            for (GameRoom room : rooms) {
                if (!room.isActive() && !room.isFull()) {
                    System.out.println("Assigning player to existing Room ID " + room.getRoomId());
                    return room;
                }
            }
            // Nếu phòng đầy, tạo phòng mới
            GameRoom newRoom = new GameRoom();
            rooms.add(newRoom);
            System.out.println("Created new Room ID " + newRoom.getRoomId());
            return newRoom;
        }
    }
    
    // Ghi log trạng thái của tất cả các phòng
    private static void logRoomsState() {
        System.out.println("=== Current Game Rooms State ===");
        synchronized (rooms) {
            for (GameRoom room : rooms) {
                String status = room.isActive() ? "ACTIVE" : (room.isFull() ? "FULL" : "WAITING");
                System.out.println("Room ID " + room.getRoomId() + ": " +
                    room.getWaitingHandlersCount() + " players, " + status);
            }
        }
        System.out.println("================================");
    }
    
    // Ghi log ra file hoặc console
    private static void log(String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String logMessage = "[" + timestamp + "] " + message;
        System.out.println(logMessage); // Ghi ra console
        try (PrintWriter writer = new PrintWriter(new PrintWriter(LOG_FILE, "UTF-8"), true)) {
            writer.println(logMessage); // Ghi ra file
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }
}
