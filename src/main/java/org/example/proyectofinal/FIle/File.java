package org.example.proyectofinal.FIle;

import org.example.proyectofinal.Constants.DataConstants;

import java.net.Socket;

public class File {
    public final FileCli fileServiceConsumer;
    public File() {
        fileServiceConsumer = new FileCli();
        fileServiceConsumer.start();
    }

    public void sendFile(String path, String ip) {
        try {
            Socket socket = new Socket(ip, DataConstants.IN_FILE_PORT);
            FileSend fileServiceSender = new FileSend(path, socket);
            fileServiceSender.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
