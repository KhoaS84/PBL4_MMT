package GAME;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("resource")
public class NumberGameClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private JFrame frame;
    private JButton[] numberButtons = new JButton[100];
    private PrintWriter out;
    private BufferedReader in;

    public NumberGameClient() {
        // Tạo giao diện người chơi
        frame = new JFrame("Number Finding Game");
        frame.setLayout(new GridLayout(10, 10));

        // Tạo các nút số từ 1 đến 100, tạm thời chưa có số cụ thể
        for (int i = 0; i < 100; i++) {
            numberButtons[i] = new JButton("");
            frame.add(numberButtons[i]);

            // ActionListener cho mỗi nút số
            final int index = i;
            numberButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (out != null && !numberButtons[index].getText().isEmpty()) {
                        int selectedNumber = Integer.parseInt(numberButtons[index].getText());
                        out.println(selectedNumber);  // Gửi số được chọn tới server
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
                        if (serverMessage.startsWith("NUMBERS:")) {
                            // Nhận danh sách số và thiết lập giao diện
                            String numberString = serverMessage.substring(8);
                            List<Integer> numbers = parseNumbers(numberString);
                            setupButtons(numbers);
                        } else if (serverMessage.contains("selected")) {
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

    // Hàm parse chuỗi thành danh sách số nguyên
    private List<Integer> parseNumbers(String numberString) {
        numberString = numberString.replaceAll("[\\[\\]]", "");  // Loại bỏ dấu []
        String[] numberArray = numberString.split(", ");
        List<Integer> numbers = new ArrayList<>();
        for (String num : numberArray) {
            numbers.add(Integer.parseInt(num.trim()));
        }
        return numbers;
    }

    // Thiết lập các nút theo danh sách số từ server
    private void setupButtons(List<Integer> numbers) {
        for (int i = 0; i < 100; i++) {
            numberButtons[i].setText(String.valueOf(numbers.get(i)));
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
