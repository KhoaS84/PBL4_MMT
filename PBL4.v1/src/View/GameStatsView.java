package View;

import Controller.ClientController;
import Model.DatabaseManager;
import Model.Player;

import javax.swing.*;
import java.awt.*;

public class GameStatsView extends JFrame {
    private final ClientController clientController;
    private final String username;
    private final DatabaseManager databaseManager; // Thêm đối tượng DatabaseManager

    public GameStatsView(ClientController clientController, String username) {
        this.clientController = clientController;
        this.username = username;
        this.databaseManager = new DatabaseManager();  // Khởi tạo DatabaseManager

        setTitle("Game Stats");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
    }

    private void createUI() {
        // Lấy thông tin người chơi từ DatabaseManager
        Player player = databaseManager.getUserByUsername(username);

        if (player == null) {
            JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setLayout(new BorderLayout());

        // Tiêu đề
        JLabel titleLabel = new JLabel("Game Statistics", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 144, 255));
        add(titleLabel, BorderLayout.NORTH);

        // Bảng thông tin
        JPanel statsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        statsPanel.add(new JLabel("Username:"));
        statsPanel.add(new JLabel(player.getUsername()));

        statsPanel.add(new JLabel("High Score:"));
        statsPanel.add(new JLabel(String.valueOf(player.getHighScore())));

        statsPanel.add(new JLabel("Games Played:"));
        statsPanel.add(new JLabel(String.valueOf(player.getGamesPlayed())));

        statsPanel.add(new JLabel("Games Won:"));
        statsPanel.add(new JLabel(String.valueOf(player.getGamesWon())));

        add(statsPanel, BorderLayout.CENTER);

        // Nút "Back"
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            new LobbyView(clientController, username).setVisible(true);
            dispose();
        });
        add(backButton, BorderLayout.SOUTH);
    }
}
