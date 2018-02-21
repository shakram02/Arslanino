/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Oleg Kurbatov (o.v.kurbatov@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.firmata4j.transport;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.firmata4j.fsm.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Allows connections over the serial interface.
 *
 * @author Ali Kia
 */
public class PublishingSerialTransport extends AbstractSerialTransport {
    private final Consumer<byte[]> eventEmitter;
    private static final Logger LOGGER = LoggerFactory.getLogger(PublishingSerialTransport.class);

    public PublishingSerialTransport(String portName, Consumer<byte[]> eventEmitter) {
        super(portName);
        this.eventEmitter = eventEmitter;
    }

    @Override
    public void setParser(Parser parser) {
        this.parser = parser;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        // queueing data from input buffer to processing by FSM logic
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                // TODO: send those bytes to the remote device
                // TODO: on the other end, those bytes are inserted to the parser
                this.eventEmitter.accept(port.readBytes());
            } catch (SerialPortException ex) {
                LOGGER.error("Cannot read from device", ex);
            }
        }
    }

    public void handleEvent(byte[] bytes) {
        parser.parse(bytes);
    }

}
