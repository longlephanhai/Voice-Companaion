package com.emotionalai.model;

import java.time.LocalDateTime;

public class Conversation {
    private int id;
    private User user; // người dùng tạo conversation
    private String userText;
    private String aiText;
    private String emotion;
    private LocalDateTime timestamp;

    public Conversation(User user, String userText, String aiText, String emotion) {
        this.user = user;
        this.userText = userText;
        this.aiText = aiText;
        this.emotion = emotion;
        this.timestamp = LocalDateTime.now();
    }

    // getters & setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }

    public String getAiText() {
        return aiText;
    }

    public void setAiText(String aiText) {
        this.aiText = aiText;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
