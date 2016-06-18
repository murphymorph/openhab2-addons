package org.openhab.binding.rfxcom.internal.connector;

import java.io.IOException;

public interface HomeduinoConnectorInterface extends RFXComConnectorInterface {

    void connect(String deviceName, int baudrate) throws Exception;

    /**
     * Procedure for sending text data to Homeduino controller.
     *
     * @param data
     *            ascii string.
     */
    void sendMessage(String message) throws IOException;

}
