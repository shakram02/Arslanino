import control.ConvertedEvent
import control.IOEventConverter
import control.PinMode
import org.firmata4j.IODevice
import org.firmata4j.IOEvent
import org.firmata4j.Pin
import org.firmata4j.PinEventListener
import org.junit.Assert
import org.junit.Test

class ConvertedEventTest {

    @Test
    fun serialize() {
        val converted = ConvertedEvent(12, PinMode.INPUT, 123, 3222)

        val expectedBytes = "12-INPUT-123-3222"
        Assert.assertArrayEquals("Invalid serialization", expectedBytes.toByteArray(), converted.serialize())
    }

    @Test
    fun deserialize() {
        val converted = ConvertedEvent(12, PinMode.INPUT, 123, 3222)
        val bytes = converted.serialize()

        val deserialized = ConvertedEvent.deserialize(bytes)

        Assert.assertTrue(converted.pinNumber == deserialized.pinNumber)
        Assert.assertTrue(converted.mode == deserialized.mode)
        Assert.assertTrue(converted.value == deserialized.value)
        Assert.assertTrue(converted.timestamp == deserialized.timestamp)
    }

    @Test
    fun analog() {
        val converter = IOEventConverter()
        converter.addPinMapping(14, 9)
        val event = IOEvent(FakePin(14, 850, Pin.Mode.ANALOG))
        val converted = converter.convert(event)

        Assert.assertEquals(IOEventConverter.mapToPwm(850), converted.value)

    }

    class FakePin(private val pinNumber: Byte, private var value: Long, private val mode: Pin.Mode) : Pin {
        override fun getIndex(): Byte {
            return pinNumber
        }

        override fun getMode(): Pin.Mode {
            return mode
        }

        override fun getValue(): Long {
            return value
        }

        override fun setValue(value: Long) {
            this.value = value
        }

        override fun setMode(mode: Pin.Mode?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setServoMode(minPulse: Int, maxPulse: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun supports(mode: Pin.Mode?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun removeEventListener(listener: PinEventListener?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getDevice(): IODevice? {
            return null
        }

        override fun removeAllEventListeners() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getSupportedModes(): MutableSet<Pin.Mode> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addEventListener(listener: PinEventListener?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}