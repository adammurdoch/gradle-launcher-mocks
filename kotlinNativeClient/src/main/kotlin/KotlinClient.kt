import kotlinx.cinterop.*
import platform.posix.*

fun main(args: Array<String>) {
    println("Kotlin Native client")
    memScoped {
        val f = "../server/build/server.bin"
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
                val port = b1 and 0xFF or (b2 and 0xFF shl 8)
                println("server port: ${port}")
            }
        } finally {
            close(registryFile)
        }
    }
}
