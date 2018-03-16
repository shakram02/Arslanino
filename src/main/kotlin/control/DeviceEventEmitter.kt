package control

import org.firmata4j.IODeviceEventListener
import org.firmata4j.IOEvent
import org.firmata4j.Pin
import shakram02.events.Event

class DeviceEventEmitter : IODeviceEventListener {
    val onPinChange = Event<ConvertedEvent>()
    val onStart = Event<Unit>()
    val onStop = Event<Unit>()
    private val converter = IOEventConverter()

    init {
        // TODO take mapping object as a parameter
        // TODO create an event converter with the given mapping
    }

    /**
     * Invoked when an [org.firmata4j.IODevice] has been successfully started
     * and initialized.
     *
     * @param event the event
     */
    override fun onStart(event: IOEvent) {
        onStart(Unit)
    }

    /**
     * Invoked when communication with [IODevice] has been
     * successfully terminated.
     *
     * @param event the event
     */
    override fun onStop(event: IOEvent) {
        onStop(Unit)
    }

    /**
     * Invoked when the state of one of device's pins has been changed.
     * It can be change of mode or a value.
     *
     * @param event the event
     */
    override fun onPinChange(event: IOEvent) {
        val pin = event.pin
        if (pin.mode == Pin.Mode.ANALOG) return // TODO Implement this later (Reading analog values)

        onPinChange(converter.convert(event))
    }

    /**
     * Invoked when a string message has been received from the device.
     *
     * @param event the event
     * @param message the message
     */
    override fun onMessageReceive(event: IOEvent, message: String?) {
        println("Message Received:$message")  // TODO this will need to be implemented
    }

    fun addPinMapping(myPin: Byte, theirPin: Byte) {
        converter.addPinMapping(myPin, theirPin)
    }
}
