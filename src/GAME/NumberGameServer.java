package GAME;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class NumberGameServer {
    private static final int PORT = 12345;  // Cổng mà server lắng nghe
    private static List<GameRoom> rooms = new ArrayList<>();  // Danh sách các phòng chơi

    public static void main(String[] args) {
        System.out.println("Server is running...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();  // Chấp nhận kết nối mới từ client
                System.out.println("A new player has connected.");

                // Tìm phòng trống hoặc tạo phòng mới
                GameRoom room = getOrCreateRoom();
                ClientHandler clientHandler = new ClientHandler(clientSocket, room);

                // Thêm client vào phòng
                synchronized (room) {
                    room.addPlayer(clientHandler);
                }

                clientHandler.start();  // Bắt đầu luồng để xử lý client
            }
        } catch (IOException e) {
            e.printStackTrace();
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
