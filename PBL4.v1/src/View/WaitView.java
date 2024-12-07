package View;

import Controller.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WaitView extends JFrame{
    private final ClientController clientController;
    private final String playerName;
    
    public WaitView(ClientController clientController, String playerName){
        this.clientController = clientController;
        this.playerName = playerName;
        Wait_View();
        new Thread(this::listenToServer).start();
    }
    
    private void listenToServer(){
        while(true){
            String response = clientController.listen();
            if (response != null && response.startsWith("GAME_STARTED")) {
                String[] parts = response.split(" ", 2);
                String boardData = parts[1];
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    GameView gameView = new GameView(clientController, playerName);
                    gameView.initializeBoard(boardData);
                    gameView.setVisible(true);
                });
                break;
            }
        }
    }
    
    private void Wait_View(){
        setLayout(new BorderLayout());
        
        // Tiêu đề
        JLabel titleLabel = new JLabel("WAITING", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(70, 130, 180));
        add(titleLabel, BorderLayout.NORTH);
        
        JButton backButton = new JButton("BACK");
        styleMainButton(backButton);
        backButton.setBounds(300, 300, 200, 40); // Nút căn giữa, kích thước nhỏ hơn và cách xuống dưới một chút
        backButton.addActionListener((ActionEvent e) -> {
            clientController.sendStartGame(playerName, 0);
            String response = clientController.listen();
            
            if (response != null && response.startsWith("START_GAME_SUCCESS")) {
                String[] parts = response.split(" ", 3);
                String roomId = parts[1];
                String boardData = parts[2];
                JOptionPane.showMessageDialog(this, "Joined room " + roomId, "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                LobbyView lobbyView = new LobbyView(clientController, playerName);
                lobbyView.setVisible(true);
            } else if (response != null && response.startsWith("START_GAME_FAILURE")) {
                JOptionPane.showMessageDialog(this, "Failed to start game: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(backButton);
    }
    
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
}
