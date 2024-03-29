import kotlinx.cinterop.*
import platform.posix.*

fun main() {
    println("* Kotlin Native client")
    val port = readServerPort()
    println("* Server port: ${port}")
    handleRequest(port)
}

private fun handleRequest(port: Int) {
    memScoped {
        val socket = connectToServer(port)
        try {
            writeRequest(socket)
            readResponse(socket)
        } finally {
            close(socket)
        }
    }
}

private fun connectToServer(port: Int): Int {
    memScoped {
        val fd = socket(PF_INET, SOCK_STREAM, 0)
        if (fd < 0) {
            throw Throwable("Could not open socket")
        }
        val serverAddr = alloc<sockaddr_in>()
        with(serverAddr) {
            memset(this.ptr, 0, sizeOf<sockaddr_in>().convert())
            sin_family = AF_INET.convert()
            sin_port = posix_htons(port.convert()).convert()
        }
        if (connect(fd, serverAddr.ptr.reinterpret(), sizeOf<sockaddr_in>().convert()) != 0) {
            throw Throwable("could not connect to server")
        }
        return fd
    }
}

private fun writeRequest(socket: Int) {
    memScoped {
        val message = "Kotlin/Native  "
        val lenBuffer = ByteArray(1)
        lenBuffer[0] = message.length.toByte()
        write(socket, lenBuffer.refTo(0), 1)
        write(socket, message.cstr, message.length.convert())
    }
}

private fun readResponse(socket: Int) {
    memScoped {
        val lenBuffer = ByteArray(1)
        read(socket, lenBuffer.refTo(0), 1)
        val len = lenBuffer[0]
        val buffer = ByteArray(len.toInt())
        read(socket, buffer.refTo(0), len.convert())
        val message = buffer.toKString()
        println("* Received: $message")
    }
}

private fun readServerPort(): Int {
    memScoped {
        val f = "build/server.bin"
        val registryFile = open(f, 0)
        if (registryFile < 0) {
            throw Throwable("Could not open file ${f}")
        }
        try {
            val buffer = ByteArray(2)
            val nread = read(registryFile, buffer.refTo(0), buffer.size.convert())
            if (nread.convert<Int>() != 2) {
                throw Throwable("Could not read server port from ${f}")
            }
            val b1 = buffer[0].toInt()
            val b2 = buffer[1].toInt()
            return b1 and 0xFF or (b2 and 0xFF shl 8)
        } finally {
            close(registryFile)
        }
    }
}
