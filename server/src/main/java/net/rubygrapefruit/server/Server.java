package net.rubygrapefruit.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private final Map<String, AtomicInteger> counters = new HashMap<>();

    public static void main(String[] args) throws IOException {
        System.out.println("test server");
        new Server().run();
    }

    private void run() throws IOException {
        try (ServerSocket socket = new ServerSocket()) {
            socket.bind(new InetSocketAddress(0));
            writeRegistryFile(socket);
            while (true) {
                try (Socket client = socket.accept()) {
                    System.out.println("received client connection");
                    handleClient(client);
                }
            }
        }
    }

    private void handleClient(Socket client) throws IOException {
        InputStream inputStream = client.getInputStream();
        int len = inputStream.read();
        byte[] buffer = new byte[len];
        if (inputStream.read(buffer, 0, len) != len) {
            System.out.println("-> could not read from client");
            return;
        }
        String receivedMessage = new String(buffer, "utf8");
        System.out.println(String.format("received message from client: %s", receivedMessage));

        counters.computeIfAbsent(receivedMessage, (msg) -> new AtomicInteger());
        int count = counters.get(receivedMessage).incrementAndGet();

        buffer = ("Hello " + receivedMessage + ", greeted " + count + " times").getBytes("utf8");
        OutputStream outputStream = client.getOutputStream();
        outputStream.write(buffer.length);
        outputStream.write(buffer);
    }

    private void writeRegistryFile(ServerSocket socket) throws IOException {
        int port = socket.getLocalPort();
        System.out.println(String.format("listening on port: %s", port));
        Path registryFile = Paths.get("../build/server.bin");
        try (OutputStream registryStream = Files.newOutputStream(registryFile)) {
            registryStream.write(port & 0xFF);
            registryStream.write((port >> 8) & 0xFF);
        }
    }
}
