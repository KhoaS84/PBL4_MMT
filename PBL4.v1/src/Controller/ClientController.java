package Controller;

import java.io.*;
import java.net.Socket;

public class ClientController {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientController(Socket socket) throws IOException {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

            if (socket != null && socket.isConnected()) {
                System.out.println("Connection established with server.");
            } else {
                System.out.println("Failed to connect to server.");
            }
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    // Gửi thông tin đăng nhập
    public void sendLogin(String username, String password) {
        sendMessage("LOGIN " + username + " " + password);
    }

    // Gửi yêu cầu phòng
    public void sendRoom(String roomName) {
        sendMessage("ROOM " + roomName);
    }

    public void sendMove(String playerName, int number) {
        sendMessage("MOVE " + playerName + " " + number);
    }


    // Gửi tin nhắn
    private void sendMessage(String message) {
        out.println(message);
    }

    // Lắng nghe phản hồi từ server
    public String listen() {
        try {
            String response = in.readLine();
            System.out.println("Received from server: " + response);
            return response;
        } catch (IOException e) {
            System.err.println("Error while listening from server: " + e.getMessage());
            return null;
        }
    }

    public void sendStartGame(String username, int roomId) {
        sendMessage("START_GAME " + username + " " + roomId);
    }

    public void sendExitGame(String username){
        sendMessage("LEAVE_ROOM" + username);
    }

    // Đăng xuất
    public void sendLogout(String username) {
        sendMessage("LOGOUT " + username);
    }
    public void sendRegister(String username, String password) {
        sendMessage("REGISTER " + username + " " + password);
    }

    // Đóng kết nối
    public void closeConnection() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
