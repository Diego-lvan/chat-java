package org.example.proyectofinal.VideoCall;

import javafx.scene.image.ImageView;

public class VideoCallService {
    public VideoCallSender videoCallSender;
    public VideoCallConsumer videoCallReceiver;
    public VideoCallService(String receiverIp, ImageView consumerImageView, ImageView senderImageView) {
        videoCallSender = new VideoCallSender(receiverIp, senderImageView);
        videoCallReceiver = new VideoCallConsumer(consumerImageView, receiverIp);
    }

    public void start() {
        videoCallSender.start();
        videoCallReceiver.start();
    }

    public void stop() {
        videoCallSender.interrupt();
        videoCallReceiver.interrupt();
    }
}
