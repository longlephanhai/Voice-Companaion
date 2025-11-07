package com.emotionalai.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class VoiceRecorder {

    private static TargetDataLine line;
    private static volatile boolean recording = false;

    public static void startRecording(String filename) {
        AudioFormat format = new AudioFormat(16000, 8, 2, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Not Supported!");
            return;
        }

        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open();
            line.start();
            recording = true;

            Thread stopper = new Thread(() -> {
                AudioInputStream audioInputStream = new AudioInputStream(line);
                File wavFile = new File(filename);
                try {
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            stopper.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopRecording() {
        recording = false;
        if (line != null) {
            line.stop();
            line.close();
            line = null;
            System.out.println("Đã dừng ghi âm");
        }
    }

    public static boolean isRecording() {
        return recording;
    }
}
