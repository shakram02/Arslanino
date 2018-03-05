package control

import org.firmata4j.firmata.FirmataDevice

class DeviceEventReceiver(private val device: FirmataDevice) {
    fun execute(e: ConvertedEvent) {
        val pin = device.getPin(e.pinNumber.toInt())

        if (pin.mode != e.mode) {
            pin.mode = e.mode.toFirmataMode()
        }

        pin.value = e.value
    }
}