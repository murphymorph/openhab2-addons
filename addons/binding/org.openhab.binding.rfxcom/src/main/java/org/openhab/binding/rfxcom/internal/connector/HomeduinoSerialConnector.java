package org.openhab.binding.rfxcom.internal.connector;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.TooManyListenersException;

import org.openhab.binding.rfxcom.internal.config.RFXComBridgeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public class HomeduinoSerialConnector extends RFXComSerialConnector
        implements HomeduinoConnectorInterface, SerialPortEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeduinoSerialConnector.class);

    @Override
    public void connect(RFXComBridgeConfiguration device)
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

    @Override
    public void sendMessage(String message) throws IOException {
        LOGGER.debug("Sending message: {}", message);
        sendMessage((message + "\r\n").getBytes(StandardCharsets.US_ASCII));
    }
}
