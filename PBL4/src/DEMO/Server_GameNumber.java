/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DEMO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author KHOA
 */
public class Server_GameNumber {
    private static final int PORT = 11111;  // Cổng mà server lắng nghe
    private static List<GameRoom> rooms = new ArrayList<>();  // Danh sách các phòng chơi

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port "+PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();  // Chấp nhận kết nối mới từ client
                System.out.println("A new player has connected.");
                
                // Tìm phòng trống hoặc tạo phòng mới
                GameRoom room = getOrCreateRoom();
                WaitingHandler waitingHandler = new WaitingHandler(clientSocket, room);

                // Thêm client vào phòng
                synchronized (room) {
                    room.addPlayer(waitingHandler);
                }
                
                waitingHandler.start();// Bắt đầu luồng để xử lý client
            }
        } catch (IOException e) {
            
        }
    }

    // Tìm phòng trống hoặc tạo phòng mới nếu không có phòng nào trống
    private static GameRoom getOrCreateRoom() {
        synchronized (rooms) {
            for (GameRoom room : rooms) {
                if (!room.isFull()) {
                    return room;
                }
            }
            GameRoom newRoom = new GameRoom();
            rooms.add(newRoom);
            return newRoom;
        }
    }
}
