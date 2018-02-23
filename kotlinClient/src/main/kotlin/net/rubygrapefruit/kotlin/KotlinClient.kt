package net.rubygrapefruit.kotlin

import java.io.File

fun main(args: Array<String>) {
    println("Kotlin JVM client")
    val registryFile = File("build/server.bin")
    registryFile.inputStream().use {
        val b1 = it.read()
        val b2 = it.read()
        val port = b1 and 0xFF or (b2 and 0xFF shl 8)
        println("server port: ${port}")
    }
}