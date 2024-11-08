package GAME;
import java.util.*;

public class GameRoom {
    private Set<Integer> selectedNumbers = new HashSet<>();
    private List<Integer> randomNumbers;
    private ClientHandler player1;
    private ClientHandler player2;
    private int nextNumber = 1; // Bắt đầu từ số 1

    public GameRoom() {
        // Tạo danh sách số ngẫu nhiên từ 1 đến 100
        randomNumbers = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            randomNumbers.add(i);
        }
        Collections.shuffle(randomNumbers);  // Trộn ngẫu nhiên danh sách
    }

    public boolean addPlayer(ClientHandler player) {
        if (player1 == null) {
            player1 = player;
            return true;
        } else if (player2 == null) {
            player2 = player;
            return true;
        }
        return false;  // Phòng đã đầy
    }

    public boolean isFull() {
        return player1 != null && player2 != null;
    }

    public List<Integer> getRandomNumbers() {
        return randomNumbers;
    }

    public synchronized boolean selectNumber(int number, ClientHandler player) {
        // Kiểm tra xem người chơi có chọn đúng số tiếp theo không
        if (number == nextNumber) {
            selectedNumbers.add(number); // Đánh dấu số đã chọn
            nextNumber++; // Cập nhật số tiếp theo cần chọn
            // Cập nhật cho cả hai người chơi về số đã chọn
            if (player1 != null) player1.sendNumberUpdate(number);
            if (player2 != null) player2.sendNumberUpdate(number);
            return true;
        } else {
            // Gửi thông báo lỗi cho người chơi
            player.sendMessage("Invalid selection! Please select number " + nextNumber + ".");
            return false;
        }
    }
}
