package View;

import Controller.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WaitView extends JFrame {
    private final ClientController clientController;
    private final String playerName;

    public WaitView(ClientController clientController, String playerName) {
        this.clientController = clientController;
        this.playerName = playerName;
        initializeUI();
        new Thread(this::listenToServer).start();
    }

    private void listenToServer() {
        while (true) {
            String response = clientController.listen();
            if (response != null) {
                if (response.startsWith("GAME_STARTED")) {
                    String[] parts = response.split(" ");
                    System.out.println(parts.length);
                    if(parts.length == 2){
                        String boardData = parts[1];
                        SwingUtilities.invokeLater(() -> {
                            dispose();
                            GameView gameView = new GameView(clientController, playerName);
                            gameView.initializeBoard(boardData);
                            gameView.setVisible(true);
                        });
                    } else if(parts.length == 3){
                        String roomId = parts[1];
                        String boardData = parts[2];
                        SwingUtilities.invokeLater(() -> {
                            dispose();
                            GameView gameView = new GameView(clientController, playerName);
                            gameView.initializeBoard(boardData);
                            gameView.setVisible(true);
                        });
                    }
                    break;
                } else if (response.startsWith("LEAVE_ROOM_SUCCESS")) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Left the room successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        LobbyView lobbyView = new LobbyView(clientController, playerName);
                        lobbyView.setVisible(true);
                    });
                    break;
                } else if (response.startsWith("LEAVE_ROOM_FAILURE")) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Failed to leave the room: " + response, "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }
        }
    }

    private void initializeUI() {
        setTitle("Waiting Room");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setUndecorated(true);  // Remove default window border for a modern look
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);  // Remove the default title bar

        // Main Container
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(255, 255, 255));
        add(mainPanel);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 144, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel("Waiting Room", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Panel with Illustration and Message
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1;

        JLabel illustrationLabel = new JLabel();
        illustrationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        illustrationLabel.setIcon(new ImageIcon("resources/waiting.png")); // Add an illustration
        centerPanel.add(illustrationLabel, gbc);

        gbc.gridy++;
        JLabel waitingMessage = new JLabel("Finding an opponent for you...", JLabel.CENTER);
        waitingMessage.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        waitingMessage.setForeground(new Color(70, 130, 180));
        centerPanel.add(waitingMessage, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer Panel with Back Button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(245, 245, 245));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JButton backButton = new JButton("Back to Lobby");
        styleMainButton(backButton);
        backButton.addActionListener((ActionEvent e) -> clientController.sendLeaveRoom(playerName, 0));

        footerPanel.add(backButton);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private void styleMainButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(30, 144, 255));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 50));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 144, 255));
            }
        });
    }
}
