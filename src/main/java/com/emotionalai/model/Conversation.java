package com.emotionalai.model;

import java.time.LocalDateTime;

public class Conversation {
    private int id;
    private String userText;
    private String aiText;
    private String emotion;
    private LocalDateTime timestamp;

    public Conversation(String userText, String aiText, String emotion) {
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

    public String getUserText() {
        return userText;
    }

    public String getAiText() {
        return aiText;
    }

    public String getEmotion() {
        return emotion;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
