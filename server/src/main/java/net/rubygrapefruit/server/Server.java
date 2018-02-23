package net.rubygrapefruit.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Server {
    public static void main(String[] args) throws IOException {
        System.out.println("test server");
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

    private static void handleClient(Socket client) throws IOException {
        int len = client.getInputStream().read();
        byte[] buffer = new byte[len];
        if (client.getInputStream().read(buffer, 0, len) != len) {
            System.out.println("-> could not read from client");
            return;
        }
        String message = new String(buffer, "utf8");
        System.out.println(String.format("received message from client: %s", message));
    }

    private static void writeRegistryFile(ServerSocket socket) throws IOException {
        int port = socket.getLocalPort();
        System.out.println(String.format("listening on port: %s", port));
        Path registryFile = Paths.get("../build/server.bin");
        try (OutputStream registryStream = Files.newOutputStream(registryFile)) {
            registryStream.write(port & 0xFF);
            registryStream.write((port >> 8) & 0xFF);
        }
    }
}
