package com.emotionalai.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class TextToSpeech {

    public static String convert(String text, String emotion, String aiAudioPath) throws Exception {
        String credPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        System.out.println("GOOGLE_APPLICATION_CREDENTIALS = " + credPath);

        if (credPath == null || !new File(credPath).exists()) {
            throw new RuntimeException("Credential file not found or not set properly!");
        }

        text = cleanText(text);

        //  Khởi tạo client với credentials
        GoogleCredentials credentials = GoogleCredentials.fromStream(Files.newInputStream(Paths.get(credPath)));
        TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        try (TextToSpeechClient ttsClient = TextToSpeechClient.create(settings)) {

            // Chuẩn bị văn bản
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            double pitch = 0.0;
            double speakingRate = 1.0;
            String voiceName = "vi-VN-Neural2-A";

            if (emotion != null) {
                switch (emotion.toLowerCase()) {
                    case "sad":
                        pitch = -2.5;
                        speakingRate = 0.9;
                        break;

                    case "happy":
                        pitch = 2.5;
                        speakingRate = 1.1;
                        break;

                    case "angry":
                    case "frustration":
                        pitch = -1.0;
                        speakingRate = 0.95;
                        break;

                    case "fear":
                    case "disgust":
                        pitch = -1.5;
                        speakingRate = 0.9;
                        break;

                    default:
                        pitch = 0.0;
                        speakingRate = 1.0;
                        break;
                }
            }


            SsmlVoiceGender gender = voiceName.endsWith("A")
                    ? SsmlVoiceGender.FEMALE
                    : SsmlVoiceGender.MALE;

            System.out.println("Voice: " + voiceName + " | Gender: " + gender +
                    " | Emotion: " + emotion +
                    " | Pitch: " + pitch +
                    " | Rate: " + speakingRate);

            // Cấu hình giọng
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("vi-VN")
                    .setName(voiceName)
                    .setSsmlGender(gender)
                    .build();

            // Cấu hình âm thanh
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .setPitch((float) pitch)
                    .setSpeakingRate((float) speakingRate)
                    .build();

            // Gọi API
            SynthesizeSpeechResponse response = ttsClient.synthesizeSpeech(input, voice, audioConfig);
            ByteString audioContents = response.getAudioContent();
            String uniqueId = UUID.randomUUID().toString();
            // Xuất file    String tempFile = uploadDir + "user_" + uniqueId + ".wav";
//            Files.createDirectories(Paths.get("uploads/ai/"));
//            String outputFile = "uploads/ai/ai_" + uniqueId + ".mp3";
            try (OutputStream out = new FileOutputStream(aiAudioPath)) {
                out.write(audioContents.toByteArray());
            }

            System.out.println("Đã tạo file giọng nói: " + aiAudioPath);
            return aiAudioPath;
        }
    }

    //  Làm sạch text để tránh lỗi phát âm / SSML
    private static String cleanText(String text) {
        if (text == null) return "";

        return text
                .replaceAll("\\*", "")
                .replaceAll("_", "")
                .replaceAll("#", "")
                .replaceAll("<", "(")
                .replaceAll(">", ")")
                .replaceAll("&", "và")
                .replaceAll("```", "")
                .replaceAll("\"", "")
                .replaceAll("’", "'")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
