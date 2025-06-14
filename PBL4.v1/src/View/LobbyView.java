package View;

import Controller.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LobbyView extends JFrame {
    private final ClientController clientController;
    private final String playerName;

    public LobbyView(ClientController clientController, String playerName) {
        this.clientController = clientController;
        this.playerName = playerName;

        setTitle("Lobby");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
    }

    private void createUI() {
        setLayout(null); // Dùng layout null để định vị chính xác
        getContentPane().setBackground(Color.WHITE);

        // Tiêu đề "WELCOME"
        JLabel welcomeLabel = new JLabel("WELCOME", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 40));
        welcomeLabel.setForeground(new Color(30, 144, 255));
        welcomeLabel.setBounds(250, 50, 300, 50); // Căn giữa ở trên
        add(welcomeLabel);

        // Nút PLAY
        JButton playButton = new JButton("PLAY");
        styleMainButton(playButton);
        playButton.setBounds(300, 300, 200, 40); // Nút căn giữa, kích thước nhỏ hơn và cách xuống dưới một chút
        playButton.addActionListener((ActionEvent e) -> {
            clientController.sendStartGame(playerName, 0);
            String response = clientController.listen();
            
            if (response != null && response.startsWith("START_GAME_SUCCESS")) {
                String[] parts = response.split(" ", 3);
                String roomId = parts[1];
                String boardData = parts[2];
                JOptionPane.showMessageDialog(this, "Joined room " + roomId, "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                WaitView waitView = new WaitView(clientController, playerName); //Hiển thị WaitView
                waitView.setVisible(true);
            } else if (response != null && response.startsWith("START_GAME_FAILURE")) {
                JOptionPane.showMessageDialog(this, "Failed to start game: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(playButton);

        // Nút JOIN ROOM
        JButton joinRoomButton = new JButton("JOIN ROOM");
        styleMainButton(joinRoomButton);			
        joinRoomButton.setBounds(300, 360, 200, 40); // Nút căn giữa bên dưới, kích thước nhỏ hơn
        joinRoomButton.addActionListener((ActionEvent e) -> {
            String roomName = JOptionPane.showInputDialog(this, "Enter Room Name:");
            if (roomName != null && !roomName.trim().isEmpty()) {
                clientController.sendRoom(roomName);
                dispose();
                new GameView(clientController, playerName).setVisible(true); // Chuyển đến giao diện trò chơi
            }
        });
        add(joinRoomButton);

        // Nút gamestats
        JButton buttongamestats = new JButton("GAME STATS");
        styleMainButton(buttongamestats);
        buttongamestats.setBounds(300, 420, 200, 40); // Nút dưới JOIN ROOM, kích thước nhỏ hơn
        buttongamestats.addActionListener((ActionEvent e) -> {
//            this.setVisible(false); // Ẩn giao diện hiện tại
            new GameStatsView(clientController, playerName).setVisible(true); // Truyền đúng tham số vào constructor
        });
        add(buttongamestats);


        // Nút HELP
        JButton helpButton = new JButton("HELP");
        styleSideButton(helpButton);
        helpButton.setBounds(50, 500, 80, 30); // Nút góc trái dưới, kích thước nhỏ hơn
        helpButton.addActionListener((ActionEvent e) -> {
            this.setVisible(false); // Ẩn giao diện hiện tại
            new HelpView(this).setVisible(true); // Hiển thị giao diện HelpView
        });

        add(helpButton);


        JButton exitButton = new JButton("LOGOUT");
        styleSideButton(exitButton);
        exitButton.setBounds(650, 500, 80, 30); // Vị trí góc phải dưới
        exitButton.addActionListener((ActionEvent e) -> {
            // Hiển thị hộp thoại xác nhận
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // Gửi yêu cầu đăng xuất
                clientController.sendLogout(playerName);
                String response = clientController.listen();

                if (response != null && response.startsWith("LOGOUT_SUCCESS")) {
                    JOptionPane.showMessageDialog(this, "Logged out successfully!", "Logout", JOptionPane.INFORMATION_MESSAGE);

                    // Hiển thị LoginView
                    new LoginView(clientController).setVisible(true);
                    dispose(); // Đóng LobbyView
                } else {
                    JOptionPane.showMessageDialog(this, "Logout failed: " + response, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            // Nếu chọn NO, không cần làm gì
        });
        add(exitButton);
    }

    // Hàm định dạng cho các nút chính (PLAY, JOIN ROOM, HISTORY)
    private void styleMainButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Giảm kích thước chữ một chút
        button.setForeground(new Color(30, 144, 255));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255), 2));

        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 248, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
    }

    // Hàm định dạng cho các nút bên góc (HELP, BACK)
    private void styleSideButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14)); // Giảm kích thước chữ cho nút HELP và BACK
        button.setForeground(new Color(30, 144, 255));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255), 2));

        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 248, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
    }
}
