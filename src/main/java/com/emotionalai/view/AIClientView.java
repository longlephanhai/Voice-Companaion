package com.emotionalai.view;

import com.emotionalai.rmi.ConversationInterface;
import com.emotionalai.utils.VoiceRecorder;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AIClientView extends JFrame {

    private JButton btnRecord, btnStop, btnPlay, btnSendAI;
    private JLabel lblStatus;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private String userAudioPath = "input.wav";
    private ConversationInterface ai;
    private Clip currentClip;

    public AIClientView() {
        setupUI();
        connectToRMI();
        attachListeners();
    }

    /**
     * ------------------ UI Setup ------------------
     */
    private void setupUI() {
        setTitle("AI Voice Chat - Emotional AI");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel title = new JLabel("Emotional AI Chat", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(0, 100, 200));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Chat Panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(240, 242, 245));
        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Controls
        add(createControlPanel(), BorderLayout.SOUTH);

        addMessage("AI", "Xin chào! Hãy ghi âm và gửi cho tôi để bắt đầu trò chuyện!", false);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        lblStatus = new JLabel("Trạng thái: Sẵn sàng");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(Color.GRAY);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(Color.WHITE);

        btnRecord = createButton("🎤 Ghi âm", new Color(76, 175, 80));
        btnStop = createButton("⏹ Dừng", new Color(244, 67, 54));
        btnPlay = createButton("▶ Nghe", new Color(33, 150, 243));
        btnSendAI = createButton("🚀 Gửi AI", new Color(156, 39, 176));

        Dimension btnSize = new Dimension(100, 35);
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * ------------------ RMI ------------------
     */
    private void connectToRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ai = (ConversationInterface) registry.lookup("AIChat");
            addMessage("AI", "Kết nối RMI thành công!", false);
        } catch (Exception e) {
            e.printStackTrace();
            addMessage("AI", "Không thể kết nối server AI: " + e.getMessage(), false);
        }
    }

    /**
     * ------------------ Event Listeners ------------------
     */
    private void attachListeners() {
        btnRecord.addActionListener(e -> startRecording());
        btnStop.addActionListener(e -> stopRecording());
        btnPlay.addActionListener(e -> new Thread(() -> playAudio(userAudioPath)).start());
        btnSendAI.addActionListener(e -> new Thread(this::sendAudioToAI).start());
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
        try {
            lblStatus.setText("Đang gửi audio...");
            btnSendAI.setEnabled(false);
            btnRecord.setEnabled(false);

            addMessage("Bạn", "Đang gửi audio tới AI...", true);

            // Chuyển file thành byte[]
            byte[] audioBytes = java.nio.file.Files.readAllBytes(new File(userAudioPath).toPath());
            String responseAudio = ai.talkWithUser(audioBytes);

            addMessage("AI", "Đã nhận audio. AI đang xử lý...", false);
            lblStatus.setText("AI đã phản hồi");
            System.out.println("response ai" + responseAudio);
            // Phát audio phản hồi
            playAudio(responseAudio);

        } catch (Exception e) {
            e.printStackTrace();
            addMessage("AI", "Lỗi khi gọi AI: " + e.getMessage(), false);
            lblStatus.setText("Lỗi khi gọi AI");
        } finally {
            btnSendAI.setEnabled(true);
            btnRecord.setEnabled(true);
        }
    }

    /**
     * ------------------ Audio Playback ------------------
     */
    private void playAudio(String filePath) {
        try {
            if (currentClip != null && currentClip.isRunning()) {
                currentClip.stop();
                currentClip.close();
            }

            File audioFile = new File(filePath);
            if (!audioFile.exists() || audioFile.length() == 0) {
                addMessage("System", "File âm thanh không tồn tại hoặc trống", false);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                addMessage("System", "Định dạng audio không được hỗ trợ", false);
                audioStream.close();
                return;
            }

            currentClip = (Clip) AudioSystem.getLine(info);
            currentClip.open(audioStream);
            currentClip.start();

            currentClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    SwingUtilities.invokeLater(() -> addMessage("System", "Phát xong", false));
                    currentClip.close();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            addMessage("System", "Lỗi phát audio: " + e.getMessage(), false);
        }
    }

    /**
     * ------------------ Chat Message ------------------
     */
    private void addMessage(String sender, String message, boolean isUser) {
        SwingUtilities.invokeLater(() -> {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(chatPanel.getBackground());
            wrapper.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JPanel msgPanel = new JPanel(new BorderLayout());
            msgPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            msgPanel.setBackground(isUser ? new Color(220, 248, 198) : Color.WHITE);

            JLabel senderLabel = new JLabel(sender);
            senderLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            senderLabel.setForeground(isUser ? new Color(0, 100, 0) : new Color(100, 100, 200));

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
            msgPanel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));

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
        if (currentClip != null) currentClip.close();
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new AIClientView().setVisible(true);
        });
    }
}
