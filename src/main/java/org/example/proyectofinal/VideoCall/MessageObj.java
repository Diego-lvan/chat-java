package org.example.proyectofinal.VideoCall;

import java.net.InetAddress;

public class MessageObj {
    protected String message;
    protected int serverPort;
    protected int clientPort;
    protected InetAddress clientAddress;
    protected InetAddress serverAddress;

    public MessageObj() {

    }

    public MessageObj(String mensaje, int puertoServidor, int puertoCliente, InetAddress addressCliente, InetAddress addressServidor) {
        this.message = mensaje;
        this.serverPort = puertoServidor;
        this.clientPort = puertoCliente;
        this.clientAddress = addressCliente;
        this.serverAddress = addressServidor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(InetAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(InetAddress serverAddress) {
        this.serverAddress = serverAddress;
    }
}