/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DEMO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author KHOA
 */
public class Handler extends Thread{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GameRoom room;
//    public void sendMessage(String message) {
//        out.println(message);
//    }

    public Handler(Socket socket, GameRoom room) {
        this.socket = socket;
        this.room = room;
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

                // Check if the number has been selected and handle it
                if (!room.selectNumber(selectedNumber, this)) {
                    out.println("Invalid selection or number already taken.");
                }
            }
        } catch (Exception e) {
        }
    }
    
    public void sendMessage(String message){
        if (out != null) {
            out.println(message);
        }
    }

    public void sendNumberUpdate(int selectedNumber) {
        out.println("Number " + selectedNumber + " has been selected.");
    }
}
