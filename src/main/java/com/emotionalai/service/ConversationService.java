//package com.emotionalai.service;
//
//import com.emotionalai.model.Conversation;
//import com.emotionalai.repository.ConversationRepository;
//import com.emotionalai.utils.AIEmotionClient;
//import com.emotionalai.utils.TextToSpeech;
//
//import java.io.File;
//
//public class ConversationService {
//    private ConversationRepository repository = new ConversationRepository();
//
//    public Conversation handleConversation(File audioFile, String userText, String aiResponse) {
//        try {
//            String emotionJson = AIEmotionClient.detectEmotion(audioFile);
//            String emotionLabel = emotionJson.replaceAll(".*\"label\":\"(\\w+)\".*", "$1");
//
//            TextToSpeech.convert(aiResponse, emotionLabel);
//
//            Conversation conv = new Conversation(userText, aiResponse, emotionLabel);
//            repository.save(conv);
//            return conv;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
