package com.emotionalai.config;

import com.emotionalai.rmi.ConversationInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIConnection {
    private static RMIConnection instance;
    private ConversationInterface ai;

    private RMIConnection() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ai = (ConversationInterface) registry.lookup("AIChat");
            System.out.println("Kết nối RMI thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Không thể kết nối server AI: " + e.getMessage());
        }
    }

    public static synchronized RMIConnection getInstance() {
        if (instance == null) {
            instance = new RMIConnection();
        }
        return instance;
    }

    public ConversationInterface getAI() {
        return ai;
    }
}
