package com.emotionalai.service;

import com.emotionalai.utils.GeminiAPI;
import com.emotionalai.utils.TextToSpeech;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConversationService {

    public String talkWithUser(byte[] audioData) {
        try {
            // 1. Lưu file tạm
            String tempFile = "server_received.wav";
            Files.write(Paths.get(tempFile), audioData);

            // 2. Gửi file sang server Python để detect emotion
            String apiUrl = "http://localhost:8000/detect_emotion";
            String boundary = "*****" + System.currentTimeMillis() + "*****";

            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream outputStream = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"audio\"; filename=\"server_received.wav\"\r\n");
            writer.append("Content-Type: audio/wav\r\n\r\n");
            writer.flush();

            FileInputStream fis = new FileInputStream(tempFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            fis.close();

            writer.append("\r\n");
            writer.append("--").append(boundary).append("--\r\n");
            writer.flush();
            writer.close();

            int responseCode = connection.getResponseCode();
            InputStream responseStream = responseCode == 200 ? connection.getInputStream() : connection.getErrorStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(responseStream));
            StringBuilder responseStr = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                responseStr.append(line);
            }
            in.close();

            // 3. Parse JSON từ server Python
            JsonObject json = JsonParser.parseString(responseStr.toString()).getAsJsonObject();
            String text = json.has("text") ? json.get("text").getAsString() : "";
            String emotion = json.has("emotion") ? json.get("emotion").getAsString() : "neutral";
            double confidence = json.has("confidence") ? json.get("confidence").getAsDouble() : 0.0;

            String pythonResponse = "Text: " + text + " Emotion: " + emotion + ", Confidence: " + confidence;

            // 4. Gửi tiếp sang Gemini API để lấy phản hồi
            String geminiReply = GeminiAPI.chat(pythonResponse);

            // 5. Chuyển phản hồi thành âm thanh
            String audioFilePath = TextToSpeech.convert(geminiReply, emotion);
            System.out.println("TTS đã tạo file âm thanh tại: " + audioFilePath);
            return audioFilePath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
