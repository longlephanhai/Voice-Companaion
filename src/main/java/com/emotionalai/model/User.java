package com.emotionalai.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String email;
    private String password;
    private final List<Conversation> conversations;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.conversations = new ArrayList<>();
    }

    public void addConversation(Conversation conversation) {
        this.conversations.add(conversation);
    }

    // getters & setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }
}
