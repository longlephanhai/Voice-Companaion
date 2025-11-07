package com.emotionalai.utils;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class AIEmotionClient {
    public static String detectEmotion(File audioFile) throws Exception {
        String urlString = "http://localhost:8000/detect_emotion";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "audio/wav");

        try (OutputStream os = conn.getOutputStream()) {
            Files.copy(audioFile.toPath(), os);
        }

        String response = new String(conn.getInputStream().readAllBytes());
        conn.disconnect();

        // Response: {"label":"happy"} → trả về nguyên JSON
        return response;
    }
}
