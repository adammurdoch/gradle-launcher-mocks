## Gradle Launcher experiments

This repo contains several mock-ups of a Gradle launcher. The goal is to investigate the performance improvements we
might expect by reimplementing the Gradle launcher in a different language or repackaging as a native executable.

The following clients are included:

- Java client packaged as a Jar with launcher script. This approximates the current Gradle launcher.
- Java client packaged as a JVM image using `jlink`. This is built from the same Java source as the previous client.
- Java client packages as a native executable using GraalVM `native-image`. This is built from the same Java source as
  the previous client.
- Kotlin/JVM client packaged as a Jar with launcher script. This does not share any code with the Java client.
- Kotlin/Native client packaged as an executable. This does not share any code with the previous client, due to
  differences in the Kotlin standard libraries for Kotlin/JVM and Kotlin/Native. A little abstraction could address
  this, so that most of the code can be shared between a native executable and a JVM based fallback.
- C++ client packaged as an executable. This is included just to see what is possible, not as a serious option for
  reimplementing the Gradle launcher.

There is also a mock up of the Gradle daemon (the "server"). This does not run any builds. It simply receives messages
from the client and sends a simple response. It is implemented in Java.

Each client does the same work:

- Read a "daemon registry" mock to locate the server address.
- Sends a small request message to the server.
- Receives a small response message from the server.

### Prerequisites

 - GraalVM
   1. Use SDKMAN to install GraalVM: `$ sdk install java 22.2.r11-grl`
   2. Install native image component: `$ $SDKMAN_CANDIDATES_DIR/java/22.2.r11-grl/bin/gu install native-image`
   3. ~Point `GRAALVM_HOME` env var to the installation: `$ export GRAALVM_HOME=$SDKMAN_CANDIDATES_DIR/java/22.2.r11-grl`. This step should not be necessary since the `org.graalvm.buildtools.native` plugin [claims](https://graalvm.github.io/native-build-tools/0.9.4/gradle-plugin.html#_installing_graalvm_native_image_tool) it uses toolchains to discover the installation.~

To use:

- Run the server using `./gradlew server:run`. This runs in the foreground.
- Run the clients using `./gradle runClient`. This builds and runs each of the clients.
- To build everything: `./gradle assemble installDist`

To time the clients:

- Java: `time ./javaClient/build/install/javaClient/bin/javaClient`
- Java + jlink: `time ./jlinkClient/build/jlink/bin/client`
- Java + GraalVM: `time ./graalClient/build/native/nativeCompile/graalClient`
- Kotlin/JVM: `time ./kotlinClient/build/install/kotlinClient/bin/kotlinClient`
- Kotlin/Native (M1): `time ./kotlinNativeClient/build/bin/macosArm64/releaseExecutable/kotlinNativeClient.kexe`
- Kotlin/Native (intel): `time ./kotlinNativeClient/build/bin/macosX64/releaseExecutable/kotlinNativeClient.kexe`
- C++: `time ./cppClient/build/install/main/release/cppClient`

### Benchmark results

Some benchmark results (using `hyperfine --warmup 10 -N <client>`, Kotlin 1.7.10, Java 17 (Intel) and Java 18 (M1),
GraalVM 22.2.0 (Intel), macOS 12.6 on a M1 machine)

| Client                         | Execution time      | Installation size |
|--------------------------------|---------------------|-------------------|
| Java (17 - Intel)              | 108.4 ms ± 2.6 ms   | 16K               |
| Java (18 - M1)                 | 50.6 ms ±   1.0 ms  | -                 |
| Kotlin/JVM (17 - Intel)        | 112.8 ms ±   4.5 ms | 1.7M              |
| Kotlin/JVM (18 - M1)           | 52.0 ms ±   1.1 ms  | -                 |
| Java jlink (17 - Intel)        | 136.9 ms ±   1.7 ms | 43M               |
| Java GraalVM (22.2.0 - Intel)  | 89.1 ms ±   1.0 ms  | 11M               |
| Java GraalVM (22.2.r11 - Intel)| 6.3 ms ±   0.5 ms   | 11.9M             |
| Java GraalVM (22.2.r11 - M1)   | 5.1 ms ±   0.7 ms   | 12M               |
| Kotlin/Native (Intel)          | 4.8 ms ±   0.4 ms   | 474K              |
| Kotlin/Native (M1)             | 1.7 ms ±   0.2 ms   | 473K              |
| C++ (Intel)                    | 6.8 ms ±   0.5 ms   | 53K               |
| C++ (M1)                       | 3.7 ms ±   0.4 ms   | 52K               |

#### Older results

Some benchmark results (using `bench <client>`, Kotlin/Native 1.3.20, Kotlin 1.3.20, Java 9.0.4, GraalVM 1.0-RC15, macOS
10.14.4):

| Client        | Execution time              |
|---------------|-----------------------------|
| Java          | 198.2 ms (std dev 3.767 ms) |
| Kotlin/JVM    | 158.5 ms (std dev 3.295 ms) |
| Java jlink    | 139.7 ms (std dev 7.121 ms) |
| Kotlin/Native | 13.21 ms (std dev 585.8 μs) |
| Java GraalVm  | 8.595 ms (std dev 550.6 μs) |
| C++           | 4.372 ms (std dev 396.0 μs) |
