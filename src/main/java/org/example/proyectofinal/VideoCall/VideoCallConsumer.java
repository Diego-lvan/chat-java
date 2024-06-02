package org.example.proyectofinal.VideoCall;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.proyectofinal.Constants.DataConstants;

import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class VideoCallConsumer extends Thread {
    private final ImageView imageView;
    private DatagramSocket datagramSocket;
    private Map<Integer, byte[]> currentFrame;
    private int numberOfPackets = -1;
    private int currentFrameLength = 0;
    private String senderIp;
    public boolean running = true;

    public VideoCallConsumer(ImageView imageView, String senderIp) {
        this.imageView = imageView;
        currentFrame = new HashMap<>();
        this.senderIp = senderIp;
    }

    public void run() {
        try {
            datagramSocket = new DatagramSocket(DataConstants.VIDEO_CALL_PORT);
            while (true) {
                byte[] frame = receiveFrame();
                if (frame != null) {
                    System.out.println("Frame received");
                    displayFrame(frame);
                }
            }
        } catch (Exception e) {

        }
    }

    private byte[] receiveFrame() throws Exception {
        byte[] newPacket = new byte[DataConstants.MAX_PACKET_SIZE + 4];
        DatagramPacket datagramPacket = new DatagramPacket(newPacket, newPacket.length);
        datagramSocket.receive(datagramPacket);
        if (senderIp != null && !datagramPacket.getAddress().getHostAddress().equals(senderIp)) {
            return null;
        }
        byte[] packetData = datagramPacket.getData();
        int packetNumber = ByteBuffer.wrap(packetData, 0, 4).getInt();
        if ( packetNumber == 0 ) {
            if (numberOfPackets != -1) {
                return null;
            }
            numberOfPackets = ByteBuffer.wrap(packetData, 4, 4).getInt();
            currentFrame = new HashMap<>();
            return null;
        }
        byte[] packetDataWithoutNumber = new byte[packetData.length - 4];
        System.arraycopy(packetData, 4, packetDataWithoutNumber, 0, packetDataWithoutNumber.length);
        addFrameChunk(packetDataWithoutNumber, packetNumber);
        if (isFrameComplete()) {
            byte[] frameData = getFrameData();
            currentFrame.clear();
            currentFrameLength = 0;
            numberOfPackets = -1;
            return frameData;
        }
        return null;
    }

    private boolean isFrameComplete() {
        return currentFrame.size() == numberOfPackets;
    }

    private void addFrameChunk(byte[] packetData, int packetNumber) {
        if (currentFrame.containsKey(packetNumber)) {
            return;
        }
        currentFrame.put(packetNumber, packetData);
        currentFrameLength += packetData.length;
    }

    private byte[] getFrameData() {
        byte[] frameData = new byte[currentFrameLength];
        int offset = 0;
        for (int i = 1; i <= numberOfPackets; i++) {
            byte[] packetData = currentFrame.get(i);
            System.arraycopy(packetData, 0, frameData, offset, packetData.length);
            offset += packetData.length;
        }

        return frameData;
    }

    private void displayFrame(byte[] frameData) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(frameData);
        Image image = new Image(byteArrayInputStream);
        Platform.runLater(() -> {
            imageView.setImage(image);
        });
    }

    public void interrupt() {
        datagramSocket.close();
        super.interrupt();
    }
}
