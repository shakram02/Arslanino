import org.firmata4j.IOEvent
import org.firmata4j.Pin

class IOEventConverter {
    // TODO: Add pin mapping

    fun convert(e: IOEvent): ConvertedEvent {
        val pin = e.pin
        // TODO: match pin numbers on different types
        return convertWithMapping(e, pin.index)
    }


    private fun convertWithMapping(e: IOEvent, outPin: Byte): ConvertedEvent {
        val pin = e.pin
        val outMode: PinMode = when (e.pin.mode) {
            Pin.Mode.INPUT -> PinMode.OUTPUT
            Pin.Mode.ANALOG -> PinMode.PWM
            else -> TODO()
        }

        return ConvertedEvent(outPin, outMode, pin.value, e.timestamp)
    }
}

enum class PinMode {
    INPUT,
    OUTPUT,
    PWM,
    ANALOG_INPUT
}

data class ConvertedEvent(val pinNumber: Byte, val mode: PinMode, val value: Long, val timestamp: Long)
