Several mock-ups of a Gradle client that use different implementation languages.

- Java client packaged as a Jar with launcher script.
- Java client package as a JVM image using jlink.
- Kotlin native client packaged as an executable.
- C++ client package as an executable.

The mock ups only work on macOS.

To use:

- Run the server using `./gradlew server:run`. This runs in the foreground
- Run the clients using `./gradle runClient`. This builds and runs each of the clients

To time the clients:

- Java + launcher: `time ./javaClient/build/install/javaClient/bin/javaClient`
- Java + jlink: `time ./javaClient/build/client/bin/client` 
- Kotlin native: `time kotlinNativeClient/build/exe/client.kexe`
- C++: `time cppClient/build/install/main/debug/lib/cppClient`
