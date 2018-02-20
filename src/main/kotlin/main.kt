import org.firmata4j.firmata.FirmataMessageFactory
import sun.security.krb5.Confounder.bytes
import java.nio.charset.Charset


fun main(args: Array<String>) {
    val device = SerialChannel("/dev/ttyACM0", { m, p -> println(p.readBytes().joinToString(",")) }) // construct the Firmata device instance using the name of a port

    device.start() // initiate communication to the device
    device.ensureInitializationIsDone() // wait for initialization is done

    println("Started, please wait about 5 secs")
    device.write(FirmataMessageFactory.REQUEST_FIRMWARE)

    println("Hit enter to terminate")
    readLine()
    device.stop()
}