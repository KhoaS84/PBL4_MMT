package View;

import Controller.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegisterView extends JFrame {
    private final ClientController clientController;

    public RegisterView(ClientController clientController) {
        this.clientController = clientController;

        setTitle("Register");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
    }

    private void createUI() {
        setLayout(new BorderLayout());

        // Tiêu đề
        JLabel titleLabel = new JLabel("Register New Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(70, 130, 180));
        add(titleLabel, BorderLayout.NORTH);

        // Panel chứa các thành phần nhập liệu
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255)); // Màu nền nhạt
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JTextField usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JPasswordField passwordField = new JPasswordField(20);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JPasswordField confirmPasswordField = new JPasswordField(20);

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

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Nút đăng ký
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        registerButton.setBackground(new Color(60, 179, 113)); // Màu xanh lá
        registerButton.setForeground(Color.WHITE); // Chữ trắng
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));

        backButton.setBackground(new Color(70, 130, 180)); // Màu xanh dương
        backButton.setForeground(Color.WHITE); // Chữ trắng
        backButton.setFont(new Font("Arial", Font.BOLD, 16));

        // Hiệu ứng hover
        addHoverEffect(registerButton, new Color(34, 139, 34));
        addHoverEffect(backButton, new Color(25, 120, 180));

        // Xử lý sự kiện nút đăng ký
        registerButton.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Gửi thông tin đăng ký tới server
            clientController.sendRegister(username, password);
            String response = clientController.listen();

            if (response != null && response.startsWith("REGISTER_SUCCESS")) {
                JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginView(clientController).setVisible(true); // Quay lại giao diện đăng nhập
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed! Username may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Xử lý sự kiện nút quay lại
        backButton.addActionListener((ActionEvent e) -> {
            new LoginView(clientController).setVisible(true); // Quay lại giao diện đăng nhập
            dispose(); // Đóng giao diện đăng ký
        });

        // Panel chứa các nút
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.add(registerButton);
        bottomPanel.add(backButton);

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
