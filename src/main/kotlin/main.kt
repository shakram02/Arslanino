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
    arduino.onReceived += { e -> println(ConvertedEvent.deserialize(e.bytes)) }

    println("My port: $portNumber")
    println("My serial port: $serialPortName")



    // Create mapping for pin numbers
    

    device.addEventListener(emitter)
    device.start()
    device.ensureInitializationIsDone()

    emitter.onPinChange += { e -> arduino.send(e.serialize()) }
    emitter.onStop += { println("Stopped") }
    println("Press enter to connect")
    readLine()
    arduino.connect(remoteIp, remotePort)   
    if (serialPortName == "COM7") {
        emitter.addPinMapping(4, 13)
        device.getPin(4).setMode(Pin.Mode.INPUT)
        val value = device.getPin(4).getValue()
        println("Pin 4 value = $value") 
        device.getPin(13).setMode(Pin.Mode.OUTPUT)
        device.getPin(13).setValue(255)
        println("My pin is 4")
    
    } else {
        emitter.addPinMapping(13, 4)
        device.getPin(13).setMode(Pin.Mode.OUTPUT)
        println("My pin is 13")
    }
    println("Hit enter to terminate")
    readLine()
    device.stop()
}