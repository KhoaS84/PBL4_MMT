/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DEMO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author KHOA
 */
public class GameRoom {
    private Set<Integer> selectedNumbers = new HashSet<>();
    private List<Integer> randomNumbers;
    private List<WaitingHandler> waitingHandlers = new ArrayList<>();
    private List<Handler> handlers = new ArrayList<>();
    private int nextNumber = 1; // Bắt đầu từ số 1
    private static final int MAX_PLAYERS = 2; // Số lượng người chơi tối đa trong phòng

    public GameRoom() {
        // Tạo danh sách số ngẫu nhiên từ 1 đến 100
        randomNumbers = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            randomNumbers.add(i);
        }
        Collections.shuffle(randomNumbers);  // Trộn ngẫu nhiên danh sách
        System.out.println("11s");
    }

    public synchronized boolean addPlayer(WaitingHandler waitingHandler) {
        if (waitingHandlers.size() < MAX_PLAYERS) {
            waitingHandlers.add(waitingHandler);
            if (waitingHandlers.size() == MAX_PLAYERS) {
                notifyAll(); // Thông báo cho các luồng đang đợi khi phòng đã đầy
                notifyPlayersReady(); // Thông báo cho cả hai người chơi khi phòng đã đầy
            }
            return true;
        }
        return false;  // Phòng đã đầy
    }
    
    public synchronized boolean isFull() {
        return waitingHandlers.size() == MAX_PLAYERS;
    }
    
    public List<Integer> getRandomNumbers() {
        return randomNumbers;
    }

    public synchronized boolean selectNumber(int number, Handler handler) {
        // Kiểm tra xem người chơi có chọn đúng số tiếp theo không
        if (number == nextNumber) {
            selectedNumbers.add(number); // Đánh dấu số đã chọn
            nextNumber++; // Cập nhật số tiếp theo cần chọn
            // Cập nhật cho cả hai người chơi về số đã chọn
            for (Handler h : handlers) {
                h.sendNumberUpdate(number);
            }
            return true;
        } else {
            // Gửi thông báo lỗi cho người chơi
            handler.sendMessage("Invalid selection! Please select number " + nextNumber + ".");
            return false;
        }
    }
    
        public void notifyPlayersReady() {
        for (WaitingHandler waitingHandler : waitingHandlers) {
            waitingHandler.sendMessage("NUMBERS:" + randomNumbers.toString());
        }
        // Chuyển tất cả các WaitingHandler thành Handler
        for (WaitingHandler waitingHandler : waitingHandlers) {
            Handler handler = new Handler(waitingHandler.getSocket(), this);
            handlers.add(handler);
            handler.start();
        }
        waitingHandlers.clear();
    }
}
