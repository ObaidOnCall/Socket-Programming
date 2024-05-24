package cok.hour;

import java.io.*;
import java.net.*;
import java.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ServerWebsocketProtocol {

    private Socket socket;
    private ServerSocket server;
    private InputStream in;
    private OutputStream out;


    // init the server here in this partie
    // this websocket server gona deal just with one client or one connection
    public ServerWebsocketProtocol(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            socket = server.accept();
            System.out.println("Client connected");
        
            in = socket.getInputStream();
            out = socket.getOutputStream();

            handleHandshake();
            listenForMessages();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    private void handleHandshake() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        StringBuilder request = new StringBuilder();
        String webSocketKey = null;

        // Read headers line by line
        while (!(line = reader.readLine()).isEmpty()) {
            request.append(line).append("\r\n");
            if (line.startsWith("Sec-WebSocket-Key")) {
                webSocketKey = line.split(": ")[1];
            }
        }

        if (webSocketKey != null) {
            String webSocketAccept = generateWebSocketAccept(webSocketKey);
            out.write(("HTTP/1.1 101 Switching Protocols\r\n" +
                    "Upgrade: websocket\r\n" +
                    "Connection: Upgrade\r\n" +
                    "Sec-WebSocket-Accept: " + webSocketAccept + "\r\n\r\n").getBytes());
            out.flush();
            System.out.println("Handshake completed");
        } else {
            throw new IOException("Invalid WebSocket handshake request");
        }
    }

    private String generateWebSocketAccept(String key) {
        String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        String acceptKey = key + GUID;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashedBytes = md.digest(acceptKey.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void listenForMessages() throws IOException {
        while (true) {
            int messageOpcode = in.read();
            if (messageOpcode == -1) break;
            int payloadLength = in.read() & 0x7F;
            byte[] mask = new byte[4];
            in.read(mask, 0, 4);

            byte[] messageBytes = new byte[payloadLength];
            for (int i = 0; i < payloadLength; i++) {
                messageBytes[i] = (byte) (in.read() ^ mask[i % 4]);
            }

            String message = new String(messageBytes);
            System.out.println("Received: " + message);

            sendMessage("Hello from server");
        }
    }

    private void sendMessage(String message) throws IOException {
        byte[] messageBytes = message.getBytes();
        out.write(0x81); // Text frame opcode
        out.write(messageBytes.length);
        out.write(messageBytes);
        out.flush();
    }

    private void closeConnections() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            if (server != null) server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServerWebsocketProtocol(5000);
    }
}
