package net.rubygrapefruit.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaClient {
    public static void main(String[] args) throws IOException {
        System.out.println("* Java client");
        int port = readServerPort();
        System.out.println(String.format("* Server port: %s", port));
        handleRequest(port);
    }

    private static int readServerPort() throws IOException {
        Path registryFile = Paths.get("build/server.bin");
        try (InputStream registryStream = Files.newInputStream(registryFile)) {
            int b1 = registryStream.read();
            int b2 = registryStream.read();
            return (b1 & 0xFF) | (b2 & 0xFF) << 8;
        }
    }

    private static void handleRequest(int port) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(port));
            writeRequest(socket);
            readResponse(socket);
        }
    }

    private static void writeRequest(Socket socket) throws IOException {
        String message = "Java client";
        byte[] bytes = message.getBytes("utf8");
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write((byte) bytes.length);
        outputStream.write(bytes);
    }

    private static void readResponse(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        int len = inputStream.read();
        byte[] bytes = new byte[len];
        if (inputStream.readNBytes(bytes, 0, len) != len) {
            throw new IOException("Could not read from server");
        }
        String receivedMessage = new String(bytes, "utf8");
        System.out.println("* Received: " + receivedMessage);
    }
}
