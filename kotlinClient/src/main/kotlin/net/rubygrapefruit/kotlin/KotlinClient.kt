package net.rubygrapefruit.kotlin

import java.io.File
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.charset.Charset

fun main(args: Array<String>) {
    println("* Kotlin JVM client")
    val port = readServerPort()
    println("* Server port: $port")
    handleRequest(port)
}

private fun readServerPort(): Int {
    val registryFile = File("build/server.bin")
    registryFile.inputStream().use {
        val b1 = it.read()
        val b2 = it.read()
        return b1 and 0xFF or (b2 and 0xFF shl 8)
    }
}

private fun handleRequest(port: Int) {
    Socket().use { it ->
        it.connect(InetSocketAddress(port))
        writeRequest(it)
        readResponse(it)
    }
}

private fun writeRequest(socket: Socket) {
    val message = "Kotlin/JVM".toByteArray()
    val outputStream = socket.getOutputStream()
    outputStream.write(message.size)
    outputStream.write(message)
}

fun readResponse(socket: Socket) {
    val len = socket.getInputStream().read()
    val buffer = ByteArray(len)
    if (socket.getInputStream().readNBytes(buffer, 0, len) != len) {
        throw Throwable("Could not read from server")
    }
    val response = String(buffer, Charset.forName("utf8"))
    println("* Received: $response")
}
