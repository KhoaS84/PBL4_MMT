/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DEMO.CLIENT.CONTROLLER;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author KHOA
 */
public class Client_Connection{
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 11111;
    private static final String LOG_FILE = "client_log.txt";

    private PrintWriter out;
    private BufferedReader in;
    private Client_GameNumber client;

    public Client_Connection(Client_GameNumber client) {
        this.client = client;
    }

    void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        processServerMessage(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            //client.showErrorMessage("Unable to connect to the server.");
        }
    }

    private void processServerMessage(String serverMessage) {
        System.out.println("Server: " + serverMessage);

        if (serverMessage.startsWith("SCORE")) {
        // Thông điệp điểm số
        String[] parts = serverMessage.split("\\|");
        int player1Score = Integer.parseInt(parts[0].replace("SCORE Player 1: ", "").trim());
        int player2Score = Integer.parseInt(parts[1].replace("Player 2: ", "").trim());
        client.updateScores(player1Score, player2Score);

        } else if (serverMessage.startsWith("Room ") && serverMessage.contains("WAIT")) {
            // Phòng chờ, hiển thị màn hình chờ
            client.view.showWaitScreen();

        } else if (serverMessage.startsWith("NUMBERS:")) {
            // Nhận danh sách số và hiển thị giao diện trò chơi
            String numberString = serverMessage.substring(8);
            List<Integer> numbers = parseNumbers(numberString);
            client.setupButtons(numbers);
            client.showGameScreen();

        } else if (serverMessage.startsWith("Room ") && serverMessage.contains("selected")) {
            // Một số đã được chọn, cập nhật màu cho số đó
        try {
            String[] parts = serverMessage.split(" ");
            // Cấu trúc thông điệp: "Room <roomId>: Number <selectedNumber> has been selected by Player <playerId> with color <color>."
            if (parts.length >= 13) {
                int selectedNumber = Integer.parseInt(parts[3]); // Số được chọn
                int playerId = Integer.parseInt(parts[9]); // ID người chơi
                String colorString = parts[12].replace(".", ""); // Màu (loại bỏ dấu chấm cuối)
                Color color = colorString.equalsIgnoreCase("red") ? Color.RED : Color.BLUE;
                client.updateButtonColor(selectedNumber, color);
            } else {
                System.err.println("Invalid server message format: " + serverMessage);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number: " + e.getMessage());
        }

        } else if (serverMessage.startsWith("Room ") && serverMessage.contains("Lựa chọn không hợp lệ")) {
            // Thông báo lỗi khi chọn sai số
            //client.showErrorMessage(serverMessage);

        } else if (serverMessage.equals("Invalid selection or number already taken.")) {
            // Thông báo lỗi chọn số
            //client.showErrorMessage(serverMessage);

        } else if (serverMessage.startsWith("Room ") && serverMessage.contains("disconnected")) {
            // Một người chơi thoát, thông báo và reset trò chơi
            //client.showErrorMessage(serverMessage);
            //client.resetGameView();

        } else if (serverMessage.startsWith("Room ") && serverMessage.contains("Game reset")) {
            // Trò chơi được reset
            //client.resetGameView();
        }
    }

    public void sendNumber(int number) {
        if (out != null) {
            out.println(number);
        }
    }

    private List<Integer> parseNumbers(String numberString) {
        numberString = numberString.replaceAll("[\\[\\]]", "");
        String[] numberArray = numberString.split(", ");
        List<Integer> numbers = new ArrayList<>();
        for (String num : numberArray) {
            numbers.add(Integer.parseInt(num.trim()));
        }
        return numbers;
    }
    
    private void log(String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String logMessage = "[" + timestamp + "] " + message;
        System.out.println(logMessage); // Ghi ra console
        try (PrintWriter writer = new PrintWriter(new PrintWriter(LOG_FILE, "UTF-8"), true)) {
            writer.println(logMessage); // Ghi ra file
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }
}