package org.example.proyectofinal.FIle;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileSend extends Thread {
    private final String path;
    private final Socket socket;

    public FileSend(String path, Socket socket) {
        this.path = path;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            sendFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFile() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            dataOutputStream.writeUTF(file.getName());
            dataOutputStream.writeLong(file.length());

            byte[] buffer = new byte[4096];
            int read;
            while ((read = fileInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, read);
            }
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
