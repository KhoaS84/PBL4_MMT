package Model;

public class Room {
    private int idRoom;
    private String nameRoom;
    private String statusRoom; // "open" hoặc "close"
    private int playerRoom;

    // Constructor
    public Room(int idRoom, String nameRoom, String statusRoom) {
        this.idRoom = idRoom;
        this.nameRoom = nameRoom;
        this.statusRoom = statusRoom;
    }

    // Getters và Setters
    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }

    public String getNameRoom() {
        return nameRoom;
    }

    public void setNameRoom(String nameRoom) {
        this.nameRoom = nameRoom;
    }

    public String getStatusRoom() {
        return statusRoom;
    }

    public void setStatusRoom(String statusRoom) {
        this.statusRoom = statusRoom;
    }

    @Override
    public String toString() {
        return "Room{" +
                "idRoom=" + idRoom +
                ", nameRoom='" + nameRoom + '\'' +
                ", statusRoom='" + statusRoom + '\'' +
                '}';
    }
}
