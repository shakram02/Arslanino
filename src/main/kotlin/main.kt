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

    val device = FirmataDevice(serialPortName)
    val emitter = DeviceEventEmitter()
    val commChannel = ArduinoChannel(ip, portNumber)
    val eventReceiver = DeviceEventReceiver(device)

    var connected = false
    emitter.onPinChange += { e ->
        if (connected) {
            commChannel.send(e.serialize())
        }
    }

    emitter.onStop += { println("Stopped") }

    commChannel.onConnectedToRemote += {
        connected = true
        println("Connected")
    }

    commChannel.onClientConnected += {
        println("A client connected")
    }

    commChannel.onReceived += { e ->
        val receivedConverted = ConvertedEvent.deserialize(e.bytes)!!
        println(receivedConverted)
        eventReceiver.execute(receivedConverted)
    }

    device.addEventListener(emitter)
    device.start()
    device.ensureInitializationIsDone()

    println("Press enter to connect...")
    readLine()

    commChannel.connect(remoteIp, remotePort)

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