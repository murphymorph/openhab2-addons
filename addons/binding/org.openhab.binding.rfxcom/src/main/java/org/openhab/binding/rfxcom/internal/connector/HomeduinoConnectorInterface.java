package org.openhab.binding.rfxcom.internal.connector;

import java.io.IOException;

public interface HomeduinoConnectorInterface extends RFXComConnectorInterface {
    /**
     * Procedure for sending text data to Homeduino controller.
     *
     * @param message
     *            ascii string.
     */
    void sendMessage(String message) throws IOException;
}
