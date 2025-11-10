package com.emotionalai.component;

import com.emotionalai.model.User;
import com.emotionalai.utils.VoiceRecorder;
import javazoom.jl.player.Player;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;

public class ControlPanel extends JPanel {

    private JButton btnRecord, btnStop, btnPlay, btnSendAI;
    private JLabel lblStatus;
    private String userAudioPath = "input.wav";
    private Player currentPlayer;
    private User currentUser;
    private Runnable sendAction; // callback khi nhấn gửi

    public ControlPanel(User user, Runnable sendAction) {
        this.currentUser = user;
        this.sendAction = sendAction;
        setupUI();
        attachListeners();
        updateUIForLogin();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        setBackground(Color.WHITE);

        lblStatus = new JLabel("Trạng thái: Sẵn sàng");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN,13));
        lblStatus.setForeground(Color.DARK_GRAY);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,15,5));
        buttonPanel.setBackground(Color.WHITE);

        btnRecord = createButton("🎤 Ghi âm", new Color(76,175,80));
        btnStop = createButton("⏹ Dừng", new Color(244,67,54));
        btnPlay = createButton("▶ Nghe", new Color(33,150,243));
        btnSendAI = createButton("🚀 Gửi AI", new Color(156,39,176));

        Dimension btnSize = new Dimension(120,40);
        btnRecord.setPreferredSize(btnSize);
        btnStop.setPreferredSize(btnSize);
        btnPlay.setPreferredSize(btnSize);
        btnSendAI.setPreferredSize(btnSize);

        buttonPanel.add(btnRecord);
        buttonPanel.add(btnStop);
        buttonPanel.add(btnPlay);
        buttonPanel.add(btnSendAI);

        btnStop.setEnabled(false);
        btnPlay.setEnabled(false);
        btnSendAI.setEnabled(false);

        add(lblStatus, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD,14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(color.darker(),1,true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void attachListeners() {
        btnRecord.addActionListener(e -> startRecording());
        btnStop.addActionListener(e -> stopRecording());
        btnPlay.addActionListener(e -> new Thread(this::playAudio).start());
        btnSendAI.addActionListener(e -> {
            if(sendAction != null) sendAction.run();
        });
    }

    private void updateUIForLogin() {
        boolean loggedIn = currentUser != null;
        btnRecord.setEnabled(loggedIn);
        btnSendAI.setEnabled(loggedIn);
        if(!loggedIn) lblStatus.setText("Vui lòng đăng nhập để sử dụng AI Chat");
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
    }

    private void playAudio() {
        try {
            if(currentPlayer != null) currentPlayer.close();
            FileInputStream fis = new FileInputStream(userAudioPath);
            currentPlayer = new Player(fis);
            currentPlayer.play();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getAudioPath() {
        return userAudioPath;
    }
}
