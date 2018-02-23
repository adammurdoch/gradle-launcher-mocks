package net.rubygrapefruit.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Server {
    public static void main(String[] args) throws IOException {
        System.out.println("test server");
        try (ServerSocket socket = new ServerSocket()) {
            socket.bind(new InetSocketAddress(0));
            int port = socket.getLocalPort();
            System.out.println(String.format("listening on port: %s", port));
            Path registryFile = Paths.get("build/server.bin");
            try (OutputStream registryStream = Files.newOutputStream(registryFile)) {
                registryStream.write(port & 0xFF);
                registryStream.write((port >> 8) & 0xFF);
            }
        }
    }
}
