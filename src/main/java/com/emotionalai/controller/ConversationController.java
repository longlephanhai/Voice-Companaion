package com.emotionalai.controller;

import com.emotionalai.model.Conversation;
import com.emotionalai.service.ConversationService;

import java.io.File;

public class ConversationController {
    private ConversationService service = new ConversationService();

    public Conversation processUserAudio(File audioFile, String userText, String aiResponse) {
        return service.handleConversation(audioFile, userText, aiResponse);
    }
}
