/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DEMO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author KHOA
 */
public class WaitingHandler extends Thread{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GameRoom room;
    
    public WaitingHandler(Socket socket, GameRoom room){
        this.socket=socket;
        this.room=room;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try {
            // Gửi thông báo "WAIT" ngay khi client kết nối
            sendMessage("WAIT");

            // Chờ cho đến khi phòng đầy
            synchronized (room) {
                while (!room.isFull()) {
                    room.wait(); // Chờ cho đến khi phòng đầy
                }
            }

            // Khi phòng đầy, chuyển sang lớp Handler để xử lý client
            Handler handler = new Handler(socket, room);
            handler.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
    
    public Socket getSocket(){
        return socket;
    }
}
