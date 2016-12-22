/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.openhab.binding.homeduino.internal.config.HomeduinoBridgeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

/**
 * Homeduino connector for serial port communication.
 *
 * @author Pauli Anttila - Initial contribution for the RFXCom-binding
 * @author Martin van Wingerden - adapted for usage for the Homeduino-binding
 */
public class HomeduinoSerialConnector implements SerialPortEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeduinoSerialConnector.class);

    private static final List<HomeduinoEventListener> LISTENERS = new ArrayList<>();

    private InputStream in;
    private OutputStream out;
    private SerialPort serialPort;
    private Thread readerThread;

    public void disconnect() {
        LOGGER.debug("Disconnecting");

        if (serialPort != null) {
            serialPort.removeEventListener();
            LOGGER.debug("Serial port event listener stopped");
        }

        if (readerThread != null) {
            LOGGER.debug("Interrupt serial listener");
            readerThread.interrupt();
        }

        if (out != null) {
            LOGGER.debug("Close serial out stream");
            IOUtils.closeQuietly(out);
        }
        if (in != null) {
            LOGGER.debug("Close serial in stream");
            IOUtils.closeQuietly(in);
        }

        if (serialPort != null) {
            LOGGER.debug("Close serial port");
            serialPort.close();
        }

        readerThread = null;
        serialPort = null;
        out = null;
        in = null;

        LOGGER.debug("Closed");
    }

    private void sendMessage(byte[] data) throws IOException {
        LOGGER.trace("Send data (len={}): {}", data.length, DatatypeConverter.printHexBinary(data));
        out.write(data);
        out.flush();
    }

    @Override
    public void serialEvent(SerialPortEvent arg0) {
        try {
            /*
             * See more details from
             * https://github.com/NeuronRobotics/nrjavaserial/issues/22
             */
            LOGGER.trace("RXTX library CPU load workaround, sleep forever");
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException ignore) {
        }
    }

    public void connect(HomeduinoBridgeConfiguration device)
            throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException {
        int baudrate = device.baudrate.intValue();
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(device.serialPort);

        CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

        serialPort = (SerialPort) commPort;
        serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        serialPort.setRTS(true);
        serialPort.setDTR(true);
        serialPort.enableReceiveThreshold(1);
        serialPort.disableReceiveTimeout();

        in = serialPort.getInputStream();
        out = serialPort.getOutputStream();

        out.flush();
        if (in.markSupported()) {
            in.reset();
        }

        // RXTX serial port library causes high CPU load
        // Start event listener, which will just sleep and slow down event loop
        try {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (TooManyListenersException ignore) {
        }

        readerThread = new HomeduinoStreamReader(this, in);
        readerThread.start();
    }

    public void sendMessage(String message) throws IOException {
        LOGGER.debug("Sending message: {}", message);
        sendMessage((message + "\r\n").getBytes(StandardCharsets.US_ASCII));
    }

    public synchronized void addEventListener(HomeduinoEventListener homeduinoEventListener) {
        if (!LISTENERS.contains(homeduinoEventListener)) {
            LISTENERS.add(homeduinoEventListener);
        }
    }

    public synchronized void removeEventListener(HomeduinoEventListener listener) {
        LISTENERS.remove(listener);
    }

    void sendMsgToListeners(byte[] msg) {
        try {
            for (HomeduinoEventListener listener : LISTENERS) {
                listener.packetReceived(msg);
            }
        } catch (Exception e) {
            LOGGER.error("Event listener invoking error", e);
        }
    }

    void sendErrorToListeners(String error) {
        try {
            for (HomeduinoEventListener listener : LISTENERS) {
                listener.errorOccurred(error);
            }
        } catch (Exception e) {
            LOGGER.error("Event listener invoking error", e);
        }
    }
}