package com.emotionalai.rmi;

import com.emotionalai.model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ConversationInterface extends Remote {
    String talkWithUser(byte[] audioData) throws RemoteException;
    boolean register(User user) throws RemoteException;
    User login(String email, String password) throws RemoteException;
}
