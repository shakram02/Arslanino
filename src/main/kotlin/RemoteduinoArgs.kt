import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class RemoteduinoArgs(parser: ArgParser) {
    val serialPort by parser.positional("SERIALPORT", help = "Name of Arduino's serial port")

    val localIp by
    parser.positional("LOCALIP", help = "IP Address of the local device. Default \"localhost\"")
            .default("localhost")

    val localPort by
    parser.positional("LOCALPORT", help = "Port number of the local device. Default 50001") { toInt() }
            .default(50000)

    val remoteIp by
    parser.positional("REMOTEIP", help = "IP Address of the remote device. Default \"localhost\"")
            .default("localhost")

    val remotePort by
    parser.positional("REMOTEPORT", help = "Port number of the remote device. Default 50001") { toInt() }
            .default(50001)

    val isTestSender by
    parser.flagging("-t", "--test", help = "Is testing sender device")
            .default(false)
}