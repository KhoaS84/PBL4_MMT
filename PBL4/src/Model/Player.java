package Model;

import java.util.HashSet;
import java.util.Set;

public class Player {
    private int id;
    private String username;
    private String password;
    private int highScore;
    private int gamesPlayed;
    private int gamesWon;

    // Danh sách các số người chơi đã chọn
    private Set<Integer> selectedNumbers;

    // Constructor
    public Player(int id, String username, String password, int highScore, int gamesPlayed, int gamesWon) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.highScore = highScore;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.selectedNumbers = new HashSet<>();
    }

    // Constructor không có ID (Dùng khi thêm người chơi mới)
    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.highScore = 0;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.selectedNumbers = new HashSet<>();
    }

    // Getter and Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    // Phương thức chọn số
    public void selectNumber(int number) {
        selectedNumbers.add(number);
    }

    // Phương thức lấy số lượng số đã chọn
    public int getSelectedNumbersCount() {
        return selectedNumbers.size();
    }

    // Phương thức reset lại các số đã chọn khi bắt đầu trò chơi mới
    public void resetSelectedNumbers() {
        selectedNumbers.clear();
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", highScore=" + highScore +
                ", gamesPlayed=" + gamesPlayed +
                ", gamesWon=" + gamesWon +
                '}';
    }
}
