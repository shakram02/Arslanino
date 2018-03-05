package control

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue


class RemoteArduino {
    var connectionSocket: Socket? = null

    private val inputQueue = LinkedBlockingQueue<ByteArray>()

    fun RemoteArduinoServer(s: Socket) {
        try {
            println("Client Got Connected  ")
            connectionSocket = s
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class MessageReader(stream: InputStream) : Runnable {
        private val readPackets = LinkedBlockingQueue<ByteArray>()
        private val inputStream = DataInputStream(stream)
        private var willContinue = true

        override fun run() {
            try {
                while (true) {
                    TODO("Read message from socket")
                    readPackets.put(ByteArray(8))
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }

        fun stop() {
            willContinue = false
        }
    }

    class MessageWriter(stream: OutputStream) : Runnable {
        private val toWrite = LinkedBlockingQueue<ByteArray>()
        private val willContinute = true
        private val outputStream = DataOutputStream(stream)

        override fun run() {
            try {
                while (true) {
                    val message = toWrite.poll()
                    TODO("Write message to socket")
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }
}