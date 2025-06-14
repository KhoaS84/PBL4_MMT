/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DEMO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author KHOA
 */
public class Client_Connection{
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 11111;

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
                    //System.out.println(in.readLine());
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                        if (serverMessage.startsWith("NUMBERS:")) {
                            // Nhận danh sách số và thiết lập giao diện
                            String numberString = serverMessage.substring(8);
                            List<Integer> numbers = parseNumbers(numberString);
                            client.setupButtons(numbers);
                            client.showGameScreen();
                            System.out.println("1c");
                        } else if (serverMessage.contains("selected")) {
                            int selectedNumber = Integer.parseInt(serverMessage.replaceAll("[^0-9]", ""));
                            client.disableNumberButton(selectedNumber);  // Vô hiệu hóa nút số đã chọn
                        } else if (serverMessage.equals("WAIT")){
                            client.view.showWaitScreen();
                            System.out.println("1d");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("f");
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
}