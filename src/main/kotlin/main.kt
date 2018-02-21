import org.firmata4j.firmata.PublishingFirmataDevice
import java.util.concurrent.LinkedTransferQueue


fun main(args: Array<String>) {
    val messages = LinkedTransferQueue<ByteArray>()
    val device = PublishingFirmataDevice("/dev/ttyACM0", { b -> messages.add(b) }) // construct the Firmata device instance using the name of a port

    val sendThread = Thread {
        while (!Thread.interrupted()) {
            val message = messages.take()!!
            device.writeToInterface(message)
        }
    }

    sendThread.isDaemon = true
    sendThread.start()

    device.start() // initiate communication to the device
    device.ensureInitializationIsDone() // wait for initialization is done

    val pin = device.getPin(13)
    pin.value = 1

    println("Hit enter to turn off")
    readLine()

    pin.value = 0

    println("Hit enter to terminate")
    readLine()
    device.stop()
}