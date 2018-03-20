package networking

import org.junit.Assert
import org.junit.Test

class ArduinoChannelTest {
    private val localhost = "localhost"
    private val portOne = 60555
    private val portRemote = 60554

    @Test
    fun connect() {
        var transEstablished = false
        var receiveEstablished = false
        var receiveCount = 0
        val clientOne = ArduinoChannel(localhost, portOne)
        clientOne.onConnectedToRemote += { transEstablished = true }
        clientOne.onReceived += { receiveCount++ }

        val remoteSide = ArduinoChannel(localhost, portRemote)
        remoteSide.onConnectedToRemote += { receiveEstablished = true }
        remoteSide.onReceived += { receiveCount++ }

        remoteSide.connect(localhost, portOne)
        clientOne.connect(localhost, portRemote)
        waitNetworkOperation()

        val msg = "Hello".toByteArray()
        clientOne.send(msg)
        remoteSide.send(msg)
        waitNetworkOperation()

        Assert.assertTrue(transEstablished && receiveEstablished)
        Assert.assertEquals(2, receiveCount)
    }

    private fun waitNetworkOperation() {
        Thread.sleep(100)
    }
}