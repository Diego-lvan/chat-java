package org.example.proyectofinal.FIle;

import org.example.proyectofinal.Constants.DataConstants;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileCli extends Thread{
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(DataConstants.IN_FILE_PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                receiveFile(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveFile(Socket socket) {
        try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {
            String fileName = dataInputStream.readUTF();
            long fileLength = dataInputStream.readLong();

            try (FileOutputStream fileOutputStream = new FileOutputStream(DataConstants.PATH_TO_SAVE + fileName)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                double startTime = System.currentTimeMillis();
                long totalBytesRead = 0;
                long lastBytesRead = 0;

                while (totalBytesRead < fileLength && (bytesRead = dataInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                    double endTime = System.currentTimeMillis();
                    Thread.sleep((int)(Math.random() * 1000));
                    if (endTime - startTime > 1000) {
                        double seconds = (endTime - startTime) / 1000;
                        double bps = (totalBytesRead - lastBytesRead) * 8 / seconds;
                        if (bps > 1000000000){
                            bps = bps / 1000000000;
                            System.out.println("Seconds: " + seconds +" Gbps: " + bps);
                        } else if (bps > 1000000){
                            bps = bps / 1000000;
                            System.out.println("Seconds: " + seconds +" Mbps: " + bps);
                        } else if (bps > 1000){
                            bps = bps / 1000;
                            System.out.println("Seconds: " + seconds +" Kbps: " + bps);
                        } else if (bps > 0){
                            System.out.println("Seconds: " + seconds +" bps: " + bps);
                        }
                        startTime = endTime;
                        lastBytesRead = totalBytesRead;
                    }
                    totalBytesRead += bytesRead;
                }

                System.out.println("File received from client: " + fileName);

            } catch (IOException e) {
                System.err.println("File transfer error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("Error receiving file: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}
