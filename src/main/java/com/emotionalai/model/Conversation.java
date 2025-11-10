package com.emotionalai.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Conversation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private String userText;
    private String aiText;
    private String emotion;
    private LocalDateTime timestamp;

    private String userAudioPath;
    private String aiAudioPath;

    public Conversation() {
    }

    public Conversation(int userId, String userText, String aiText, String emotion,
                        String userAudioPath, String aiAudioPath) {
        this.userId = userId;
        this.userText = userText;
        this.aiText = aiText;
        this.emotion = emotion;
        this.userAudioPath = userAudioPath;
        this.aiAudioPath = aiAudioPath;
        this.timestamp = LocalDateTime.now();
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getUserAudioPath() {
        return userAudioPath;
    }

    public void setUserAudioPath(String userAudioPath) {
        this.userAudioPath = userAudioPath;
    }

    public String getAiAudioPath() {
        return aiAudioPath;
    }

    public void setAiAudioPath(String aiAudioPath) {
        this.aiAudioPath = aiAudioPath;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id=" + id +
                ", userId=" + userId +
                ", userText='" + userText + '\'' +
                ", aiText='" + aiText + '\'' +
                ", emotion='" + emotion + '\'' +
                ", timestamp=" + timestamp +
                ", userAudioPath='" + userAudioPath + '\'' +
                ", aiAudioPath='" + aiAudioPath + '\'' +
                '}';
    }
}
