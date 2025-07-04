package Controller;

import Model.GameRoom;
import java.util.ArrayList;
import java.util.List;

public class RoomController{
    private List<GameRoom> rooms;
    
    public RoomController(){
        this.rooms = new ArrayList<>();
    }
    
    public synchronized GameRoom getOrCreateRoom() {
        for (GameRoom room : rooms) {
            if (!room.isActive() && !room.isFull()) {
                System.out.println("Assigning player to existing Room ID " + room.getRoomId());
                return room;
            }
        }
        
        // Nếu không có phòng, tạo phòng mới
        GameRoom newRoom = new GameRoom();
        rooms.add(newRoom);
        System.out.println("Created new Room ID " + newRoom.getRoomId());
        return newRoom;
    }
    
    public synchronized List<GameRoom> getAllRooms(){
        return new ArrayList<>(rooms);
    }
    
    public synchronized void logRoomsState() {
    System.out.println("=== Current Game Rooms State ===");
        for (GameRoom room : rooms) {
            String status = room.isActive() ? "ACTIVE" : (room.isFull() ? "FULL" : "WAITING");
    //        System.out.println("Room ID " + room.getRoomId() + ": " +
    //            room.getWaitingHandlersCount() + " players, " + status);
        }
    }
}
