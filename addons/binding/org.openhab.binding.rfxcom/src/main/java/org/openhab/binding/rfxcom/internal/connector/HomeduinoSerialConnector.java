package org.openhab.binding.rfxcom.internal.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TooManyListenersException;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.openhab.binding.rfxcom.internal.config.RFXComBridgeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class HomeduinoSerialConnector extends RFXComBaseConnector implements HomeduinoConnectorInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(RFXComJD2XXConnector.class);

    private InputStream in;
    private OutputStream out;
    private SerialPort serialPort;
    private Thread readerThread;

    @Override
    public void connect(RFXComBridgeConfiguration device) throws Exception {
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

        readerThread = new SerialReader(in);
        readerThread.start();
    }

    @Override
    public void disconnect() {
        LOGGER.debug("Disconnecting");

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

    @Override
    public void sendMessage(String message) throws IOException {
        sendMessage((message + "\r\n").getBytes(StandardCharsets.US_ASCII));
    }

    @Override
    public void sendMessage(byte[] data) throws IOException {
        LOGGER.trace("Send data (len={}): {}", data.length, DatatypeConverter.printHexBinary(data));
        out.write(data);
        out.flush();
    }


    // extract this similar to RFXComStreamReader

    public class SerialReader extends Thread implements SerialPortEventListener {
        boolean interrupted = false;
        InputStream in;

        public SerialReader(InputStream in) {
            this.in = in;
        }

        @Override
        public void interrupt() {
            interrupted = true;
            super.interrupt();
            try {
                in.close();
            } catch (IOException e) {
            } // quietly close
        }

        @Override
        public void run() {
            final int dataBufferMaxLen = Short.MAX_VALUE;

            byte[] dataBuffer = new byte[dataBufferMaxLen];

            int index = 0;
            boolean start_found = false;

            LOGGER.debug("Data listener started");

            // RXTX serial port library causes high CPU load
            // Start event listener, which will just sleep and slow down event loop
            try {
                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);
            } catch (TooManyListenersException e) {
            }

            try {

                byte[] tmpData = new byte[20];
                int len = -1;

                while ((len = in.read(tmpData)) > 0 && !interrupted) {

                    byte[] logData = Arrays.copyOf(tmpData, len);
                    LOGGER.debug("Received data (len={}): {}", len, DatatypeConverter.printHexBinary(logData));

                    for (int i = 0; i < len; i++) {

                        if (index > dataBufferMaxLen) {
                            // too many bytes received, try to find new start
                            start_found = false;
                        }

                        if (!start_found && ((tmpData[i] >= 97 && tmpData[i] <= 122) // a-z
                                || (tmpData[i] >= 65 && tmpData[i] <= 90) // A-Z
                        )) {

                            start_found = true;
                            index = 0;
                            dataBuffer[index++] = tmpData[i];

                        } else if (start_found) {

                            // line feed & carriage return
                            if (tmpData[i] == 13) {
                                // ignore the line feed
                            } else if (tmpData[i] == 10) {
                                // whole message received, send an event
                                byte[] msg = new byte[index];

                                System.arraycopy(dataBuffer, 0, msg, 0, index);

                                sendMsgToListeners(msg);

                                // find new start
                                start_found = false;
                            } else {
                                dataBuffer[index++] = tmpData[i];
                            }
                        }
                    }
                }
            } catch (InterruptedIOException e) {
                Thread.currentThread().interrupt();
                LOGGER.error("Interrupted via InterruptedIOException");
            } catch (IOException e) {
                LOGGER.error("Reading from serial port failed", e);
                sendErrorToListeners(e.getMessage());
            }

            serialPort.removeEventListener();
            LOGGER.debug("Data listener stopped");
        }

        @Override
        public void serialEvent(SerialPortEvent arg0) {
            try {
                /*
                 * See more details from
                 * https://github.com/NeuronRobotics/nrjavaserial/issues/22
                 */
                LOGGER.trace("RXTX library CPU load workaround, sleep forever");
                sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
            }
        }
    }
}
