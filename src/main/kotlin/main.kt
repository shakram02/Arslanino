@file:JvmName("Main")

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import control.ConvertedEvent
import control.DeviceEventEmitter
import control.DeviceEventReceiver
import networking.ArduinoChannel
import org.firmata4j.Pin
import org.firmata4j.firmata.FirmataDevice


fun main(args: Array<String>) = mainBody {
    val parsedArgs = ArgParser(args).parseInto(::RemoteduinoArgs)

    val device = FirmataDevice(parsedArgs.serialPort)
    val emitter = DeviceEventEmitter()
    val commChannel = ArduinoChannel(parsedArgs.localIp, parsedArgs.localPort)
    val eventReceiver = DeviceEventReceiver(device)

    var connected = false
    emitter.onSenableEvent += { e ->
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

    device.addEventListener(emitter)
    device.start()
    device.ensureInitializationIsDone()

    println("Press enter to connect...")
    readLine()

    commChannel.connect(parsedArgs.remoteIp, parsedArgs.remotePort)

    if (parsedArgs.isTestSender) {
        emitter.addPinMapping(4, 13)
        val pin = device.getPin(4)
        pin.mode = Pin.Mode.INPUT
        println("[Transmitter Mode - Pin 4]")
    } else {
        commChannel.onReceived += { e ->
            val receivedConverted = ConvertedEvent.deserialize(e.bytes)!!
            println("Received: $receivedConverted")
            eventReceiver.execute(receivedConverted)
        }
    }

    println("Hit enter to terminate")
    readLine()

    device.stop()
    commChannel.close()
}