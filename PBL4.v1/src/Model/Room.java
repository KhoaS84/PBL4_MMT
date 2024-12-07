package Model;

public class Room {
    private int id;
    private String roomName;
    private String status; // "open" hoặc "closed"

    // Constructor
    public Room(int id, String roomName, String status) {
        this.id = id;
        this.roomName = roomName;
        this.status = status;
    }

    public Room(String roomName, String status) {
        this.roomName = roomName;
        this.status = status;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomName='" + roomName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
