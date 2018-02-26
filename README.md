## Gradle Launcher experiments

This repo contains several mock-ups of a Gradle launcher. The goal is to investigate the performance improvements we might expect by reimplementing the Gradle launcher in a different language or repackaging using `jlink`.

The following clients are included:

- Java client packaged as a Jar with launcher script. This represents the current Gradle launcher.
- Java client packaged as a JVM image using `jlink`. This is built from the same Java source as the previous client.
- Kotlin/JVM client packaged as a Jar with launcher script. This does not share any code with the Java client.
- Kotlin/Native client packaged as an executable. This does not share any code with the previous client, due to differences in the Kotlin standard libraries for Kotlin/JVM and Kotlin/Native. A little abstraction could address this.
- C++ client packaged as an executable.

There is also a mock up of the Gradle daemon (the "server"). This does not run any builds. It simply receives messsages from the client and sends a simple response. It is implemented in Java.

Each client does the same work:

- Read a "daemon registry" mock to locate the server address.
- Sends a small request message to the server.
- Recevies a small response message from the server.

The mock-ups only work on macOS.

To use:

- Run the server using `./gradlew server:run`. This runs in the foreground.
- Run the clients using `./gradle runClient`. This builds and runs each of the clients.
- To build everything: `./gradle assemble`

To time the clients:

- Java: `time ./javaClient/build/install/javaClient/bin/javaClient`
- Java + jlink: `time ./javaClient/build/client/bin/client` 
- Kotlin/JVM: `time ./kotlinClient/build/install/kotlinClient/bin/kotlinClient`
- Kotlin/Native: `time kotlinNativeClient/build/exe/client.kexe`
- C++: `time cppClient/build/install/main/debug/lib/cppClient`

### Benchmark results

Some benchmark results (using `bench <client>`, Kotlin/Native 0.6, Kotlin 1.2.21, Java 9.0.4):

Client        | Execution time
--------------|----------------------------
Java          | 247.1 ms (std dev 4.709 ms)
Java jlink    | 212.5 ms (std dev 5.062 ms) 
Kotlin/JVM    | 188.6 ms (std dev 1.796 ms)
Kotlin/Native | 16.34 ms (std dev 388.1 μs)
C++           | 4.990 ms (std dev 114.6 μs)
