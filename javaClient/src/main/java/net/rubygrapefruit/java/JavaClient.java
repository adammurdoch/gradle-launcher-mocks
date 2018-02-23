package net.rubygrapefruit.java;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaClient {
    public static void main(String[] args) throws IOException {
        System.out.println("Java client");
        int port = readServerPort();
        System.out.println(String.format("* Server port: %s", port));
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(port));
        }
        System.out.println("* Done");
    }

    private static int readServerPort() throws IOException {
        Path registryFile = Paths.get("build/server.bin");
        try (InputStream registryStream = Files.newInputStream(registryFile)) {
            int b1 = registryStream.read();
            int b2 = registryStream.read();
            return (b1 & 0xFF) | (b2 & 0xFF) << 8;
        }
    }
}
