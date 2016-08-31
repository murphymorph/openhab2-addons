package org.openhab.binding.rfxcom.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openhab.binding.rfxcom.internal.connector.HomeduinoConnectorInterface;
import org.openhab.binding.rfxcom.internal.connector.RFXComEventListener;

public class HomeduinoConnectorMock implements HomeduinoConnectorInterface {
    private boolean connectCalled;
    private boolean connect2Called;

    private List<String> receivedMessages = new ArrayList<String>();

    private static List<RFXComEventListener> _listeners = new ArrayList<RFXComEventListener>();

    @Override
    public void connect(String device) throws Exception {
        this.connectCalled = true;
    }

    @Override
    public void connect(String deviceName, int baudrate) {
        this.connect2Called = true;
    }

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendMessage(String message) {
        receivedMessages.add(message);
    }

    @Override
    public void sendMessage(byte[] data) throws IOException {
        throw new UnsupportedOperationException("not supported");
    }

    public List<String> getReceivedMessages() {
        return receivedMessages;
    }

    public void mockReceiveMessage(String message) {
        byte[] msg = message.getBytes(StandardCharsets.US_ASCII);

        Iterator<RFXComEventListener> iterator = _listeners.iterator();

        while (iterator.hasNext()) {
            iterator.next().packetReceived(msg);
        }
    }

    @Override
    public synchronized void addEventListener(RFXComEventListener rfxComEventListener) {
        if (!_listeners.contains(rfxComEventListener)) {
            _listeners.add(rfxComEventListener);
        }
    }

    @Override
    public synchronized void removeEventListener(RFXComEventListener listener) {
        _listeners.remove(listener);
    }

    public boolean isConnectCalled() {
        return connectCalled;
    }

    public boolean isConnect2Called() {
        return connect2Called;
    }

}
