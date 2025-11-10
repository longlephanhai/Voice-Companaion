package com.emotionalai.component;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class WebcamPanel extends JPanel implements Runnable {

    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private VideoCapture capture;
    private BufferedImage image;
    private boolean running = true;

    public WebcamPanel() {
        capture = new VideoCapture(0);
        if(!capture.isOpened()){
            System.err.println("Không mở được camera!");
            return;
        }
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

    public BufferedImage getCurrentImage() {
        return image;
    }

    public void stop() {
        running = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }
    }
}
