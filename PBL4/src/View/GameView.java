package View;

import Controller.ClientController;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;

public class GameView extends JFrame {
    private ClientController clientController;
    private int[] board; // Bàn cờ nhận từ server
    private JButton[] buttons; // Các nút đại diện cho số trên bàn cờ
    private JTextArea gameLog;
    private String playerName;

    private JLabel player1ScoreLabel;
    private JLabel player2ScoreLabel;
    private PrintWriter out;
    private JLabel timeLabel;
    private Timer timer;
    private int elapsedTime;

    public GameView(ClientController clientController, String playerName) {
        this.clientController = clientController;
        this.playerName = playerName;

        setTitle("Game Room");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
        startTimer();
        new Thread(this::listenToServer).start();
    }

    private void createUI() {
        setLayout(new BorderLayout());

        // Bàn cờ
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(7, 7, 5, 5)); // Thêm khoảng cách giữa các nút
        boardPanel.setBackground(new Color(230, 240, 255));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttons = new JButton[9];

        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.BOLD, 16));
            buttons[i].setBackground(Color.WHITE);
            buttons[i].setFocusPainted(false);
            buttons[i].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));

            int index = i;
            buttons[i].addActionListener(e -> {
                if (buttons[index].isEnabled() && buttons[index].getText().length() > 0) {
                    int selectedNumber = Integer.parseInt(buttons[index].getText());
                    clientController.sendMove(playerName, selectedNumber);
                }
            });
            boardPanel.add(buttons[i]);
        }

        add(boardPanel, BorderLayout.CENTER);

        // Thông tin trò chơi
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(300, 0));
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        add(infoPanel, BorderLayout.EAST);

        // Tiêu đề
        JLabel titleLabel = new JLabel("NUMBER GAME");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 144, 255));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(titleLabel);

        // Điểm số và thời gian
        player1ScoreLabel = new JLabel("PLAYER 1: 0");
        player2ScoreLabel = new JLabel("PLAYER 2: 0");
        timeLabel = new JLabel("TIME: 00:00");

        Font infoFont = new Font("Arial", Font.BOLD, 18);
        player1ScoreLabel.setFont(infoFont);
        player2ScoreLabel.setFont(infoFont);
        timeLabel.setFont(infoFont);

        player1ScoreLabel.setForeground(Color.BLUE);
        player2ScoreLabel.setForeground(Color.BLUE);
        timeLabel.setForeground(Color.RED);

        player1ScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        player2ScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(player1ScoreLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(player2ScoreLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(timeLabel);

        // Nút thoát
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(245, 245, 245));
        JButton exitButton = new JButton("EXIT");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(new Color(220, 20, 60));
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        exitButton.addActionListener(e -> {
            clientController.sendExitGame(playerName);
            appendToLog("You have exited the game. You lose.");
            dispose();
            new LobbyView(clientController, playerName).setVisible(true);
            }); // Trả về giao diện Lobby
        
        buttonPanel.add(exitButton);
        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(buttonPanel);

        // Log game
        gameLog = new JTextArea(10, 20);
        gameLog.setFont(new Font("Monospaced", Font.PLAIN, 14));
        gameLog.setEditable(false);
        gameLog.setBackground(new Color(240, 255, 240));
        gameLog.setBorder(BorderFactory.createTitledBorder("Game Log"));
        JScrollPane logScrollPane = new JScrollPane(gameLog);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(logScrollPane);
    }

    private void listenToServer() {
        while (true) {
            String serverMessage = clientController.listen();
            if (serverMessage == null) continue;
            
            if (serverMessage.startsWith("START_GAME_SUCCESS")) {
                String boardData = serverMessage.substring(20); // Lấy dữ liệu bàn cờ sau lệnh
                initializeBoard(boardData); // Khởi tạo bàn cờ
                appendToLog("Game started with board data received from server.");
            } else if (serverMessage.startsWith("MOVE_SUCCESS")) {
                String[] parts = serverMessage.split(" ");
                String player = parts[1];
                int number = Integer.parseInt(parts[2]);

                SwingUtilities.invokeLater(() -> {
                    for (int i = 0; i < buttons.length; i++) {
                        if (buttons[i].getText().equals(String.valueOf(number))) {
                            buttons[i].setEnabled(false);
                            buttons[i].setBackground(player.equals(playerName) ? Color.RED : Color.BLUE);
                            break;
                        }
                    }
                });
            } else if (serverMessage.startsWith("MOVE_FAILURE")) {
                handleInvalidMove(serverMessage); // Xử lý bước đi sai
            } else if (serverMessage.startsWith("SCORE")) {
                String[] parts = serverMessage.split(" ");
                int player1Score = Integer.parseInt(parts[1]);
                int player2Score = Integer.parseInt(parts[2]);

                SwingUtilities.invokeLater(() -> {
                    player1ScoreLabel.setText("PLAYER 1: " + player1Score);
                    player2ScoreLabel.setText("PLAYER 2: " + player2Score);
                });
            } else if (serverMessage.startsWith("GAME_STARTED")) {
                String roomId = serverMessage.split(" ")[1];
                appendToLog("Game started in room " + roomId + "!");
            } else if (serverMessage.startsWith("MOVE")) {
                updateBoard(serverMessage);
            } else if (serverMessage.startsWith("EXIT_GAME_SUCCESS")) {
                appendToLog("You have exited the game. Returning to lobby.");
                dispose();
                new LobbyView(clientController, playerName).setVisible(true);
                break;
            } else if (serverMessage.startsWith("GAME_OVER")) {
                String[] parts = serverMessage.split(" ");
                String winner = parts.length > 1 ? parts[1] : "Unknown";

                // Kiểm tra người thắng
                boolean isWinner = winner.equals(playerName);

                // Lấy điểm số của người chơi hiện tại
                int playerScore = isWinner ? Integer.parseInt(player1ScoreLabel.getText().split(":")[1].trim()) : Integer.parseInt(player2ScoreLabel.getText().split(":")[1].trim());

                // 
                clientController.sendUpdatePlayerStats(playerName, playerScore, isWinner);
                String updateResponse = clientController.listen();
                if (updateResponse != null && updateResponse.startsWith("UPDATE_PLAYER_STATS_SUCCESS")) {
                    appendToLog("Game data updated successfully.");
                } else {
                    appendToLog("Failed to update game data.");
                }

                // Hiển thị thông báo game kết thúc
                appendToLog("Game over! Winner: " + winner);
                JOptionPane.showMessageDialog(
                    this,
                    "Game over!\nWinner: " + winner,
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE
                );

                // Quay lại giao diện Lobby
                dispose();
                new LobbyView(clientController, playerName).setVisible(true);
                break;
            } else {
                appendToLog("Server: " + serverMessage);
            }
        }
    }

    public void initializeBoard(String boardData) {
        if (boardData == null || boardData.isEmpty()) {
            appendToLog("Failed to initialize board: No data received.");
            return;
        }
        
        String[] numbers = boardData.split(",");
        board = new int[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            board[i] = Integer.parseInt(numbers[i]);
            buttons[i].setText(String.valueOf(board[i]));
            buttons[i].setEnabled(true); // Kích hoạt nút khi bàn cờ được tạo
        }
        appendToLog("Game started! Make your move.");
    }

    private void updateBoard(String moveData) {
        String[] parts = moveData.split(" ");
        String player = parts[1];
        System.out.println("Received from server: " + moveData);
        int number;
        try {
            number = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            appendToLog("Invalid move data received: " + moveData);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < board.length; i++) {
                if (board[i] == number) {
                    buttons[i].setEnabled(false);
                    buttons[i].setBackground(player.equals(playerName) ? Color.GREEN : Color.RED);
                    break;
                }
            }
            appendToLog(player + " selected number " + number);
        });
    }

    private void updateScores(String scoreData) {
        String[] parts = scoreData.split(" ");
        int player1Score = Integer.parseInt(parts[1]);
        int player2Score = Integer.parseInt(parts[2]);

        SwingUtilities.invokeLater(() -> {
            player1ScoreLabel.setText("PLAYER 1: " + player1Score);
            player2ScoreLabel.setText("PLAYER 2: " + player2Score);
        });
    }

    private void appendToLog(String message) {
        if (gameLog != null) {
            SwingUtilities.invokeLater(() -> gameLog.append(message + "\n"));
        }
    }

    private void startTimer() {
        elapsedTime = 0; // Thời gian bắt đầu
        timer = new Timer(1000, e -> {
            elapsedTime++;
            int minutes = elapsedTime / 60;
            int seconds = elapsedTime % 60;
            timeLabel.setText(String.format("TIME: %02d:%02d", minutes, seconds));
        });
        timer.start();
    }
    
    private void handleInvalidMove(String message) {
        // Hiển thị thông báo cho người chơi
        System.out.println("Invalid move. Try again!");
        appendToLog("Invalid move. Try again!");
        // Cho phép người chơi nhập lại
    }
}
