package Model;

import Controller.ClientController;
import Controller.ServerController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GameRoom {
    private static final AtomicInteger ROOM_ID_GENERATOR = new AtomicInteger(1);
    private final int roomId;
    
    private List<Player> players;  // Danh sách người chơi trong phòng
    private List<Integer> numbers; // Danh sách số trong trò chơi
    private int currentPlayerIndex;  // Chỉ số người chơi đang chơi
    private boolean isActive = false; // Trạng thái phòng: false = chờ, true = đang chơi
    private int nextNumber = 1;
    private Map<String, Integer> scores;
    
    public GameRoom() {
        this.roomId = ROOM_ID_GENERATOR.getAndIncrement();
        this.players = new ArrayList<>();
        this.numbers = new ArrayList<>();
        this.scores = new HashMap<>();
        for (int i = 1; i <= 100; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);  // Trộn số ngẫu nhiên
        this.currentPlayerIndex = 0;
    }
    
    public int getRoomId(){
        return roomId;
    }

    // Thêm người chơi vào phòng
    public synchronized boolean addPlayer(Player player) {
        if (players.size() < 2) {  // Giới hạn 2 người chơi trong một phòng
            players.add(player);
            scores.put(player.getUsername(), 0);
            return true;
        }
        return false;  // Phòng đã đầy
    }

    // Kiểm tra và cập nhật số được nhấn
    public synchronized boolean selectNumber(int number, String username) {
        if (number == nextNumber) {
            nextNumber++;
            int currentScore = scores.get(username);
            scores.put(username, currentScore + 1); // Tăng điểm số
            return true;
        }
        return false; // Nếu nhấn sai số
    }
    
    // Lấy điểm số của người chơi
    public synchronized Map<String, Integer> getScores() {
        return new HashMap<>(scores); // Trả về bản sao điểm số
    }
    
    // Kiểm tra nếu phòng có đủ người chơi
    public boolean isFull() {
        return players.size() == 2;
    }
    
    public synchronized boolean isActive(){
        return isActive;
    }

    public List<Integer> getNumbers() {	
        return numbers;
    }

    public synchronized boolean selectNumber(int number, ServerController controller){
        if(number == nextNumber){
            //selectedNumber.add(number);
            nextNumber++;
            return true;
        } else{
            return false;
        }
    }
    
    // Lấy số tiếp theo cần chọn
    public int getNextNumber() {
        return nextNumber;  // Lấy số đầu tiên
    }
    public String getNumbersAsString() {
        StringBuilder sb = new StringBuilder();
        for (int number : numbers) {
            sb.append(number).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1); // Xóa dấu "," cuối cùng
        }
        return sb.toString();
    }

    // Cập nhật số đã chọn
    public void selectNumber(int number) {
        numbers.remove(Integer.valueOf(number));
    }

    // Kiểm tra nếu số có hợp lệ hay không
    public boolean isValidNumber(int number) {
        return numbers.contains(number);
    }

    public List<Player> getPlayers() {
        return players;
    }
    
    public synchronized void startGame(){
        if(players.size() == 2 && !isActive){
            this.isActive = true;
            System.out.println("Game started in room: " + roomId);
        } else {
            System.out.println("Cannot start game: Room is either already active or not full.");
        }
    }
}