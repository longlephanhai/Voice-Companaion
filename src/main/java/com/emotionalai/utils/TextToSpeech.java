package com.emotionalai.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TextToSpeech {

    public static String convert(String text, String emotion) throws Exception {
        String credPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        System.out.println("GOOGLE_APPLICATION_CREDENTIALS = " + credPath);

        // ⚙️ Kiểm tra credentials
        if (credPath == null || !new File(credPath).exists()) {
            throw new RuntimeException("❌ Credential file not found or not set properly!");
        }

        // 🧹 Làm sạch text trước khi đọc
        text = cleanText(text);

        // ✅ Khởi tạo client với credentials
        GoogleCredentials credentials = GoogleCredentials.fromStream(Files.newInputStream(Paths.get(credPath)));
        TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        try (TextToSpeechClient ttsClient = TextToSpeechClient.create(settings)) {

            // 1️⃣ Chuẩn bị văn bản
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            // 2️⃣ Điều chỉnh cảm xúc
            double pitch = 0.0;
            double speakingRate = 1.0;
            String voiceName = "vi-VN-Neural2-A"; // giọng nữ mặc định

            if (emotion != null) {
                switch (emotion.toLowerCase()) {
                    case "sad":
                        pitch = -3.0;
                        speakingRate = 0.9;
                        break;
                    case "happy":
                        pitch = 3.0;
                        speakingRate = 1.1;
                        break;
                    case "angry":
                    case "ang":
                        pitch = 2.0;
                        speakingRate = 1.2;
                        voiceName = "vi-VN-Neural2-D"; // giọng nam
                        break;
                    default:
                        break;
                }
            }

            // 🧠 Giới tính tự động
            SsmlVoiceGender gender = voiceName.endsWith("A")
                    ? SsmlVoiceGender.FEMALE
                    : SsmlVoiceGender.MALE;

            System.out.println("Voice: " + voiceName + " | Gender: " + gender +
                    " | Emotion: " + emotion +
                    " | Pitch: " + pitch +
                    " | Rate: " + speakingRate);

            // 3️⃣ Cấu hình giọng
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("vi-VN")
                    .setName(voiceName)
                    .setSsmlGender(gender)
                    .build();

            // 4️⃣ Cấu hình âm thanh
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .setPitch((float) pitch)
                    .setSpeakingRate((float) speakingRate)
                    .build();

            // 5️⃣ Gọi API
            SynthesizeSpeechResponse response = ttsClient.synthesizeSpeech(input, voice, audioConfig);
            ByteString audioContents = response.getAudioContent();

            // 6️⃣ Xuất file
            Files.createDirectories(Paths.get("audio_output"));
            String outputFile = "audio_output/output_" + System.currentTimeMillis() + ".mp3";
            try (OutputStream out = new FileOutputStream(outputFile)) {
                out.write(audioContents.toByteArray());
            }

            System.out.println("✅ Đã tạo file giọng nói: " + outputFile);
            return outputFile;
        }
    }

    // 🧽 Làm sạch text để tránh lỗi phát âm / SSML
    private static String cleanText(String text) {
        if (text == null) return "";

        return text
                .replaceAll("\\*", "")      // bỏ dấu **
                .replaceAll("_", "")        // bỏ dấu _
                .replaceAll("#", "")
                .replaceAll("<", "(")
                .replaceAll(">", ")")
                .replaceAll("&", "và")
                .replaceAll("```", "")
                .replaceAll("\"", "")
                .replaceAll("’", "'")
                .replaceAll("\\s+", " ")    // gộp khoảng trắng
                .trim();
    }
}
