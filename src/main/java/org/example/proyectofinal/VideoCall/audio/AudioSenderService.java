package org.example.proyectofinal.VideoCall.audio;

import org.example.proyectofinal.Constants.DataConstants;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AudioSenderService extends Thread {
    private ServerSocket serverSocket;

    public void run() {
        try {
            serverSocket = new ServerSocket(DataConstants.AUDIO_CALL_PORT);
            Socket socket = serverSocket.accept();
            AudioFormat audioFormat = new AudioFormat(8000.0f, 16, 2, true, true);
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            OutputStream outputStream = socket.getOutputStream();
            byte[] buffer = new byte[DataConstants.MAX_AUDIO_PACKET_SIZE];
            int bytesRead;
            while ((bytesRead = targetDataLine.read(buffer, 0, buffer.length)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            targetDataLine.drain();
            targetDataLine.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
