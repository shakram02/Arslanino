package control

import org.firmata4j.IOEvent
import org.firmata4j.Pin
import java.lang.IllegalArgumentException

class IOEventConverter {
    private val pinConverter = HashMap<Byte, Byte>()

    companion object {
        private const val ADC_MAX = 1023L
        private const val PWM_MAX = 255L

        fun mapToPwm(x: Long): Long {
            return (x * PWM_MAX) / ADC_MAX
        }
    }

    fun convert(e: IOEvent): ConvertedEvent {
        val pin = e.pin

        // TODO: match pin numbers on different board types
        var convertedPin: Byte = pin.index


        if (pinConverter.containsKey(convertedPin)) {
            convertedPin = pinConverter[convertedPin] as Byte
        } else {
            throw IllegalArgumentException("pin ${pin.index} is not mapped to any output pins")
        }

        return convertWithMapping(e, convertedPin)
    }


    private fun convertWithMapping(e: IOEvent, outPin: Byte): ConvertedEvent {
        val pin = e.pin
        var convertedPinValue = pin.value

        val outMode: PinMode = when (e.pin.mode) {
            Pin.Mode.INPUT -> PinMode.OUTPUT
            Pin.Mode.ANALOG -> {
                // convert 10-bits analog input to 8-bits PWM output
                // NOTE we can't set pin.value as this is translated as an
                // intention to write to input pin, that's why convertedPinValue exists
                convertedPinValue = mapToPwm(pin.value)

                PinMode.PWM
            }
            else -> throw IllegalStateException("${e.pin.mode} can't be converted")
        }

        return ConvertedEvent(outPin, outMode, convertedPinValue, e.timestamp)
    }

    fun addPinMapping(myPin: Byte, theirPin: Byte) {
        pinConverter[myPin] = theirPin
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
                "OUTPUT" -> OUTPUT
                "PWM" -> PWM
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
        fun deserialize(serialized: ByteArray): ConvertedEvent {
            val string = String(serialized)
            val values = string.split("-")

            return ConvertedEvent(values[0].toByte(), PinMode.fromString(values[1]),
                    values[2].toLong(), values[3].toLong())
        }
    }
}
