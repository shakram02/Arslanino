import jssc.SerialPort
import jssc.SerialPortEvent
import jssc.SerialPortEventListener
import jssc.SerialPortException
import org.firmata4j.firmata.FirmataMessageFactory


import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

class SerialChannel(portName: String, private val onSerialEvent: (SerialPortEvent, SerialPort) -> Unit) : SerialPortEventListener {
    private val port: SerialPort = SerialPort(portName)
    private val started = AtomicBoolean(false)
    private val ready = AtomicBoolean(false)

    companion object {
        private const val TIMEOUT = 15000L
    }


    override fun serialEvent(event: SerialPortEvent) {
        if (!event.isRXCHAR || event.eventValue <= 0)
            return

        onSerialEvent(event, port)
    }

    @Throws(IOException::class)
    fun start() {
        if (started.get()) return

        if (!port.isOpened) {
            try {
                port.openPort()
                port.setParams(
                        SerialPort.BAUDRATE_57600,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE)
                port.eventsMask = SerialPort.MASK_RXCHAR
                port.addEventListener(this)
                started.set(true)
            } catch (ex: SerialPortException) {
                throw IOException("Cannot start firmata device", ex)
            }
        }
    }

    @Throws(IOException::class)
    fun stop() {
        try {
            if (!port.isOpened) return

            port.purgePort(SerialPort.PURGE_RXCLEAR or SerialPort.PURGE_TXCLEAR)
            write(FirmataMessageFactory.analogReport(false))
            write(FirmataMessageFactory.digitalReport(false))

            port.closePort()
            ready.set(false)
            started.set(false)
        } catch (ex: SerialPortException) {
            throw IOException("Cannot properly stop firmata device", ex)
        }
    }

    @Throws(IOException::class)
    fun write(bytes: ByteArray) {
        try {
            port.writeBytes(bytes)
        } catch (ex: SerialPortException) {
            throw IOException("Cannot send message to device", ex)
        }
    }

    @Throws(InterruptedException::class)
    fun ensureInitializationIsDone() {
        if (started.get()) return


        try {
            start()
        } catch (ex: IOException) {
            throw InterruptedException(ex.message)
        }


        var timePassed = 0L
        val timeout: Long = 100
        while (!isReady()) {
            if (timePassed >= TIMEOUT) {
                throw InterruptedException("Connection timeout")
            }
            timePassed += timeout
            Thread.sleep(timeout)
        }
    }

    fun isReady(): Boolean {
        return ready.get()
    }

}