@file:JvmName("Main")

import control.ConvertedEvent
import control.DeviceEventEmitter
import networking.ArduinoChannel
import org.firmata4j.Pin
import org.firmata4j.firmata.FirmataDevice


fun main(args: Array<String>) {
    if (args.isNotEmpty() && args.size != 5) {
        println("Usage\nremoteduino SERIAL-PORT LOCAL-IP LOCAL-PORT REMOTE-IP REMOTE-PORT")
        println("defaults: /dev/ttyACM0 localhost 55555")
        return
    }

    val serialPortName: String = if (args.isEmpty()) {
        "COM3"
    } else {
        args[0]
    }

    val ip: String = if (args.isEmpty()) {
        "localhost"
    } else {
        args[1]
    }


    val portNumber: Int = if (args.isEmpty()) {
        55555
    } else {
        args[2].toInt()
    }

    val remoteIp: String = if (args.isEmpty()) {
        "localhost"
    } else {
        args[3]
    }

    val remotePort: Int = if (args.isEmpty()) {
        55556
    } else {
        args[4].toInt()
    }


    // TODO: on the other end, those bytes are inserted to the parser
    val device = FirmataDevice(serialPortName) // construct the Firmata device instance using the name of a port
    val emitter = DeviceEventEmitter()


    val arduino = ArduinoChannel(ip, portNumber)


    println("My port: $portNumber")
    println("My serial port: $serialPortName")


    // Create mapping for pin numbers


    emitter.onPinChange += { e ->
        println("Sending to remote $e")
        arduino.send(e.serialize())
    }

    emitter.onStop += { println("Stopped") }
    println("Press enter to connect")
    readLine()
    arduino.connect(remoteIp, remotePort)

    device.addEventListener(emitter)
    device.start()
    device.ensureInitializationIsDone()

    if (serialPortName == "/dev/ttyUSB0") {
        emitter.addPinMapping(4, 13)
        val pin = device.getPin(4)
        pin.mode = Pin.Mode.INPUT
        println("My pin is 4")

    } else {
        arduino.onReceived += { e ->
            run {
                val receivedConverted = ConvertedEvent.deserialize(e.bytes)!!
                println(receivedConverted)

                val pin = device.getPin(receivedConverted.pinNumber.toInt())
                pin.mode = receivedConverted.mode.toFirmataMode()
                pin.value = receivedConverted.value
            }
        }

        println("My pin is 13")
    }
    println("Hit enter to terminate")
    readLine()
    device.stop()
}