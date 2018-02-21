package org.firmata4j.transport;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.firmata4j.fsm.Parser;

import java.io.IOException;

public abstract class AbstractSerialTransport implements TransportInterface, SerialPortEventListener {
    protected final SerialPort port;
    protected Parser parser;

    AbstractSerialTransport(String portName) {
        this.port = new SerialPort(portName);
    }

    /**
     * Starts the transport and initializes the connector.
     *
     * @throws IOException
     */
    @Override
    public void start() throws IOException {
        if (!port.isOpened()) {
            try {
                port.openPort();
                port.setParams(
                        SerialPort.BAUDRATE_57600,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                port.setEventsMask(SerialPort.MASK_RXCHAR);
                port.addEventListener(this);
            } catch (SerialPortException ex) {
                throw new IOException("Cannot start firmata device", ex);
            }
        }
    }

    /**
     * Shuts down the connector and stops the transport.
     *
     * @throws IOException
     */
    @Override
    public void stop() throws IOException {
        try {
            if (port.isOpened()) {
                port.purgePort(SerialPort.PURGE_RXCLEAR | SerialPort.PURGE_TXCLEAR);
                port.closePort();
            }
        } catch (SerialPortException ex) {
            throw new IOException("Cannot properly stop firmata device", ex);
        }
    }

    /**
     * Sends data to the device.
     *
     * @param bytes data to send
     * @throws IOException
     */
    @Override
    public void write(byte[] bytes) throws IOException {
        try {
            port.writeBytes(bytes);
        } catch (SerialPortException ex) {
            throw new IOException("Cannot send message to device", ex);
        }

    }

    /**
     * Sets the parser. Transport transmits received data to the parser.
     *
     * @param parser data parser
     */
    @Override
    public void setParser(Parser parser) {
        this.parser = parser;
    }

    @Override
    public abstract void serialEvent(SerialPortEvent serialPortEvent);
}
