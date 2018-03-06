import control.ConvertedEvent
import control.PinMode
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

}