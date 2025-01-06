package Application;
import Controller.ServerController;
import Model.DatabaseManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static final int PORT = 12345; // Cổng để client kết nối

    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager();  // Khởi tạo cơ sở dữ liệu người chơi
        ServerController serverController = new ServerController(databaseManager);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running and waiting for clients to connect...");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();  // Chấp nhận kết nối từ client
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Xử lý client trong một luồng riêng biệt
                new Thread(() -> serverController.handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
