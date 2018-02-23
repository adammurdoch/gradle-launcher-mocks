package net.rubygrapefruit.java;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaClient {
    public static void main(String[] args) throws IOException {
        System.out.println("Java client");
        Path registryFile = Paths.get("build/server.bin");
        try (InputStream registryStream = Files.newInputStream(registryFile)) {
            int b1 = registryStream.read();
            int b2 = registryStream.read();
            int port = (b1 & 0xFF) | (b2 & 0xFF) << 8;
            System.out.println(String.format("server port: %s", port));
        }
    }
}
