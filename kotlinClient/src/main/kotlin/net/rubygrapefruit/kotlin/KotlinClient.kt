package net.rubygrapefruit.kotlin

import java.io.File
import java.net.InetSocketAddress
import java.net.Socket

fun main(args: Array<String>) {
    println("Kotlin JVM client")
    val port = readServerPort()
    println("* Server port: $port")
    writeToServer(port)
    println("* Done")
}

private fun writeToServer(port: Int) {
    Socket().use { it ->
        it.connect(InetSocketAddress(port))
        val message = "Kotlin/JVM".toByteArray()
        it.getOutputStream().write(message.size)
        it.getOutputStream().write(message)
    }
}

private fun readServerPort(): Int {
    val registryFile = File("build/server.bin")
    registryFile.inputStream().use {
        val b1 = it.read()
        val b2 = it.read()
        return b1 and 0xFF or (b2 and 0xFF shl 8)
    }
}
