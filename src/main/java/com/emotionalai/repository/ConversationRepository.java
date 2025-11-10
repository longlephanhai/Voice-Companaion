package com.emotionalai.repository;

import com.emotionalai.config.DatabaseConnection;
import com.emotionalai.model.Conversation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationRepository {

    // Lưu conversation mới
    public boolean saveConversation(Conversation conversation) {
        String sql = """
                INSERT INTO conversations\s
                (user_id, user_text, ai_text, emotion, user_audio_path, ai_audio_path, timestamp)
                VALUES (?, ?, ?, ?, ?, ?, ?)
               \s""";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, conversation.getUserId());
            stmt.setString(2, conversation.getUserText());
            stmt.setString(3, conversation.getAiText());
            stmt.setString(4, conversation.getEmotion());
            stmt.setString(5, conversation.getUserAudioPath());
            stmt.setString(6, conversation.getAiAudioPath());
            stmt.setTimestamp(7, Timestamp.valueOf(conversation.getTimestamp()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) return false;

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    conversation.setId(generatedKeys.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            System.err.println("Lỗi khi lưu conversation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tất cả conversation của một user
    public List<Conversation> getConversationsByUserId(int userId) {
        List<Conversation> conversations = new ArrayList<>();
        String sql = """
                SELECT * FROM conversations 
                WHERE user_id = ? 
                ORDER BY timestamp DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Conversation c = new Conversation();
                    c.setId(rs.getInt("id"));
                    c.setUserId(userId);
                    c.setUserText(rs.getString("user_text"));
                    c.setAiText(rs.getString("ai_text"));
                    c.setEmotion(rs.getString("emotion"));
                    c.setUserAudioPath(rs.getString("user_audio_path"));
                    c.setAiAudioPath(rs.getString("ai_audio_path"));
                    c.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    conversations.add(c);
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách hội thoại: " + e.getMessage());
            e.printStackTrace();
        }

        return conversations;
    }
}
