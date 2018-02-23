## Gradle Launcher experiments

This repo contains several mock-ups of a Gradle launcher that use different implementation languages. The goal is to investigate the performance improvements we might expect by reimplementing or repackaging the Gradle launcher in a different language.

- Java client packaged as a Jar with launcher script. This represents the current Gradle launcher.
- Java client packaged as a JVM image using jlink.
- Kotlin/JVM client packaged as a Jar with launcher script.
- Kotlin/Native client packaged as an executable.
- C++ client package as an executable.

There is also a mock up of the Gradle daemon (the "server"). This does not run any builds. It simply receives a small request message from the client and sends a small message back in response. It is implemented in Java.

The mock ups only work on macOS.

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

Some benchmark results (using `bench <client>`):

Client        | Execution time
--------------|----------------------------
Java          | 247.1 ms (std dev 4.709 ms)
Java jlink    | 212.5 ms (std dev 5.062 ms) 
Kotlin/JVM    | 188.6 ms (std dev 1.796 ms)
Kotlin/Native | 16.34 ms (std dev 388.1 μs)
C++           | 4.990 ms (std dev 114.6 μs)
