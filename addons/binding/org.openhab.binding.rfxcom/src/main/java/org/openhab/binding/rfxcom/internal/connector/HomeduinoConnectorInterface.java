/**
 * Copyright (c) 2010-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
