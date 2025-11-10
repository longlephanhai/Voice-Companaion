package com.emotionalai.service;

import com.emotionalai.model.Conversation;
import com.emotionalai.repository.ConversationRepository;
import com.emotionalai.utils.GeminiAPI;
import com.emotionalai.utils.TextToSpeech;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class ConversationService {


    private final ConversationRepository conversationRepository;

    public ConversationService() {
        this.conversationRepository = new ConversationRepository();
    }

    public String talkWithUser(byte[] audioData, int userId) {
        try {
            // Tạo tên file tạm duy nhất cho mỗi user request
            String uniqueId = UUID.randomUUID().toString();
            String uploadDir = "uploads/user/";
            Files.createDirectories(Paths.get(uploadDir));

            String userAudioPath  = uploadDir + "user_" + uniqueId + ".wav";
            Files.write(Paths.get(userAudioPath ), audioData);

            //  Gửi file sang server Python để detect emotion
            String apiUrl = "http://localhost:8000/detect_emotion";
            String boundary = "*****" + System.currentTimeMillis() + "*****";

            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (OutputStream outputStream = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
                 FileInputStream fis = new FileInputStream(userAudioPath )) {

                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"audio\"; filename=\"" + userAudioPath  + "\"\r\n");
                writer.append("Content-Type: audio/wav\r\n\r\n");
                writer.flush();

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();

                writer.append("\r\n--").append(boundary).append("--\r\n");
                writer.flush();
            }

            // Nhận phản hồi JSON từ Python
            int responseCode = connection.getResponseCode();
            InputStream responseStream = responseCode == 200 ? connection.getInputStream() : connection.getErrorStream();

            StringBuilder responseStr = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(responseStream))) {
                String line;
                while ((line = in.readLine()) != null) {
                    responseStr.append(line);
                }
            }

            JsonObject json = JsonParser.parseString(responseStr.toString()).getAsJsonObject();
            String text = json.has("text") ? json.get("text").getAsString() : "";
            String emotion = json.has("emotion") ? json.get("emotion").getAsString() : "neutral";
            double confidence = json.has("confidence") ? json.get("confidence").getAsDouble() : 0.0;

            String pythonResponse = "Text: " + text + " Emotion: " + emotion + ", Confidence: " + confidence;
            System.out.println("Emotion detected: " + pythonResponse);

            // Gửi sang Gemini API để lấy phản hồi
            String geminiReply = GeminiAPI.chat(pythonResponse);

            // Tạo âm thanh phản hồi
//            String aiAudioFilePath = TextToSpeech.convert(geminiReply, emotion);
            String aiDir = "uploads/ai/";
            Files.createDirectories(Paths.get(aiDir));
            String aiAudioPath = aiDir + "ai_" + System.currentTimeMillis() + ".mp3";
            TextToSpeech.convert(geminiReply, emotion, aiAudioPath);
//            System.out.println("TTS tạo file âm thanh tại: " + aiAudioFilePath);
            Conversation conversation = new Conversation();
            conversation.setUserId(userId);
            conversation.setUserText(text);
            conversation.setAiText(geminiReply);
            conversation.setEmotion(emotion);
            conversation.setUserAudioPath(userAudioPath);
            conversation.setAiAudioPath(aiAudioPath);
            conversation.setTimestamp(java.time.LocalDateTime.now());
            conversationRepository.saveConversation(conversation);

            return aiAudioPath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
