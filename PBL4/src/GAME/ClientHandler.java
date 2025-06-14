package GAME;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static int nextNumber = 1;  // Số tiếp theo cần chọn, bắt đầu từ 1

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            synchronized (clients) {
                clients.add(this);
            }

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                int selectedNumber = Integer.parseInt(inputLine);

                synchronized (ClientHandler.class) {  // Đồng bộ hóa trên class để quản lý biến nextNumber
                    if (selectedNumber == nextNumber) {
                        nextNumber++;  // Chỉ cập nhật số tiếp theo nếu số chọn là hợp lệ
                        out.println("You selected number: " + selectedNumber);
                        broadcastNumberSelection(selectedNumber);
                    } else {
                        out.println("Invalid selection. Please select number: " + nextNumber);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            synchronized (clients) {
                clients.remove(this);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastNumberSelection(int selectedNumber) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendNumberUpdate(selectedNumber);
            }
        }
    }

    private void sendNumberUpdate(int selectedNumber) {
        out.println("Number " + selectedNumber + " has been selected.");
    }
}

