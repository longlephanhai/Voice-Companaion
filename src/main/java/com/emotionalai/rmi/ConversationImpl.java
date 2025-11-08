package com.emotionalai.rmi;

import com.emotionalai.model.User;
import com.emotionalai.service.AuthService;
import com.emotionalai.service.ConversationService;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ConversationImpl extends UnicastRemoteObject implements ConversationInterface {


    private final ConversationService conversationService;
    private final AuthService authService;

    protected ConversationImpl() throws RemoteException {
        super();
        this.conversationService = new ConversationService();
        this.authService = new AuthService();
    }

    @Override
    public String talkWithUser(byte[] audioData) throws RemoteException {
        return conversationService.talkWithUser(audioData);
    }


    @Override
    public boolean register(User user) throws RemoteException {
        try {
            return authService.register(user);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User login(String email, String password) throws RemoteException {
        try {
            return authService.login(email, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
