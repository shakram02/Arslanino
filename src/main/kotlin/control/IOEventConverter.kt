package control

import kotlinx.coroutines.experimental.channels.NULL_VALUE
import org.firmata4j.IOEvent
import org.firmata4j.Pin
import java.lang.IllegalArgumentException

class IOEventConverter {
    // TODO: Add pin mapping
    private val pinConverter = HashMap<Byte, Byte>()

    fun convert(e: IOEvent): ConvertedEvent {
        val pin = e.pin

        // TODO: match pin numbers on different types
        println("Converting...")
        var convertedPin: Byte = pin.getIndex() as Byte
        

        if(pinConverter.containsKey(convertedPin)){
            convertedPin = pinConverter[convertedPin] as Byte
            println("convertedPin = $convertedPin")
        } else {
            //throw IllegalArgumentException("pin $pin.index is not mapped to any pins")
        }

        return convertWithMapping(e, convertedPin)
    }


    private fun convertWithMapping(e: IOEvent, outPin: Byte): ConvertedEvent {
        val pin = e.pin
        val idx = pin.getIndex()
        val value = pin.getValue()
        println("Pin = $idx, value = $value")
        val outMode: PinMode = when (e.pin.mode) {
            Pin.Mode.INPUT -> PinMode.OUTPUT
            Pin.Mode.ANALOG -> PinMode.PWM
            Pin.Mode.OUTPUT -> PinMode.INPUT
            else -> TODO()
        }
        println("outPin = $outPin, outMode = $outMode")
        return ConvertedEvent(outPin, outMode, pin.value, e.timestamp)
    }

    public fun addPinMapping(myPin: Byte, theirPin: Byte) {
        println("$myPin is mapped to $theirPin")
        pinConverter.put(myPin, theirPin)
    }
}

enum class PinMode {
    INPUT,
    OUTPUT,
    PWM,
    ANALOG;

    companion object {
        fun fromString(str: String): PinMode {
            return when (str.toUpperCase()) {
                "INPUT" -> INPUT
                "ANALOG" -> ANALOG
                else -> throw IllegalArgumentException("Invalid Enum Value $str")
            }
        }
    }

    fun toFirmataMode(): Pin.Mode {
        return when (this) {
            OUTPUT -> Pin.Mode.OUTPUT
            PWM -> Pin.Mode.PWM
            INPUT -> Pin.Mode.INPUT
            else -> TODO("Can't convert $this")
        }
    }

}

data class ConvertedEvent(val pinNumber: Byte, val mode: PinMode, val value: Long, val timestamp: Long) {
    override fun toString(): String {
        return "$pinNumber-$mode-$value-$timestamp"
    }


    // TODO find a better serialization mechanism
    fun serialize(): ByteArray {
        return toString().toByteArray()
    }

    companion object {
        fun deserialize(serialized: ByteArray): ConvertedEvent? {
            val string = String(serialized)
            val values = string.split("-")
            val size = values.size
            println(values)
            println("Values.size = $size")
            if(values.size > 0) {
                return ConvertedEvent(values[0].toByte(), PinMode.fromString(values[1]),
                    values[2].toLong(), values[3].toLong())
            } 
            return null
        }
    }
}
