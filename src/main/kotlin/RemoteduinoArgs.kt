import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class RemoteduinoArgs(parser: ArgParser) {
    val serialPort by parser.positional("SERIALPORT", help = "Name of Arduino's serial port")

    val localIp by
    parser.storing("-s", "--sourcelIp", help = "IP Address of the local device. Default \"localhost\"")
            .default("localhost")

    val localPort by
    parser.storing("-t", "--sourcePort", help = "Port number of the local device. Default 50000 [Tx]") { toInt() }
            .default(50000)

    val remoteIp by
    parser.storing("-d", "--destinationIp", help = "IP Address of the remote device. Default \"localhost\"")
            .default("localhost")

    val remotePort by
    parser.storing("-r", "--destinationPort", help = "Port number of the remote device. Default 50001 [Rx]") { toInt() }
            .default(50001)

    val isTestSender by
    parser.flagging("-x", "--check", help = "Is testing sender device")
            .default(false)
}