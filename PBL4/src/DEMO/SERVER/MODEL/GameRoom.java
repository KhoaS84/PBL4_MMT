/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DEMO.SERVER.MODEL;

import DEMO.SERVER.CONTROLLER.WaitingHandler;
import DEMO.SERVER.CONTROLLER.ClientHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author KHOA
 */
public class GameRoom {
    private static final AtomicInteger ROOM_ID_GENERATOR = new AtomicInteger(1);
    private final int roomId;
    
    private Set<Integer> selectedNumbers = new HashSet<>();
    private List<Integer> randomNumbers;
    private List<WaitingHandler> waitingHandlers = new ArrayList<>();
    private List<ClientHandler> handlers = new ArrayList<>();
    private int nextNumber = 1; // Bắt đầu từ số 1
    private static final int MAX_PLAYERS = 2; // Số lượng người chơi tối đa trong phòng
    private static final int MAX_NUMBER = 100; // Số lớn nhất có thể chọn    
    private boolean isActive = false; // Trạng thái phòng: false = chờ, true = đang chơi
    private int[] scores = new int[MAX_PLAYERS]; // Điểm của mỗi người chơi
    
    public GameRoom() {
        this.roomId = ROOM_ID_GENERATOR.getAndIncrement();
        // Tạo danh sách số ngẫu nhiên từ 1 đến 100
        randomNumbers = new ArrayList<>();
        for (int i = 1; i <= MAX_NUMBER; i++) {
            randomNumbers.add(i);
        }
        Collections.shuffle(randomNumbers);  // Trộn ngẫu nhiên danh sách
        nextNumber = 1;
        System.out.println("Phòng mới được tạo. nextNumber = " + nextNumber);
    }
    
    public int getRoomId(){
        return roomId;
    }

    public synchronized boolean addPlayer(WaitingHandler waitingHandler) {
        if (waitingHandlers.size() < MAX_PLAYERS) {
            waitingHandlers.add(waitingHandler);
            System.out.println("Room " + roomId + ": Player added. Total players: " + waitingHandlers.size());
            if (waitingHandlers.size() == MAX_PLAYERS) {
                isActive = true; // Phòng đã đầy và bắt đầu chơi
                notifyAll(); // Thông báo cho các luồng đang đợi khi phòng đã đầy
                notifyPlayersReady(); // Thông báo cho cả hai người chơi khi phòng đã đầy
                System.out.println("Room " + roomId + ": Room is full. Notifying players.");
            }
            return true;
        }
        System.out.println("Room " + roomId + ": Cannot add player. Room is full.");
        return false;  // Phòng đã đầy
    }
    
    public synchronized boolean isFull() {
        return waitingHandlers.size() == MAX_PLAYERS;
    }
    
    public synchronized boolean isActive() {
        return isActive;
    }
    
    public List<Integer> getRandomNumbers() {
        return randomNumbers;
    }

    public synchronized boolean selectNumber(int number, ClientHandler handler) {
        System.out.println("Room " + roomId + ": Current nextNumber: " + nextNumber);
        // Kiểm tra xem người chơi có chọn đúng số tiếp theo không
        if (number == nextNumber) {
            selectedNumbers.add(number); // Đánh dấu số đã chọn
            nextNumber++; // Cập nhật số tiếp theo cần chọn
            
            // Cập nhật điểm cho người chơi
            int playerIndex = handler.getPlayerId() - 1; // ID người chơi (1 hoặc 2)
            scores[playerIndex]++;
            System.out.println("Room " + roomId + ": Player " + handler.getPlayerId() + " scored. Total score: " + scores[playerIndex]);
            
            // Cập nhật cho cả hai người chơi về số đã chọn
            for (ClientHandler h : handlers) {
                h.sendNumberUpdate(number, handler.getPlayerId());
            }
            // Trả lại ban đầu
            if (nextNumber > MAX_NUMBER) {
                resetGame();
            }        
            return true;
        } else {
            // Gửi thông báo lỗi cho người chơi
            handler.sendMessage("Lựa chọn không hợp lệ! Vui lòng chọn số " + nextNumber + ".");
            return false;
        }
    }
    
    public void notifyPlayersReady() {
        isActive = true; // Đặt phòng là đang chơi
        System.out.println("Room " + roomId + ": Notifying players the game is starting.");

        // Chuyển từ WaitingHandler sang ClientHandler
        for (int i = 0; i < waitingHandlers.size(); i++) {
            WaitingHandler waitingHandler = waitingHandlers.get(i);
            waitingHandler.sendMessage("Room " + roomId + ": NUMBERS:" + randomNumbers.toString());
            ClientHandler handler = new ClientHandler(waitingHandler.getSocket(), this, i + 1);
            handlers.add(handler);
            handler.start();
        }
    }
    
    public int getNextPlayerId() {
        return waitingHandlers.size() + 1;
    }
    
    public synchronized void handlePlayerDisconnect(ClientHandler disconnectedHandler) {
        System.out.println("Player disconnected. Resetting the game room.");
        // Gửi thông báo cho tất cả người chơi còn lại
        for (ClientHandler handler : handlers) {
            if (handler != disconnectedHandler) {
                handler.sendMessage("A player has disconnected. The game will reset.");
            }
        }
        if (handlers.isEmpty()) {
            resetGame();
        }

        // Đặt lại trò chơi
        resetGame();
    }
    
    public synchronized void addHandler(ClientHandler handler) {
        handlers.add(handler);
    }    
    
    private void resetGame() {
        nextNumber = 1;
        selectedNumbers.clear();
        isActive = false; // Trở về trạng thái chờ
        Collections.shuffle(randomNumbers);
        
        handlers.clear();
        waitingHandlers.clear();
        
        System.out.println("Room " + roomId + ": Game reset. Room is now WAITING for players.");
    }
    
    // Trả về số lượng người chơi đang đợi trong phòng
    public synchronized int getWaitingHandlersCount() {
        return waitingHandlers.size();
    }
}
