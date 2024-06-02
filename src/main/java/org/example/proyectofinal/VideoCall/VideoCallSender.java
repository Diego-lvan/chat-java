package org.example.proyectofinal.VideoCall;


import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.proyectofinal.Constants.DataConstants;
import org.example.proyectofinal.Constants.DataConstants;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class VideoCallSender extends Thread {
    private final String receiverIp;
    private final ImageView imageView;
    private boolean running = true;
    private DatagramSocket datagramSocket;

    public VideoCallSender(String receiverIp, ImageView imageView) {
        this.receiverIp = receiverIp;
        this.imageView = imageView;
    }

    public void run() {
        VideoCapture videoCapture = new VideoCapture(0);
        try {
            datagramSocket = new DatagramSocket();
            Mat frame = new Mat();
            while (true) {
                videoCapture.read(frame);
                if (frame.empty()) {
                    break;
                }

                // Convertir el marco a matriz de bytes
                MatOfByte matOfByte = new MatOfByte();
                Imgcodecs.imencode(".jpg", frame, matOfByte, new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, 30));
                // Reducir la calidad de la imagen de salida
                byte[] frameData = matOfByte.toArray();

                // Calcular la cantidad de paquetes necesarios
                int numPackets = (int) Math.ceil((double) frameData.length / DataConstants.MAX_PACKET_SIZE);
                // Send the number of packets first and the number of package 0
                byte[] numPacketsBytes = ByteBuffer.allocate(4).putInt(numPackets).array();
                byte[] numPacketZero = ByteBuffer.allocate(4).putInt(0).array();
                byte[] frameLengthBytes= new byte[8];
                System.arraycopy(numPacketZero, 0, frameLengthBytes, 0, numPacketZero.length);
                System.arraycopy(numPacketsBytes, 0, frameLengthBytes, numPacketZero.length, numPacketsBytes.length);
                DatagramPacket lengthPacket = new DatagramPacket(frameLengthBytes, frameLengthBytes.length, InetAddress.getByName(receiverIp), DataConstants.VIDEO_CALL_PORT);
                datagramSocket.send(lengthPacket);

                // Send the frame data
                int offset = 0;
                for (int i = 0; i < numPackets; i++) {
                    int packetSize = Math.min(DataConstants.MAX_PACKET_SIZE, frameData.length - offset);
                    byte[] packetData = new byte[packetSize + 4];
                    byte[] packetNum = ByteBuffer.allocate(4).putInt(i + 1).array();
                    System.arraycopy(packetNum, 0, packetData, 0, packetNum.length);
                    System.arraycopy(frameData, offset, packetData, packetNum.length, packetSize);
                    DatagramPacket framePacket = new DatagramPacket(packetData, packetData.length, InetAddress.getByName(receiverIp), DataConstants.VIDEO_CALL_PORT);
                    datagramSocket.send(framePacket);
                    offset += packetSize;
                }


                // Crear y mostrar la imagen (opcional)
                Image image = new Image(new ByteArrayInputStream(frameData));
                Platform.runLater(() -> imageView.setImage(image));
            }
            videoCapture.release();
        } catch (Exception e) {
        } finally {
            videoCapture.release();
        }
    }

    public void interrupt() {
        if (datagramSocket != null) {
            datagramSocket.close();
        }
        super.interrupt();
    }
}
