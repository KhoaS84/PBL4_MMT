/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DEMO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

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

    public Client_View(ActionListener starListener, ActionListener buttonListener) {
        frame = new JFrame("Number Finding Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        //frame.setLayout(new GridLayout(10, 10));

        //Start Panel
        startPanel = new JPanel(new BorderLayout());
        startButton = new JButton("START");
        startButton.addActionListener(starListener);
        startPanel.add(startButton, BorderLayout.CENTER);
        
        //Wait Panel
        waitPanel = new JPanel(new BorderLayout());
        JLabel waitLabel = new JLabel("Waiting for another player...");
        waitLabel.setFont(new Font("Arial", Font.BOLD, 24));
        waitPanel.add(waitLabel, BorderLayout.CENTER);
        
        //GamePanel
        gamePanel = new JPanel(new GridLayout(10,10));
        for (int i = 0; i < 100; i++) {
            numberButtons[i] = new JButton("");
            numberButtons[i].addActionListener(buttonListener);
            gamePanel.add(numberButtons[i]);
        }

        frame.add(startPanel);
        frame.setVisible(true);
    }
    
    public void showWaitScreen(){
        frame.getContentPane().removeAll();
        frame.add(waitPanel);
        frame.revalidate();
        frame.repaint();
    }
    
    public void showGameScreen(){
        frame.getContentPane().removeAll();
        frame.add(gamePanel);
        frame.revalidate();
        frame.repaint();
    }

    public void setButtonText(int index, String text) {
        numberButtons[index].setText(text);
    }

    public void disableButton(int number) {
        for (JButton button : numberButtons) {
            if (button.getText().equals(String.valueOf(number))) {
                button.setEnabled(false);
                break;
            }
        }
    }

    public String getButtonText(int index) {
        return numberButtons[index].getText();
    }
}
