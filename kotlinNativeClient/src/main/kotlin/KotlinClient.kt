import kotlinx.cinterop.*
import platform.posix.*

fun main(args: Array<String>) {
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
            memset(this.ptr, 0, sockaddr_in.size.toULong())
            sin_family = AF_INET.toUByte()
            sin_port = posix_htons(port.toShort()).toUShort()
        }
        if (connect(fd, serverAddr.ptr.reinterpret(), sockaddr_in.size.toUInt()) != 0) {
            throw Throwable("could not connect to server")
        }
        return fd
    }
}

private fun writeRequest(socket: Int) {
    memScoped {
        val message = "Kotlin/Native  ".toUtf8()
        val lenBuffer = ByteArray(1)
        lenBuffer[0] = message.size.toByte()
        lenBuffer.usePinned { pinned ->
            write(socket, pinned.addressOf(0), 1)
        }
        message.usePinned { pinned ->
            write(socket, pinned.addressOf(0), message.size.toULong())
        }
    }
}

private fun readResponse(socket: Int) {
    memScoped {
        val lenBuffer = ByteArray(1)
        lenBuffer.usePinned { pinned ->
            read(socket, pinned.addressOf(0), 1)
        }
        val len = lenBuffer[0]
        val buffer = ByteArray(len.toInt())
        buffer.usePinned { pinned ->
            read(socket, pinned.addressOf(0), len.toULong())
        }
        val message = buffer.stringFromUtf8()
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
            buffer.usePinned { pinned ->
                val nread = read(registryFile, pinned.addressOf(0), buffer.size.toULong())
                if (nread.narrow<Int>() != 2) {
                    throw Throwable("Could not read server port from ${f}")
                }
                val b1 = buffer[0].toInt()
                val b2 = buffer[1].toInt()
                return b1 and 0xFF or (b2 and 0xFF shl 8)
            }
        } finally {
            close(registryFile)
        }
    }
}
