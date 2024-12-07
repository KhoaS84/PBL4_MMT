package View;

import Controller.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        setSize(800, 600); // Kích thước tổng thể cửa sổ
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
        startTimer();
        new Thread(this::listenToServer).start();
    }

    private void createUI() {
        setLayout(new BorderLayout());

        // Phần bên trái: Bàn cờ
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(10, 10)); // 10x10 bàn cờ
        buttons = new JButton[100];
        for (int i = 0; i < 100; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.BOLD, 12));
            int index = i;  // Tạo một bản sao cục bộ của `i` để sử dụng trong ActionListener
            boardPanel.add(buttons[i]);

            // Xử lý sự kiện nhấn nút
            buttons[i].addActionListener(e -> {
                if (buttons[index].isEnabled() && buttons[index].getText().length() > 0) {
                    int selectedNumber = Integer.parseInt(buttons[index].getText());
                    clientController.sendMove(playerName, selectedNumber); // Gửi số đến server
                }
            });
        }

        boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Viền đen
        add(boardPanel, BorderLayout.CENTER);

        // Phần bên phải: Thông tin trò chơi
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(250, 0)); // Chiều rộng cố định
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Viền đen
        add(infoPanel, BorderLayout.EAST);

        // Tiêu đề
        JLabel titleLabel = new JLabel("NUMBER GAME");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(Box.createVerticalStrut(20)); // Khoảng cách
        infoPanel.add(titleLabel);

        // Thông tin điểm số
        player1ScoreLabel = new JLabel("PLAYER 1: 0");
        player2ScoreLabel = new JLabel("PLAYER 2: 0");
        timeLabel = new JLabel("TIME: 00:00");
        player1ScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        player2ScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        player1ScoreLabel.setForeground(Color.BLUE);
        player2ScoreLabel.setForeground(Color.BLUE);
        timeLabel.setForeground(Color.BLUE);
        player1ScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        player2ScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(Box.createVerticalStrut(20)); // Khoảng cách
        infoPanel.add(player1ScoreLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(player2ScoreLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(timeLabel);

        // Nút EXIT và PAUSE
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JButton exitButton = new JButton("EXIT");
        JButton pauseButton = new JButton("PAUSE");

        exitButton.addActionListener(e -> {
            clientController.sendExitGame(playerName);
            dispose();
            new LobbyView(clientController, playerName).setVisible(true);
            }); // Trả về giao diện Lobby
        pauseButton.addActionListener(e -> pauseGame()); // Tạm dừng trò chơi

        exitButton.setForeground(Color.BLUE);
        pauseButton.setForeground(Color.BLUE);
        buttonPanel.add(exitButton);
        buttonPanel.add(pauseButton);
        infoPanel.add(Box.createVerticalGlue()); // Đẩy nút xuống cuối
        infoPanel.add(buttonPanel);
        
        // Log game(khởi tạo JTextArea)
        gameLog = new JTextArea(10, 20);
        gameLog.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(gameLog);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Game Log"));
        infoPanel.add(logScrollPane);
    }

    private void listenToServer() {
        while (true) {
            String serverMessage = clientController.listen();
            if (serverMessage == null) continue;
            
            // Xử lý START_GAME_SUCCESS
            if (serverMessage.startsWith("START_GAME_SUCCESS")) {
                String boardData = serverMessage.substring(20); // Lấy dữ liệu bàn cờ sau lệnh
                initializeBoard(boardData); // Khởi tạo bàn cờ
                appendToLog("Game started with board data received from server.");
            }
            
            //Xử lý MOVE_SUCCESS
            if (serverMessage.startsWith("MOVE_SUCCESS")) {
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
            }else if(serverMessage.startsWith("MOVE_FAILURE")){
                System.out.println("Invalid move: " + serverMessage);
                handleInvalidMove(serverMessage); // Xử lý bước đi sai
            }else if (serverMessage.startsWith("SCORE")) {
                String[] parts = serverMessage.split(" ");
                int player1Score = Integer.parseInt(parts[1]);
                int player2Score = Integer.parseInt(parts[2]);

                SwingUtilities.invokeLater(() -> {
                    player1ScoreLabel.setText("PLAYER 1: " + player1Score);
                    player2ScoreLabel.setText("PLAYER 2: " + player2Score);
                });
            } else if (serverMessage.startsWith("MOVE_FAILURE")) {
                String[] parts = serverMessage.split(" ", 3);
                String errorMessage = parts[2];
                appendToLog("Error: " + errorMessage);
            }
            
            if (serverMessage.startsWith("GAME_STARTED")) {
                String roomId = serverMessage.split(" ")[1];
                appendToLog("Game started in room " + roomId + "!");
            } else if (serverMessage.startsWith("MOVE")) {
                updateBoard(serverMessage);
            } else if (serverMessage.startsWith("SCORE")) {
                updateScores(serverMessage);
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
        int number = Integer.parseInt(parts[2]);

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

    private void pauseGame() {
        if (timer.isRunning()) {
            timer.stop();
        } else {
            timer.start();
        }
    }
    
    private void handleInvalidMove(String message) {
        // Hiển thị thông báo cho người chơi
        System.out.println("Invalid move. Try again!");
        // Cho phép người chơi nhập lại
    }
}
