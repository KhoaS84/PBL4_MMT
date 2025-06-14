package View;

import Controller.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginView extends JFrame {
    private ClientController clientController;

    // Khung của giao diện Login
    public LoginView(ClientController clientController) {
        this.clientController = clientController;

        setTitle("Login");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
    }

    // Tạo giao diện cho Login
    private void createUI() {
        setLayout(new BorderLayout());

        // Tiêu đề
        JLabel titleLabel = new JLabel("Welcome to Game Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(70, 130, 180));
        add(titleLabel, BorderLayout.NORTH);

        // Panel chứa các thành phần nhập liệu
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255)); // Màu nền nhạt
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // Padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JTextField usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JPasswordField passwordField = new JPasswordField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Nút đăng nhập và đăng ký
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginButton.setBackground(new Color(60, 179, 113)); // Màu xanh lá
        loginButton.setForeground(Color.WHITE); // Chữ trắng
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));

        registerButton.setBackground(new Color(70, 130, 180)); // Màu xanh dương
        registerButton.setForeground(Color.WHITE); // Chữ trắng
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));

        // Hiệu ứng hover cho nút
        addHoverEffect(loginButton, new Color(34, 139, 34));
        addHoverEffect(registerButton, new Color(25, 120, 180));

        // Xử lý sự kiện nút đăng nhập
        loginButton.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Gửi thông tin đăng nhập tới server
            clientController.sendLogin(username, password);
            String response = clientController.listen(); // Lắng nghe phản hồi từ server

            if (response != null && response.startsWith("LOGIN_SUCCESS")) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LobbyView(clientController, username).setVisible(true); // Chuyển sang LobbyView
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Xử lý sự kiện nút đăng ký
        registerButton.addActionListener((ActionEvent e) -> {
            new RegisterView(clientController).setVisible(true); // Mở giao diện đăng ký
            dispose();  // Đóng cửa sổ đăng nhập
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.add(loginButton);
        bottomPanel.add(registerButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Thêm hiệu ứng hover cho nút
    private void addHoverEffect(JButton button, Color hoverColor) {
        Color originalColor = button.getBackground();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
    }
}
