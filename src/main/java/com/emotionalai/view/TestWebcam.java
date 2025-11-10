package com.emotionalai.view;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class TestWebcam extends JPanel implements Runnable {

    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private VideoCapture capture;
    private BufferedImage image;
    private boolean running = true;

    public TestWebcam() {
        capture = new VideoCapture(0); // 0 = default camera
        new Thread(this).start();
    }

    @Override
    public void run() {
        Mat frame = new Mat();
        while (running) {
            if (capture.read(frame)) {
                Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2RGB);
                image = matToBufferedImage(frame);
                repaint();
            }
        }
        capture.release();
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        int width = mat.width(), height = mat.height(), channels = mat.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        mat.get(0, 0, sourcePixels);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
        return image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Webcam Test");
        TestWebcam webcamPanel = new TestWebcam();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.add(webcamPanel);
        frame.setVisible(true);
    }
}
