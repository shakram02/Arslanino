import control.DeviceEventEmitter
import org.firmata4j.Pin
import org.firmata4j.firmata.FirmataDevice


fun main(args: Array<String>) {
    // TODO: on the other end, those bytes are inserted to the parser
    val device = FirmataDevice("/dev/ttyACM0") // construct the Firmata device instance using the name of a port

    val emitter = DeviceEventEmitter()
    emitter.onPinChange += { e -> println(">>> $e") }
    emitter.onStop += { println("Stopped") }

    device.addEventListener(emitter)
    device.start()
    device.ensureInitializationIsDone()
    device.getPin(4).mode = Pin.Mode.INPUT


    println("Hit enter to terminate")
    readLine()
    device.stop()
}