/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DEMO.SERVER.CONTROLLER;

import DEMO.SERVER.MODEL.GameRoom;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author KHOA
 */
public class ClientHandler extends Thread{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GameRoom room;
    private int playerId;

    public ClientHandler(Socket socket, GameRoom room, int playerId) {
        this.socket = socket;
        this.room = room;
        this.playerId = playerId;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            List<Integer> numbers = room.getRandomNumbers();
            out.println("NUMBERS:" + numbers.toString());

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                int selectedNumber = Integer.parseInt(inputLine);
                System.out.println("Room " + room.getRoomId() + ": Player " + playerId + " selected number " + selectedNumber);
                
                
                // Kiểm tra nếu người chơi chọn đúng số
                if (!room.selectNumber(selectedNumber, this)) {
                    out.println("Invalid selection or number already taken.");
                }
            }
        }catch (Exception e) {
            System.out.println("Player " + playerId + " disconnected.");
            room.handlePlayerDisconnect(this);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void sendMessage(String message){
        if (out != null) {
            out.println("Room " + room.getRoomId() + ": " + message);
        }
    }

    public void sendNumberUpdate(int selectedNumber, int playerId) {
        String color = (playerId == 1) ? "red" : "blue";
        out.println("Room " + room.getRoomId() + ": Number " + selectedNumber + " has been selected by Player " + playerId + " with color " + color + ".");
    }
    
    public int getPlayerId(){
        return playerId;
    }
    
    public Socket getSocket(){
        return socket;
    }
    
    public void sendScoreUpdate(int player1Score, int player2Score) {
        if (out != null) {
            out.println("SCORE Player 1: " + player1Score + " | Player 2: " + player2Score);
        }
    }
}
