package com.emotionalai.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);

            ConversationImpl aiServer = new ConversationImpl();

            registry.rebind("AIChat", aiServer);

            System.out.println("RMI server chạy, object AIChat đã bind vào registry.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
