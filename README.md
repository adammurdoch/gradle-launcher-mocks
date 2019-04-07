## Gradle Launcher experiments

This repo contains several mock-ups of a Gradle launcher. The goal is to investigate the performance improvements we might expect by reimplementing the Gradle launcher in a different language or repackaging using `jlink`.

The following clients are included:

- Java client packaged as a Jar with launcher script. This represents the current Gradle launcher.
- Java client packaged as a JVM image using `jlink`. This is built from the same Java source as the previous client.
- Java client packages as a native executable using GraalVM `native-image`. This is built from the same Java source as the previous client.
- Kotlin/JVM client packaged as a Jar with launcher script. This does not share any code with the Java client.
- Kotlin/Native client packaged as an executable. This does not share any code with the previous client, due to differences in the Kotlin standard libraries for Kotlin/JVM and Kotlin/Native. A little abstraction could address this.
- C++ client packaged as an executable.

There is also a mock up of the Gradle daemon (the "server"). This does not run any builds. It simply receives messages from the client and sends a simple response. It is implemented in Java.

Each client does the same work:

- Read a "daemon registry" mock to locate the server address.
- Sends a small request message to the server.
- Receives a small response message from the server.

To use:

- Run the server using `./gradlew server:run`. This runs in the foreground.
- Run the clients using `./gradle runClient`. This builds and runs each of the clients.
- To build everything: `./gradle assemble`

To time the clients:

- Java: `time ./javaClient/build/install/javaClient/bin/javaClient`
- Java + jlink: `time jlinkClient/build/jlink/bin/client` 
- Java + GraalVM: `time ./graalClient/build/graal` 
- Kotlin/JVM: `time ./kotlinClient/build/install/kotlinClient/bin/kotlinClient`
- Kotlin/Native: `time ./kotlinNativeClient/build/konan/bin/macos_x64/app.kexe`
- C++: `time cppClient/build/install/main/debug/lib/cppClient`

### Benchmark results

Some benchmark results (using `bench <client>`, Kotlin/Native 1.3.20, Kotlin 1.3.20, Java 9.0.4, GraalVM 1.0-RC15, macOS 10.14.4):

Client        | Execution time
--------------|----------------------------
Java          | 198.2 ms (std dev 3.767 ms)
Kotlin/JVM    | 158.5 ms (std dev 3.295 ms)
Java jlink    | 139.7 ms (std dev 7.121 ms) 
Kotlin/Native | 13.21 ms (std dev 585.8 μs)
Java GraalVm  | 8.595 ms (std dev 550.6 μs) 
C++           | 4.372 ms (std dev 396.0 μs)
