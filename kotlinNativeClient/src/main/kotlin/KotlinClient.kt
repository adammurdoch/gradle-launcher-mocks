import kotlinx.cinterop.*
import platform.posix.*

fun main(args: Array<String>) {
    println("Kotlin Native client")
    val port = readServerPort()
    println("* Server port: ${port}")
    connectToServer(port)
    println("* Done")
}

private fun connectToServer(port: Int) {
    memScoped {
        val fd = socket(PF_INET, SOCK_STREAM, 0)
        if (fd < 0) {
            throw Throwable("Could not open socket")
        }
        try {
            val serverAddr = alloc<sockaddr_in>()
            with(serverAddr) {
                memset(this.ptr, 0, sockaddr_in.size)
                sin_family = AF_INET.narrow()
                sin_port = posix_htons(port.toShort())
            }
            if (connect(fd, serverAddr.ptr.reinterpret(), sockaddr_in.size.toInt()) != 0) {
                throw Throwable("could not connect to server")
            }
        } finally {
            close(fd)
        }
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
                val nread = read(registryFile, pinned.addressOf(0), buffer.size.signExtend())
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
