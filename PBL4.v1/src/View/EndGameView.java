package View;

import javax.swing.*;
import java.awt.*;

public class EndGameView {
    private JFrame frame;
    private JLabel resultLabel;
    private JButton btnExit;

    public EndGameView() {
        frame = new JFrame("Game Over");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        resultLabel = new JLabel("Game Over", JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 30));

        btnExit = new JButton("Exit");

        frame.add(resultLabel, BorderLayout.CENTER);
        frame.add(btnExit, BorderLayout.SOUTH);

        frame.setSize(300, 150);
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
