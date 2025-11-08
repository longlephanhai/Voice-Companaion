package com.emotionalai.view;

import com.emotionalai.model.User;
import com.emotionalai.config.RMIConnection;
import com.emotionalai.rmi.ConversationInterface;
import com.emotionalai.utils.VoiceRecorder;
import javazoom.jl.player.Player;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AIClientView extends JFrame {

    private JButton btnRecord, btnStop, btnPlay, btnSendAI;
    private JLabel lblStatus;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private String userAudioPath = "input.wav";
    private ConversationInterface ai;
    private Player currentPlayer;
    private User currentUser; // người dùng đang login

    public AIClientView(User user) {
        this.currentUser = user;
        ai = RMIConnection.getInstance().getAI();

        setupUI();
        attachListeners();
        updateUIForLogin();
    }

    private void setupUI() {
        setTitle("🎧 Emotional AI Chat");
        setSize(650, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Emotional AI Chat", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 90, 180));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(245, 247, 250));
        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        add(createControlPanel(), BorderLayout.SOUTH);

        addMessage("AI", "Xin chào! Hãy ghi âm và gửi cho tôi để bắt đầu trò chuyện!", false);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        lblStatus = new JLabel("Trạng thái: Sẵn sàng");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStatus.setForeground(Color.DARK_GRAY);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setBackground(Color.WHITE);

        btnRecord = createButton("🎤 Ghi âm", new Color(76, 175, 80));
        btnStop = createButton("⏹ Dừng", new Color(244, 67, 54));
        btnPlay = createButton("▶ Nghe", new Color(33, 150, 243));
        btnSendAI = createButton("🚀 Gửi AI", new Color(156, 39, 176));

        Dimension btnSize = new Dimension(120, 40);
        btnRecord.setPreferredSize(btnSize);
        btnStop.setPreferredSize(btnSize);
        btnPlay.setPreferredSize(btnSize);
        btnSendAI.setPreferredSize(btnSize);

        buttonPanel.add(btnRecord);
        buttonPanel.add(btnStop);
        buttonPanel.add(btnPlay);
        buttonPanel.add(btnSendAI);

        panel.add(lblStatus, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        btnStop.setEnabled(false);
        btnPlay.setEnabled(false);
        btnSendAI.setEnabled(false);

        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(color.darker(), 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void attachListeners() {
        btnRecord.addActionListener(e -> startRecording());
        btnStop.addActionListener(e -> stopRecording());
        btnPlay.addActionListener(e -> new Thread(() -> playMP3(userAudioPath)).start());
        btnSendAI.addActionListener(e -> new Thread(this::sendAudioToAI).start());
    }

    private void updateUIForLogin() {
        boolean loggedIn = currentUser != null;
        btnRecord.setEnabled(loggedIn);
        btnSendAI.setEnabled(loggedIn);
        if (!loggedIn) {
            lblStatus.setText("Vui lòng đăng nhập để sử dụng AI Chat");
        }
    }

    private void startRecording() {
        lblStatus.setText("Đang ghi âm...");
        btnRecord.setEnabled(false);
        btnStop.setEnabled(true);
        btnPlay.setEnabled(false);
        btnSendAI.setEnabled(false);
        new Thread(() -> VoiceRecorder.startRecording(userAudioPath)).start();
    }

    private void stopRecording() {
        VoiceRecorder.stopRecording();
        lblStatus.setText("Đã dừng ghi âm");
        btnRecord.setEnabled(true);
        btnStop.setEnabled(false);
        btnPlay.setEnabled(true);
        btnSendAI.setEnabled(true);
        addMessage("Bạn", "Đã ghi âm xong.", true);
    }

    private void sendAudioToAI() {
        if (ai == null) {
            addMessage("System", "Không có kết nối AI!", false);
            return;
        }
        try {
            lblStatus.setText("Đang gửi audio...");
            btnSendAI.setEnabled(false);
            btnRecord.setEnabled(false);

            addMessage("Bạn", "Đang gửi audio tới AI...", true);

            byte[] audioBytes = java.nio.file.Files.readAllBytes(new File(userAudioPath).toPath());
            String responseAudio = ai.talkWithUser(audioBytes);

            addMessage("AI", "AI đang xử lý...", false);
            lblStatus.setText("AI đã phản hồi");

            playMP3(responseAudio);

        } catch (Exception e) {
            e.printStackTrace();
            addMessage("AI", "Lỗi khi gọi AI: " + e.getMessage(), false);
            lblStatus.setText("Lỗi khi gọi AI");
        } finally {
            btnSendAI.setEnabled(true);
            btnRecord.setEnabled(true);
        }
    }

    private void playMP3(String filePath) {
        new Thread(() -> {
            try {
                if (currentPlayer != null) currentPlayer.close();
                FileInputStream fis = new FileInputStream(filePath);
                currentPlayer = new Player(fis);
                currentPlayer.play();
            } catch (Exception e) {
                e.printStackTrace();
                addMessage("System", "Lỗi phát audio: " + e.getMessage(), false);
            }
        }).start();
    }

    private void addMessage(String sender, String message, boolean isUser) {
        SwingUtilities.invokeLater(() -> {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(chatPanel.getBackground());
            wrapper.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JPanel msgPanel = new JPanel(new BorderLayout());
            msgPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            msgPanel.setBackground(isUser ? new Color(220, 248, 198) : Color.WHITE);

            JLabel senderLabel = new JLabel(sender);
            senderLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            senderLabel.setForeground(isUser ? new Color(0, 100, 0) : new Color(0, 0, 150));

            JLabel timeLabel = new JLabel(new SimpleDateFormat("HH:mm").format(new Date()));
            timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            timeLabel.setForeground(Color.GRAY);

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.add(senderLabel, BorderLayout.WEST);
            header.add(timeLabel, BorderLayout.EAST);

            JTextArea msgArea = new JTextArea(message);
            msgArea.setEditable(false);
            msgArea.setLineWrap(true);
            msgArea.setWrapStyleWord(true);
            msgArea.setBackground(msgPanel.getBackground());
            msgArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            msgArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

            msgPanel.add(header, BorderLayout.NORTH);
            msgPanel.add(msgArea, BorderLayout.CENTER);
            msgPanel.setMaximumSize(new Dimension(450, Integer.MAX_VALUE));

            wrapper.add(msgPanel, isUser ? BorderLayout.EAST : BorderLayout.WEST);
            chatPanel.add(wrapper);
            chatPanel.revalidate();
            chatPanel.repaint();

            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    @Override
    public void dispose() {
        if (currentPlayer != null) currentPlayer.close();
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception e){ e.printStackTrace(); }
            new AIClientView(null).setVisible(true);
        });
    }
}
