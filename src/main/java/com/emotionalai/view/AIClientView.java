package com.emotionalai.view;

import com.emotionalai.component.ChatPanel;
import com.emotionalai.component.ControlPanel;
import com.emotionalai.component.WebcamPanel;
import com.emotionalai.model.User;
import com.emotionalai.config.RMIConnection;
import com.emotionalai.rmi.ConversationInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

public class AIClientView extends JFrame {

    private WebcamPanel webcamPanel;
    private ChatPanel chatPanel;
    private ControlPanel controlPanel;
    private User currentUser;
    private ConversationInterface ai;

    public AIClientView(User user) {
        this.currentUser = user;
        ai = RMIConnection.getInstance().getAI();

        setTitle("🎧 Emotional AI Chat");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        webcamPanel = new WebcamPanel();
        chatPanel = new ChatPanel();
        controlPanel = new ControlPanel(currentUser, this::sendAudioAndFrame);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, webcamPanel, chatPanel);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0);
        add(splitPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        chatPanel.addMessage("AI", "Xin chào! Hãy ghi âm và gửi cho tôi để bắt đầu trò chuyện!", false);
    }

    private void sendAudioAndFrame() {
        new Thread(() -> {
            try {
                byte[] audioBytes = java.nio.file.Files.readAllBytes(new java.io.File(controlPanel.getAudioPath()).toPath());
                BufferedImage img = webcamPanel.getCurrentImage();
                byte[] frameBytes = null;

                if (img != null) {
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        ImageIO.write(img, "jpg", baos);
                        frameBytes = baos.toByteArray();
                    }
                }

                chatPanel.addMessage("Bạn", "Đang gửi audio và ảnh tới AI...", true);

                // Gọi RMI (bạn cần sửa talkWithUser để nhận frameBytes nếu muốn)
                String responseAudioPath = ai.talkWithUser(audioBytes, currentUser.getId());

                chatPanel.addMessage("AI", "AI đang xử lý...", false);
                playAudio(responseAudioPath);

            } catch (Exception e) {
                e.printStackTrace();
                chatPanel.addMessage("System", "Lỗi khi gửi dữ liệu AI: " + e.getMessage(), false);
            }
        }).start();
    }

    private void playAudio(String path) {
        try {
            javazoom.jl.player.Player player = new javazoom.jl.player.Player(new java.io.FileInputStream(path));
            player.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        webcamPanel.stop();
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new AIClientView(null).setVisible(true);
        });
    }
}
