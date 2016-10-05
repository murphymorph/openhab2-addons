/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.connector;

import java.io.IOException;

import org.openhab.binding.rfxcom.internal.config.RFXComBridgeConfiguration;

/**
 * This interface defines interface to communicate RFXCOM controller.
 *
 * @author Pauli Anttila - Initial contribution
 */
public interface RFXComConnectorInterface {

    /**
     * Procedure for connecting to RFXCOM controller.
     *
     * @param device
     *            Controller connection parameters (e.g. serial port name or IP
     *            address).
     */
    void connect(RFXComBridgeConfiguration device) throws Exception;

    /**
     * Procedure for disconnecting to RFXCOM controller.
     *
     */
    void disconnect();

    /**
     * Procedure for send raw data to RFXCOM controller.
     *
     * @param data
     *            raw bytes.
     */
    void sendMessage(byte[] data) throws IOException;

    /**
     * Procedure for register event listener.
     *
     * @param listener
     *            Event listener instance to handle events.
     */
    void addEventListener(RFXComEventListener listener);

    /**
     * Procedure for remove event listener.
     *
     * @param listener
     *            Event listener instance to remove.
     */
    void removeEventListener(RFXComEventListener listener);

}
