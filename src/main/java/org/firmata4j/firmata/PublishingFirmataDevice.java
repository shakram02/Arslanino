package org.firmata4j.firmata;

import org.firmata4j.transport.PublishingSerialTransport;
import java.util.function.Consumer;

public class PublishingFirmataDevice extends FirmataDevice {
    private final PublishingSerialTransport publishingSerialTransport;

    /**
     * Constructs FirmataDevice instance on specified port.
     *
     * @param portName the port name the device is connected to
     */
    public PublishingFirmataDevice(String portName, Consumer<byte[]> eventEmitter) {
        super(new PublishingSerialTransport(portName, eventEmitter));
        publishingSerialTransport = (PublishingSerialTransport) this.transport;
    }

    /**
     * Sends bytes received from the remote end to the connected Firmata device
     *
     * @param bytes Firmata protocol message
     */
    public void writeToInterface(byte[] bytes) {
        publishingSerialTransport.handleEvent(bytes);
    }
}
