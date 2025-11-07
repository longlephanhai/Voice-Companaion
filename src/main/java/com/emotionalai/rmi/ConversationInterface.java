package com.emotionalai.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ConversationInterface extends Remote {
    String talkWithUser(byte[] audioData) throws RemoteException;
}
