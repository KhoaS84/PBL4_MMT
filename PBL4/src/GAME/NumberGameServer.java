package GAME;
import java.io.*;
import java.net.*;
import java.util.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NumberGameServer {
    private static final int PORT = 12345;  // Cổng mà server lắng nghe

    public static void main(String[] args) {
        System.out.println("Server is running...");

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket socket = serverSocket.accept();  // Chấp nhận kết nối mới từ client
                System.out.println("A new player has connected.");
                
                // Tạo luồng xử lý cho mỗi client
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandler.start();  // Bắt đầu luồng để xử lý client
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

