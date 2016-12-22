/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.connector;

/**
 * This interface defines interface to receive data from Homeduino controller.
 *
 * @author Martin van Wingerden - Initial contribution
 */
public interface HomeduinoEventListener {

    /**
     * Procedure for receiving raw data from Homeduino controller.
     *
     * @param data Received raw data.
     */
    void packetReceived(byte[] data);

    /**
     * Procedure for receiving information on error.
     *
     * @param error Error occurred.
     */
    void errorOccurred(String error);

}
