/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DEMO.CLIENT.VIEW;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author KHOA
 */
public class Client_View {
    private JFrame frame;
    private JPanel gamePanel;
    private JPanel startPanel;
    private JPanel waitPanel;
    private JButton startButton;
    private JButton[] numberButtons = new JButton[100];
    private JButton exitButton;
    private JButton pauseButton;
    private JLabel player1ScoreLabel;
    private JLabel player2ScoreLabel;
    private JLabel timeLabel;
    private JLabel countdownLabel;
    private Timer timer;
    private int remainingTime;

    public Client_View(ActionListener starListener, ActionListener buttonListener) {
        frame = new JFrame("Number Finding Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);

        // Start Panel
        startPanel = new JPanel(new BorderLayout());
        startButton = new JButton("START");
        startButton.addActionListener(starListener);
        startPanel.add(startButton, BorderLayout.CENTER);

        // Wait Panel
        waitPanel = new JPanel(new BorderLayout());
        JLabel waitLabel = new JLabel("Waiting for another player...");
        waitLabel.setFont(new Font("Arial", Font.BOLD, 24));
        waitPanel.add(waitLabel, BorderLayout.CENTER);

        // Game Panel
        gamePanel = new JPanel();
        gamePanel.setLayout(new BorderLayout());

        // GridPanel for Numbers
        JPanel gridPanel = new JPanel(new GridLayout(10, 10));
        for (int i = 0; i < 100; i++) {
            numberButtons[i] = new JButton("");
            numberButtons[i].addActionListener(buttonListener);
            gridPanel.add(numberButtons[i]);
        }

        // Info Panel (Right Side)
        JPanel infoPanel = new JPanel(new BorderLayout());

        // Title Section
        JPanel titlePanel = new JPanel(new FlowLayout());
        JLabel titleLabel = new JLabel("NUMBER GAME");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // Score and Time Section
        JPanel scorePanel = new JPanel(new GridLayout(3, 2, 5, 10));
        player1ScoreLabel = new JLabel("PLAYER 1 :");
        player1ScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel player1ValueLabel = new JLabel("0");
        player1ValueLabel.setFont(new Font("Arial", Font.BOLD, 18));

        player2ScoreLabel = new JLabel("PLAYER 2 :");
        player2ScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel player2ValueLabel = new JLabel("0");
        player2ValueLabel.setFont(new Font("Arial", Font.BOLD, 18));

        timeLabel = new JLabel("TIME :");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        countdownLabel = new JLabel("120");
        countdownLabel.setFont(new Font("Arial", Font.BOLD, 18));

        scorePanel.add(player1ScoreLabel);
        scorePanel.add(player1ValueLabel);
        scorePanel.add(player2ScoreLabel);
        scorePanel.add(player2ValueLabel);
        scorePanel.add(timeLabel);
        scorePanel.add(countdownLabel);

        // Button Section
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        exitButton = new JButton("EXIT");
        pauseButton = new JButton("PAUSE");

        buttonPanel.add(exitButton);
        buttonPanel.add(pauseButton);

        // Assemble Info Panel
        infoPanel.add(titlePanel, BorderLayout.NORTH);
        infoPanel.add(scorePanel, BorderLayout.CENTER);
        infoPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Assemble Game Panel
        gamePanel.add(gridPanel, BorderLayout.CENTER);
        gamePanel.add(infoPanel, BorderLayout.EAST);

        frame.add(startPanel);
        frame.setVisible(true);
    }

    public void showWaitScreen() {
        frame.getContentPane().removeAll();
        frame.add(waitPanel);
        frame.revalidate();
        frame.repaint();
    }

    public void showGameScreen() {
        frame.getContentPane().removeAll();
        frame.add(gamePanel);
        frame.revalidate();
        frame.repaint();
        startCountdown(120);
    }

    public void setButtonText(int index, String text) {
        numberButtons[index].setText(text);
    }

    public void setButtonColor(int number, Color color) {
        for (JButton button : numberButtons) {
            if (button.getText().equals(String.valueOf(number))) {
                button.setBackground(color);
                button.setOpaque(true);
                button.setBorderPainted(false);
                break;
            }
        }
    }

    public String getButtonText(int index) {
        return numberButtons[index].getText();
    }
    
        public void startCountdown(int seconds) {
        remainingTime = seconds;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (remainingTime > 0) {
                    remainingTime--;
                    countdownLabel.setText(String.valueOf(remainingTime));
                } else {
                    timer.cancel();
                    countdownLabel.setText("0");
                    // Handle end of timer logic here if needed
                }
            }
        }, 0, 1000);
    }

    public void stopCountdown() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void previewGameRoom() {
        showGameScreen();
    }
    
    public void updateScores(int player1Score, int player2Score) {
        player1ScoreLabel.setText("PLAYER 1: " + player1Score);
        player2ScoreLabel.setText("PLAYER 2: " + player2Score);
    }
}
