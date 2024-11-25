/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DEMO.CLIENT.CONTROLLER;

import DEMO.CLIENT.VIEW.Client_View;
import java.awt.Color;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 *
 * @author KHOA
 */
public class Client_GameNumber{
    public Client_View view;
    private Client_Connection connection;

    public Client_GameNumber() {
        view = new Client_View(new StarListener(), new ButtonListener());
        connection = new Client_Connection(this);
    }

    public void setupButtons(List<Integer> numbers) {
        for (int i = 0; i < 100; i++) {
            view.setButtonText(i, String.valueOf(numbers.get(i)));
        }
    }
    
    public void updateButtonColor(int number, Color color){
        view.setButtonColor(number, color);
    }

    public void showGameScreen(){
        view.showGameScreen();
    }
    
    
    
    private class StarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            connection.connectToServer();
        }
    }

    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            String text = source.getText();
            if (!text.isEmpty()) {
                int selectedNumber = Integer.parseInt(text);
                connection.sendNumber(selectedNumber);
            }
        }
    }
    
    public void updateScores(int player1Score, int player2Score) {
        view.updateScores(player1Score, player2Score);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client_GameNumber());
    }
}