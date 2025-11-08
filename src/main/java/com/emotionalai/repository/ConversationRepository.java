package com.emotionalai.repository;

import com.emotionalai.config.DatabaseConnection;
import com.emotionalai.model.Conversation;
import com.emotionalai.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationRepository {

    // Lưu conversation mới
    public boolean saveConversation(Conversation conversation) {
        String sql = "INSERT INTO conversations (user_id, user_text, ai_text, emotion, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, conversation.getUser().getId());
            stmt.setString(2, conversation.getUserText());
            stmt.setString(3, conversation.getAiText());
            stmt.setString(4, conversation.getEmotion());
            stmt.setTimestamp(5, Timestamp.valueOf(conversation.getTimestamp()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) return false;

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    conversation.setId(generatedKeys.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tất cả conversation của 1 user
    public List<Conversation> getConversationsByUser(User user) {
        List<Conversation> conversations = new ArrayList<>();
        String sql = "SELECT * FROM conversations WHERE user_id = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Conversation c = new Conversation(
                            user,
                            rs.getString("user_text"),
                            rs.getString("ai_text"),
                            rs.getString("emotion")
                    );
                    c.setId(rs.getInt("id"));
                    c.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    conversations.add(c);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conversations;
    }
}
