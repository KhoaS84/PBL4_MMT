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
                    String[] parts = response.split(" ", 2);
                    String boardData = parts[1];
                    SwingUtilities.invokeLater(() -> {
                        dispose();
                        GameView gameView = new GameView(clientController, playerName);
                        gameView.initializeBoard(boardData);
                        gameView.setVisible(true);
                    });
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
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 144, 255));

        JLabel titleLabel = new JLabel("Waiting Room", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Center Panel with Illustration and Message
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1;

        JLabel illustrationLabel = new JLabel();
        illustrationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        illustrationLabel.setIcon(new ImageIcon("resources/waiting.png")); // Add an illustration
        centerPanel.add(illustrationLabel, gbc);

        gbc.gridy++;
        JLabel waitingMessage = new JLabel("Finding an opponent for you...", JLabel.CENTER);
        waitingMessage.setFont(new Font("Arial", Font.PLAIN, 18));
        waitingMessage.setForeground(new Color(70, 130, 180));
        centerPanel.add(waitingMessage, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Footer Panel with Back Button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(245, 245, 245));

        JButton backButton = new JButton("Back to Lobby");
        styleMainButton(backButton);
        backButton.addActionListener((ActionEvent e) -> clientController.sendLeaveRoom(playerName, 0));

        footerPanel.add(backButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void styleMainButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(30, 144, 255));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));

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
