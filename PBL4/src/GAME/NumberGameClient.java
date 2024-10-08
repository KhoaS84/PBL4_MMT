package GAME;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumberGameClient {
    private static final String SERVER_ADDRESS = "192.168.252.54";
    private static final int SERVER_PORT = 12345;

    private JFrame frame;
    private JButton[] numberButtons = new JButton[100];
    private PrintWriter out;
    private BufferedReader in;

    public NumberGameClient() {
        // Tạo giao diện người chơi
        frame = new JFrame("Number Finding Game");
        frame.setLayout(new GridLayout(10, 10));

        // Danh sách các số từ 1 đến 100
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            numbers.add(i);
        }

        // Trộn ngẫu nhiên các số
        Collections.shuffle(numbers);

        // Tạo các nút dựa trên các số đã trộn
        for (int i = 0; i < 100; i++) {
            numberButtons[i] = new JButton(String.valueOf(numbers.get(i)));
            frame.add(numberButtons[i]);

            // ActionListener cho mỗi nút số
            final int number = numbers.get(i);
            numberButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (out != null) {
                        out.println(number);  // Gửi số được chọn tới server
                    }
                }
            });
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);

        // Kết nối với server
        connectToServer();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Lắng nghe thông báo từ server và cập nhật giao diện
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        if (serverMessage.contains("selected")) {
                            int selectedNumber = Integer.parseInt(serverMessage.replaceAll("[^0-9]", ""));
                            disableNumberButton(selectedNumber);  // Vô hiệu hóa nút số đã chọn
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Vô hiệu hóa nút đã được chọn
    private void disableNumberButton(int number) {
        for (JButton button : numberButtons) {
            if (button.getText().equals(String.valueOf(number))) {
                button.setEnabled(false);
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NumberGameClient());
    }
}
