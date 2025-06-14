package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class HelpView extends JFrame {
    public HelpView(LobbyView lobbyView) {
        setTitle("Help");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); // Sử dụng null layout để tùy chỉnh giao diện

        // Tiêu đề
        JLabel titleLabel = new JLabel("Luật Chơi và Thông Tin Nhà Phát Hành", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 144, 255));
        titleLabel.setBounds(100, 20, 600, 40);
        add(titleLabel);

        // Nội dung luật chơi
        JTextArea rulesText = new JTextArea(
                """
                LUẬT CHƠI:
                1. Mỗi người chơi sẽ được chọn số theo thứ tự từ 1 đến 100. 
                2. Người nào chọn được nhiều số hơn sẽ thắng.
                3. Nếu cả hai chọn cùng số lượng, kết quả sẽ hòa.
                
                NHÀ PHÁT HÀNH:
                - Tên: GameNumber
                - Người thực hiện   
                                     Hồ Nguyễn Thế Vinh
                                     Lê Hải Khoa
                                     Ngô Xuân Vinh
                                  
                
                """
        );
        rulesText.setFont(new Font("Arial", Font.PLAIN, 16));
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);
        rulesText.setEditable(false);
        rulesText.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(rulesText); // Đặt nội dung vào JScrollPane
        scrollPane.setBounds(50, 80, 700, 400);
        add(scrollPane);

        // Nút BACK
        JButton backButton = new JButton("BACK");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setForeground(new Color(30, 144, 255));
        backButton.setBackground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255), 2));
        backButton.setBounds(350, 500, 100, 40);

        backButton.addActionListener((ActionEvent e) -> {
            this.dispose(); // Đóng giao diện HelpView
            lobbyView.setVisible(true); // Hiển thị lại LobbyView
        });

        add(backButton);
    }
}
