@file:JvmName("Main")

import control.ConvertedEvent
import control.DeviceEventEmitter
import control.DeviceEventReceiver
import networking.ArduinoChannel
import org.firmata4j.Pin
import org.firmata4j.firmata.FirmataDevice


fun main(args: Array<String>) {
    if (args.isNotEmpty() && args.size != 6) {
        println("Usage\nremoteduino SERIAL-PORT LOCAL-IP LOCAL-PORT REMOTE-IP REMOTE-PORT IS-SENDER?(Y/N) -Default N-")
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

    val isTransmitter: Boolean = if (args.isEmpty()) {
        true
    } else {
        args[5].toUpperCase() == "Y"
    }


    // TODO: on the other end, those bytes are inserted to the parser
    val device = FirmataDevice(serialPortName) // construct the Firmata device instance using the name of a port
    val emitter = DeviceEventEmitter()
    val arduino = ArduinoChannel(ip, portNumber)
    val eventReceiver = DeviceEventReceiver(device)

    var connected = false
    emitter.onPinChange += { e ->
        if (connected) {
            arduino.send(e.serialize())
        }
    }
    emitter.onStop += { println("Stopped") }

    arduino.onConnectedToRemote += {
        connected = true
        println("Connected")
    }

    arduino.onClientConnected += {
        println("A client connected")
    }

    arduino.onReceived += { e ->
        val receivedConverted = ConvertedEvent.deserialize(e.bytes)!!
        println(receivedConverted)
        eventReceiver.execute(receivedConverted)
    }

    println("Press enter to connect...")
    readLine()

    arduino.connect(remoteIp, remotePort)

    device.addEventListener(emitter)
    device.start()
    device.ensureInitializationIsDone()

    if (isTransmitter) {
        emitter.addPinMapping(4, 13)
        val pin = device.getPin(4)
        pin.mode = Pin.Mode.INPUT
        println("[Transmitter Mode - Pin 4]")
    }

    println("Hit enter to terminate")
    readLine()

    device.stop()
}