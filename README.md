Several mock-ups of a Gradle client that use different implementation languages.

- Java client packaged as a Jar with launcher script.
- Java client package as a JVM image using jlink.
- Kotlin/JVM client packaged as a Jar with launcher script.
- Kotlin/Native client packaged as an executable.
- C++ client package as an executable.

The mock ups only work on macOS.

To use:

- Run the server using `./gradlew server:run`. This runs in the foreground
- Run the clients using `./gradle runClient`. This builds and runs each of the clients

To time the clients:

- Java: `time ./javaClient/build/install/javaClient/bin/javaClient`
- Java + jlink: `time ./javaClient/build/client/bin/client` 
- Kotlin/JVM: `time ./kotlinClient/build/install/kotlinClient/bin/kotlinClient`
- Kotlin/Native: `time kotlinNativeClient/build/exe/client.kexe`
- C++: `time cppClient/build/install/main/debug/lib/cppClient`

Some benchmark results:

Client        | Execution time (mean)
--------------|----------------------------
Java          | 246.8 ms (std dev 12.06 ms)
Java jlink    | 203.5 ms (std dev 1.664 ms) 
Kotlin/JVM    | 185.8 ms (std dev 854.1 μs)
Kotlin/Native | 16.92 ms (std dev 437.3 μs)
C++           | 5.047 ms (std dev 236.9 μs)
