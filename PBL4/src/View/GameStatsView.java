package View;

import Controller.ClientController;
import Model.Player;

import javax.swing.*;
import java.awt.*;

public class GameStatsView extends JFrame {
    private final ClientController clientController;
    private final String username;

    public GameStatsView(ClientController clientController, String username) {
        this.clientController = clientController;
        this.username = username;

        setTitle("Game Stats");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
    }

    private void createUI() {
        clientController.sendGetPlayerStats(username);
        String response = clientController.listen();
        
        if (response == null || response.startsWith("GET_PLAYER_STATS_FAILURE")) {
            JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Parse response
        String[] parts = response.split(" ");
        Player player = new Player(
            Integer.parseInt(parts[1]),
            parts[2],
            "", // Password không cần thiết ở đây
            Integer.parseInt(parts[3]),
            Integer.parseInt(parts[4]),
            Integer.parseInt(parts[5])
        );
        
        setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 144, 255));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JLabel titleLabel = new JLabel("Game Statistics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Stats panel
        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"Username:", "High Score:", "Games Played:", "Games Won:"};
        String[] values = {
            player.getUsername(),
            String.valueOf(player.getHighScore()),
            String.valueOf(player.getGamesPlayed()),
            String.valueOf(player.getGamesWon())
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.PLAIN, 18));
            statsPanel.add(label, gbc);

            gbc.gridx = 1;
            JLabel valueLabel = new JLabel(values[i]);
            valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
            valueLabel.setForeground(new Color(30, 144, 255));
            statsPanel.add(valueLabel, gbc);
        }

        add(statsPanel, BorderLayout.CENTER);

        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(240, 240, 240));
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));
        backButton.setBackground(new Color(30, 144, 255));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.addActionListener(e -> {
            new LobbyView(clientController, username).setVisible(true);
            dispose();
        });

        footerPanel.add(backButton);
        add(footerPanel, BorderLayout.SOUTH);
    }
}