/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.connector;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RFXCOM connector for serial port communication.
 *
 * @author James Hewitt-Thomas
 */
abstract class RFXComBaseConnector implements RFXComConnectorInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(RFXComBaseConnector.class);
    private static final List<RFXComEventListener> LISTENERS = new ArrayList<>();

    @Override
    public synchronized void addEventListener(RFXComEventListener rfxComEventListener) {
        if (!LISTENERS.contains(rfxComEventListener)) {
            LISTENERS.add(rfxComEventListener);
        }
    }

    @Override
    public synchronized void removeEventListener(RFXComEventListener listener) {
        LISTENERS.remove(listener);
    }

    void sendMsgToListeners(byte[] msg) {
        try {

            for (RFXComEventListener listener : LISTENERS) {
                listener.packetReceived(msg);
            }

        } catch (Exception e) {
            LOGGER.error("Event listener invoking error", e);
        }
    }

    void sendErrorToListeners(String error) {
        try {

            for (RFXComEventListener listener : LISTENERS) {
                listener.errorOccured(error);
            }

        } catch (Exception e) {
            LOGGER.error("Event listener invoking error", e);
        }
    }
}