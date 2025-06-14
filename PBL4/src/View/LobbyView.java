package View;

import Controller.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LobbyView extends JFrame {
    private final ClientController clientController;
    private final String playerName;

    // Khung của giao diện Lobby
    public LobbyView(ClientController clientController, String playerName) {
        this.clientController = clientController;
        this.playerName = playerName;

        setTitle("Lobby");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
    }

    // Tạo giao diện cho Lobby
    private void createUI() {
        setLayout(null); // Dùng layout null để định vị chính xác
        getContentPane().setBackground(new Color(240, 248, 255)); // Màu nền nhẹ nhàng

        // Tiêu đề "WELCOME"
        JLabel welcomeLabel = new JLabel("WELCOME", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        welcomeLabel.setForeground(new Color(30, 144, 255));
        welcomeLabel.setBounds(250, 50, 300, 50); // Căn giữa ở trên
        add(welcomeLabel);

        // Nút PLAY
        JButton playButton = new JButton("PLAY");
        styleMainButton(playButton);
        playButton.setBounds(300, 300, 200, 50); // Tăng kích thước nút một chút
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
        joinRoomButton.setBounds(300, 360, 200, 50); // Tăng kích thước nút một chút
        joinRoomButton.addActionListener((ActionEvent e) -> {
            // Tạo panel để nhập idRoom và nameRoom
            JPanel panel = new JPanel(new GridLayout(2, 2));
            panel.add(new JLabel("Enter Room ID:"));
            JTextField idField = new JTextField();
            panel.add(idField);
            panel.add(new JLabel("Enter Room Name:"));
            JTextField nameField = new JTextField();
            panel.add(nameField);

            // Hiển thị hộp thoại nhập
            int result = JOptionPane.showConfirmDialog(
                this, panel, "Join Room", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String idRoom = idField.getText().trim();
                String nameRoom = nameField.getText().trim();

                // Kiểm tra input
                if (idRoom.isEmpty() || nameRoom.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Room ID and Name cannot be empty.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Gửi yêu cầu kiểm tra trạng thái phòng
                clientController.sendCheckRoom(playerName, idRoom, nameRoom);
                dispose();
                new WaitView(clientController, playerName).setVisible(true);
                
//String response = clientController.listen();

//                // Xử lý phản hồi từ server
//                if (response != null && response.startsWith("ROOM_OPEN")) {
//                    JOptionPane.showMessageDialog(this, "Successfully joined room: " + nameRoom, "Success", JOptionPane.INFORMATION_MESSAGE);
//                    dispose();
//                    WaitView waitView = new WaitView(clientController, playerName); //Hiển thị WaitView
//                    waitView.setVisible(true);
//                } else if (response != null && response.startsWith("ROOM_CLOSED")) {
//                    JOptionPane.showMessageDialog(this, "Room is full or not available.", "Error", JOptionPane.ERROR_MESSAGE);
//                } else {
//                    JOptionPane.showMessageDialog(this, "Unexpected error occurred while joining room.", "Error", JOptionPane.ERROR_MESSAGE);
//                }
            }
//            // Hiển thị hộp thoại nhập tên phòng
//            String roomName = JOptionPane.showInputDialog(this, "Enter Room Name or ID:");

//            // Kiểm tra tên phòng hợp lệ
//            if (roomName == null || roomName.trim().isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Room name cannot be empty.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//
//            // Gửi yêu cầu tham gia phòng
//            clientController.sendRoom(playerName, roomName); // Gọi phương thức trong ClientController
//            String response = clientController.listen(); // Nhận phản hồi từ server
//
//            // Xử lý phản hồi từ server
//            if (response != null && response.startsWith("JOIN_ROOM_SUCCESS")) {
//                // Tham gia phòng thành công
//                JOptionPane.showMessageDialog(this, "Successfully joined room: " + roomName, "Success", JOptionPane.INFORMATION_MESSAGE);
//                dispose(); // Đóng giao diện hiện tại
//                new GameView(clientController, playerName).setVisible(true); // Chuyển tới giao diện trò chơi
//            } else if (response != null && response.startsWith("JOIN_ROOM_FAILURE")) {
//                // Thất bại, hiển thị lỗi chi tiết
//                JOptionPane.showMessageDialog(this, "Failed to join room: " + response.substring(16), "Error", JOptionPane.ERROR_MESSAGE);
//            } else {
//                // Phản hồi không mong đợi
//                JOptionPane.showMessageDialog(this, "Unexpected error occurred while joining room.", "Error", JOptionPane.ERROR_MESSAGE);
//            }
        });
        add(joinRoomButton);

        // Nút GAME STATS
        JButton buttongamestats = new JButton("GAME STATS");
        styleMainButton(buttongamestats);
        buttongamestats.setBounds(300, 420, 200, 50); // Tăng kích thước nút một chút
        buttongamestats.addActionListener((ActionEvent e) -> {
            dispose();
            new GameStatsView(clientController, playerName).setVisible(true); // Truyền đúng tham số vào constructor
        });
        add(buttongamestats);

        // Nút HELP
        JButton helpButton = new JButton("HELP");
        styleSideButton(helpButton);
        helpButton.setBounds(50, 500, 100, 40); // Nút góc trái dưới, kích thước lớn hơn
        helpButton.addActionListener((ActionEvent e) -> {
            this.setVisible(false); // Ẩn giao diện hiện tại
            new HelpView(this).setVisible(true); // Hiển thị giao diện HelpView
        });
        add(helpButton);

        // Nút LOGOUT
        JButton exitButton = new JButton("LOGOUT");
        styleSideButton(exitButton);
        exitButton.setBounds(650, 500, 100, 40); // Vị trí góc phải dưới
        exitButton.addActionListener((ActionEvent e) -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                clientController.sendLogout(playerName);
                String response = clientController.listen();

                if (response != null && response.startsWith("LOGOUT_SUCCESS")) {
                    JOptionPane.showMessageDialog(this, "Logged out successfully!", "Logout", JOptionPane.INFORMATION_MESSAGE);
                    new LoginView(clientController).setVisible(true);
                    dispose(); // Đóng LobbyView
                } else {
                    JOptionPane.showMessageDialog(this, "Logout failed: " + response, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(exitButton);
    }

    // Hàm định dạng cho các nút chính (PLAY, JOIN ROOM, GAME STATS)
    private void styleMainButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Phông chữ đẹp hơn
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(30, 144, 255)); // Màu xanh dương tươi sáng
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255), 2));

        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180)); // Màu khi hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 144, 255)); // Màu ban đầu
            }
        });
    }

    // Hàm định dạng cho các nút bên góc (HELP, LOGOUT)
    private void styleSideButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Phông chữ đẹp hơn
        button.setForeground(new Color(30, 144, 255));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255), 2));

        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 248, 255)); // Màu khi hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE); // Màu ban đầu
            }
        });
    }
}
