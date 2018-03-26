package networking

import shakram02.blue.BlueClient
import shakram02.blue.BlueServer
import java.nio.channels.AsynchronousCloseException


class ArduinoChannel(listenerIp: String, listenerPort: Int) {
    private val server = BlueServer()
    private val client = BlueClient()
    val onReceived = server.onReceived
    val onClientConnected = server.onConnected
    val onConnectedToRemote = client.onConnected

    init {
        server.start(listenerIp, listenerPort)
    }

    fun connect(ip: String, port: Int) {
        client.connect(ip, port)
    }

    fun send(msg: ByteArray) {
        client.send(msg)
    }

    fun close() {
        // Exceptions thrown aren't important, they just denote
        // that a pending operation was force terminated
        try {
            server.close()
        } catch (e: AsynchronousCloseException) {
        }

        try {
            client.close()
        } catch (e: AsynchronousCloseException) {
        }
    }
}