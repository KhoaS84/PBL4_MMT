package Model;

import java.sql.Timestamp;

public class History {
    private int id;
    private int userId;  // Liên kết tới Player (bảng users)
    private int roomId;  // Liên kết tới Room
    private int score;
    private String result;
    private Timestamp playTime;

    // Constructor
    public History(int id, int userId, int roomId, int score, Timestamp playTime) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.score = score;
        this.playTime = playTime;
    }

    public History(int userId, int roomId, int score, Timestamp playTime) {
        this.userId = userId;
        this.roomId = roomId;
        this.score = score;
        this.playTime = playTime;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Timestamp getPlayTime() {
        return playTime;
    }

    public void setPlayTime(Timestamp playTime) {
        this.playTime = playTime;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", userId=" + userId +
                ", roomId=" + roomId +
                ", score=" + score +
                ", playTime=" + playTime +
                '}';
    }
}
