package com.emotionalai.view;

import com.emotionalai.model.User;
import com.emotionalai.rmi.ConversationInterface;
import com.emotionalai.config.RMIConnection;

import javax.swing.*;
import java.awt.*;

public class AuthView extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel loginPanel, registerPanel;

    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;
    private JButton btnLogin, btnBackHomeLogin;

    private JTextField regUsernameField, regEmailField;
    private JPasswordField regPasswordField;
    private JButton btnRegister, btnBackHomeRegister;

    private ConversationInterface ai;

    public AuthView() {
        ai = RMIConnection.getInstance().getAI(); // Lấy RMI instance
        setupUI();
        attachListeners();
    }

    private void setupUI() {
        setTitle("🔑 Login / Register - Emotional AI");
        setSize(500, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // -------------------- Login Panel --------------------
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        loginEmailField = new JTextField(20);
        loginPasswordField = new JPasswordField(20);
        btnLogin = createButton("Login", new Color(33, 150, 243));
        btnBackHomeLogin = createButton("🏠 Back Home", new Color(158, 158, 158));

        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(loginEmailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(loginPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        loginPanel.add(btnLogin, gbc);

        gbc.gridy = 3;
        loginPanel.add(btnBackHomeLogin, gbc);

        tabbedPane.addTab("Login", loginPanel);

        // -------------------- Register Panel --------------------
        registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setBackground(new Color(245, 247, 250));

        regUsernameField = new JTextField(20);
        regEmailField = new JTextField(20);
        regPasswordField = new JPasswordField(20);
        btnRegister = createButton("Register", new Color(76, 175, 80));
        btnBackHomeRegister = createButton("🏠 Back Home", new Color(158, 158, 158));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        registerPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        registerPanel.add(regUsernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        registerPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        registerPanel.add(regEmailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        registerPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        registerPanel.add(regPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        registerPanel.add(btnRegister, gbc);

        gbc.gridy = 4;
        registerPanel.add(btnBackHomeRegister, gbc);

        tabbedPane.addTab("Register", registerPanel);

        add(tabbedPane);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void attachListeners() {
        // Login action
        btnLogin.addActionListener(e -> {
            String email = loginEmailField.getText().trim();
            String password = new String(loginPasswordField.getPassword());

            if(email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin");
                return;
            }

            try {
                User user = ai.login(email, password); // Gọi RMI
                if(user != null) {
                    JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                    SwingUtilities.invokeLater(() -> {
                        HomeView home = new HomeView(user);
                        home.setVisible(true);
                        this.dispose();
                    });
                } else {
                    JOptionPane.showMessageDialog(this, "Email hoặc mật khẩu không đúng!");
                }
            } catch(Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi kết nối server RMI");
            }
        });

        // Register action
        btnRegister.addActionListener(e -> {
            String username = regUsernameField.getText().trim();
            String email = regEmailField.getText().trim();
            String password = new String(regPasswordField.getPassword());

            if(username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin");
                return;
            }

            try {
                User user = new User(username, email, password);
                boolean success = ai.register(user);
                if(success) {
                    JOptionPane.showMessageDialog(this, "Đăng ký thành công! Bạn có thể đăng nhập ngay.");
                    tabbedPane.setSelectedIndex(0); // chuyển sang Login
                } else {
                    JOptionPane.showMessageDialog(this, "Đăng ký thất bại, email có thể đã tồn tại.");
                }
            } catch(Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi kết nối server RMI");
            }
        });

        // Back to Home actions
        btnBackHomeLogin.addActionListener(e -> goBackHome());
        btnBackHomeRegister.addActionListener(e -> goBackHome());
    }

    private void goBackHome() {
        SwingUtilities.invokeLater(() -> {
            HomeView home = new HomeView(null);
            home.setVisible(true);
            this.dispose();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception e){ e.printStackTrace(); }
            new AuthView().setVisible(true);
        });
    }
}
