package org.example.proyectofinal.Message;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import org.example.proyectofinal.VideoCall.MessageObj;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

public class MessageCli extends Thread {
    private final DatagramSocket udpSocket;
    private final int localPort;
    private final Map<String, ObservableList<String>> messageMap;
    private final ObservableSet<String> activeChats;

    public MessageCli(DatagramSocket socket, Map<String, ObservableList<String>> messages, ObservableSet<String> chatList) throws Exception {
        this.localPort = socket.getLocalPort();
        this.udpSocket = socket;
        this.messageMap = messages;
        this.activeChats = chatList;
    }

    @Override
    public void run() {
        try {
            MessageObj incomingMessage;
            while (true) {
                incomingMessage = receiveIncomingMessage();
                handleIncomingMessage(incomingMessage);
            }
        } catch (Exception e) {
            System.err.println("Error receiving message: " + e.getMessage());
        }
    }

    private MessageObj receiveIncomingMessage() throws Exception {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        udpSocket.receive(packet);
        String receivedMsg = new String(buffer, 0, packet.getLength()).trim();

        MessageObj messageObj = new MessageObj();
        messageObj.setServerAddress(packet.getAddress());
        messageObj.setServerPort(packet.getPort());
        messageObj.setMessage(receivedMsg);

        System.out.println("Received message: " + receivedMsg);

        return messageObj;
    }

    private void handleIncomingMessage(MessageObj messageObj) {
        Platform.runLater(() -> {
            String senderIp = messageObj.getServerAddress().getHostAddress();
            activeChats.add(senderIp);
            System.out.println("Sender IP: " + senderIp);

            if (!messageMap.containsKey(senderIp)) {
                messageMap.put(senderIp, FXCollections.observableArrayList());
            }

            String displayMessage = senderIp + ": " + messageObj.getMessage();
            messageMap.get(senderIp).add(displayMessage);
        });
    }
}
