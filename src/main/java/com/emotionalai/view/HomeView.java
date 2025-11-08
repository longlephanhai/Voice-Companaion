package com.emotionalai.view;

import javax.swing.*;
import java.awt.*;

public class HomeView extends JFrame {

    private JButton btnLogin, btnRegister, btnOpenAIChat;

    public HomeView() {
        setupUI();
        attachListeners();
    }

    private void setupUI() {
        setTitle("🏠 Home - Emotional AI");
        setSize(600, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // -------------------- Header --------------------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setPreferredSize(new Dimension(600, 120));

        JLabel title = new JLabel("Emotional AI", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Đối tác tâm lý số của bạn - Giao tiếp bằng giọng nói thông minh", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(Color.WHITE);

        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // -------------------- Center Panel --------------------
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(new Color(245, 247, 250));

        // Welcome message panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(new Color(245, 247, 250));
        JLabel welcomeLabel = new JLabel("<html><center>Chào mừng bạn đến với Emotional AI!<br>Hãy đăng nhập hoặc mở AI Chat để bắt đầu.</center></html>", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        welcomePanel.add(welcomeLabel);
        centerPanel.add(welcomePanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(245, 247, 250));

        btnLogin = createButton("🔑 Login", new Color(33, 150, 243));
        btnRegister = createButton("📝 Register", new Color(76, 175, 80));
        btnOpenAIChat = createButton("🚀 AI Chat", new Color(156, 39, 176));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnOpenAIChat);

        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void attachListeners() {
        btnLogin.addActionListener(e -> JOptionPane.showMessageDialog(this, "Hiện tại chức năng Login chưa triển khai"));
        btnRegister.addActionListener(e -> JOptionPane.showMessageDialog(this, "Hiện tại chức năng Register chưa triển khai"));
        btnOpenAIChat.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            AIClientView aiClientView = new AIClientView();
            aiClientView.setVisible(true);
        }));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new HomeView().setVisible(true);
        });
    }
}
