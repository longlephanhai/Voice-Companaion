package com.emotionalai.view;

import javax.swing.*;
import java.awt.*;
import com.emotionalai.model.User;

public class HomeView extends JFrame {

    private JButton btnLogin, btnRegister, btnOpenAIChat;
    private User currentUser; // Lưu trạng thái user hiện tại

    public HomeView(User loggedInUser) {
        this.currentUser = loggedInUser;
        setupUI();
        attachListeners();
    }

    private void setupUI() {
        setTitle("🏠 Home - Emotional AI");
        setSize(600, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
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

        // Center
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(245, 247, 250));

        JLabel welcomeLabel = new JLabel("<html><center>Chào mừng bạn đến với Emotional AI!<br>Hãy đăng nhập hoặc mở AI Chat để bắt đầu.</center></html>", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(new Color(245, 247, 250));
        welcomePanel.add(welcomeLabel);
        centerPanel.add(welcomePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(245, 247, 250));

        // Nếu user đã đăng nhập thì nút Login/Register trở thành Logout
        if(currentUser != null) {
            btnLogin = createButton("Logout", new Color(244, 67, 54)); // màu đỏ
            btnRegister = null; // không cần hiển thị nữa
        } else {
            btnLogin = createButton("🔑 Login", new Color(33, 150, 243));
            btnRegister = createButton("📝 Register", new Color(76, 175, 80));
        }

        btnOpenAIChat = createButton("🚀 AI Chat", new Color(156, 39, 176));

        buttonPanel.add(btnLogin);
        if(btnRegister != null) buttonPanel.add(btnRegister);
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
        btnLogin.addActionListener(e -> {
            if(currentUser != null) {
                // Logout
                currentUser = null;
                JOptionPane.showMessageDialog(this, "Bạn đã đăng xuất!");
                SwingUtilities.invokeLater(() -> {
                    new HomeView(null).setVisible(true);
                    this.dispose();
                });
            } else {
                // Chuyển sang AuthView
                SwingUtilities.invokeLater(() -> {
                    AuthView authView = new AuthView();
                    authView.setVisible(true);
                    this.dispose();
                });
            }
        });

        if(btnRegister != null) {
            btnRegister.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    AuthView authView = new AuthView();
                    authView.setVisible(true);
                    this.dispose();
                });
            });
        }

        btnOpenAIChat.addActionListener(e -> {
            if(currentUser == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập trước khi mở AI Chat!");
                return;
            }
            SwingUtilities.invokeLater(() -> {
                AIClientView aiClientView = new AIClientView(currentUser);
                aiClientView.setVisible(true);
            });
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception e){ e.printStackTrace(); }
            new HomeView(null).setVisible(true); // bắt đầu chưa login
        });
    }
}
