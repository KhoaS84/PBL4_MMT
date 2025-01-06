package View;

import javax.swing.*;
import java.awt.*;

public class EndGameView {
    private JFrame frame;
    private JLabel resultLabel;
    private JButton btnExit;

    public EndGameView() {
        // Create the main frame
        frame = new JFrame("Game Over");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Set background color for the frame
        frame.getContentPane().setBackground(new Color(40, 44, 52));

        // Create and style the result label
        resultLabel = new JLabel("Game Over", JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 36));
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Create and style the exit button
        btnExit = new JButton("Exit");
        btnExit.setFont(new Font("Arial", Font.PLAIN, 18));
        btnExit.setBackground(new Color(220, 53, 69)); // Red button
        btnExit.setForeground(Color.WHITE);
        btnExit.setFocusPainted(false);
        btnExit.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Add hover effect to the button
        btnExit.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnExit.setBackground(new Color(200, 35, 50));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnExit.setBackground(new Color(220, 53, 69));
            }
        });

        // Add components to the frame
        frame.add(resultLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(40, 44, 52));
        buttonPanel.add(btnExit);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Set frame size and visibility
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }

    public JLabel getResultLabel() {
        return resultLabel;
    }

    public JButton getExitButton() {
        return btnExit;
    }

    public JFrame getFrame() {
        return frame;
    }
}
