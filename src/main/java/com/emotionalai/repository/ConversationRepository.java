package com.emotionalai.repository;

import com.emotionalai.config.DatabaseConnection;
import com.emotionalai.model.Conversation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationRepository {
    public void save(Conversation conversation) {
        String sql = "INSERT INTO conversations(user_text, ai_text, emotion) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, conversation.getUserText());
            stmt.setString(2, conversation.getAiText());
            stmt.setString(3, conversation.getEmotion());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) conversation.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Conversation> findAll() {
        List<Conversation> list = new ArrayList<>();
        String sql = "SELECT * FROM conversations ORDER BY timestamp DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Conversation c = new Conversation(
                        rs.getString("user_text"),
                        rs.getString("ai_text"),
                        rs.getString("emotion")
                );
                list.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
