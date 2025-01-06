package Application;

import Controller.ClientController;
import View.LoginView;

import java.io.IOException;
import java.net.Socket;

public class ClientMain {

    private static final String SERVER_ADDRESS = "172.20.10.2"; // Địa chỉ server
    private static final int SERVER_PORT = 12345; // Cổng kết nối

    public static void main(String[] args) {
        try {
            // Kết nối đến server
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to server.");

            // Khởi tạo ClientController để xử lý giao tiếp
            ClientController clientController = new ClientController(socket);

            // Hiển thị màn hình đăng nhập
            LoginView loginView = new LoginView(clientController);
            loginView.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
