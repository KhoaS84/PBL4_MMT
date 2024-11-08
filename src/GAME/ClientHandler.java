package GAME;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GameRoom room;
    public void sendMessage(String message) {
        out.println(message);
    }


    public ClientHandler(Socket socket, GameRoom room) {
        this.socket = socket;
        this.room = room;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Gửi danh sách số ngẫu nhiên tới client
            List<Integer> numbers = room.getRandomNumbers();
            out.println("NUMBERS:" + numbers.toString());

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                int selectedNumber = Integer.parseInt(inputLine);

                // Kiểm tra xem số đã được chọn chưa và xử lý
                if (!room.selectNumber(selectedNumber, this)) {
                    out.println("Invalid selection or number already taken.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendNumberUpdate(int selectedNumber) {
        out.println("Number " + selectedNumber + " has been selected.");
    }
}
