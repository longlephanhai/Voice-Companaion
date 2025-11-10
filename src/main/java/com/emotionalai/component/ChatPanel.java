package com.emotionalai.component;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatPanel extends JPanel {

    private JPanel chatContainer;
    private JScrollPane scrollPane;

    public ChatPanel() {
        setLayout(new BorderLayout());
        chatContainer = new JPanel();
        chatContainer.setLayout(new BoxLayout(chatContainer, BoxLayout.Y_AXIS));
        chatContainer.setBackground(new Color(245,247,250));
        scrollPane = new JScrollPane(chatContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addMessage(String sender, String message, boolean isUser) {
        SwingUtilities.invokeLater(() -> {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(chatContainer.getBackground());
            wrapper.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

            JPanel msgPanel = new JPanel(new BorderLayout());
            msgPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200,200,200),1,true),
                    BorderFactory.createEmptyBorder(10,15,10,15)
            ));
            msgPanel.setBackground(isUser ? new Color(220,248,198) : Color.WHITE);

            JLabel senderLabel = new JLabel(sender);
            senderLabel.setFont(new Font("Segoe UI", Font.BOLD,12));
            senderLabel.setForeground(isUser ? new Color(0,100,0) : new Color(0,0,150));

            JLabel timeLabel = new JLabel(new SimpleDateFormat("HH:mm").format(new Date()));
            timeLabel.setFont(new Font("Segoe UI", Font.PLAIN,10));
            timeLabel.setForeground(Color.GRAY);

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.add(senderLabel, BorderLayout.WEST);
            header.add(timeLabel, BorderLayout.EAST);

            JTextArea msgArea = new JTextArea(message);
            msgArea.setEditable(false);
            msgArea.setLineWrap(true);
            msgArea.setWrapStyleWord(true);
            msgArea.setBackground(msgPanel.getBackground());
            msgArea.setFont(new Font("Segoe UI", Font.PLAIN,13));
            msgArea.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));

            msgPanel.add(header, BorderLayout.NORTH);
            msgPanel.add(msgArea, BorderLayout.CENTER);
            msgPanel.setMaximumSize(new Dimension(450,Integer.MAX_VALUE));

            wrapper.add(msgPanel, isUser ? BorderLayout.EAST : BorderLayout.WEST);
            chatContainer.add(wrapper);
            chatContainer.revalidate();
            chatContainer.repaint();

            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
