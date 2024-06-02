package org.example.proyectofinal.VideoCall.audio;

import org.example.proyectofinal.Constants.DataConstants;
import org.example.proyectofinal.Constants.DataConstants;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class AudioConsumerService extends Thread {
    private final String host;
    private boolean isConnected = false;
    public AudioConsumerService(String host) {
        this.host = host;
    }
    public void run() {
        Socket socket = null;
        while (!isConnected) {
            try {
                socket = new Socket(host, DataConstants.AUDIO_CALL_PORT);
                isConnected = true;
            } catch (IOException e) {
                System.out.println("Waiting for connection");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();

                }
            }
        }
        receiveAudio(socket);
    }

    private void receiveAudio(Socket socket) {
        try {
            InputStream inputStream = socket.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                AudioFormat audioFormat = new AudioFormat(16000.0f, 16, 1, true, true);
                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
                SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

                sourceDataLine.open(audioFormat);
                sourceDataLine.start();

                byte[] buffer = new byte[DataConstants.MAX_AUDIO_PACKET_SIZE];
                int bytesRead;

                while ((bytesRead = bufferedInputStream.read(buffer, 0, buffer.length)) != -1) {
                    sourceDataLine.write(buffer, 0, bytesRead);
                }

                sourceDataLine.drain();
                sourceDataLine.close();
                socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
