/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.messages;

import org.openhab.binding.homeduino.internal.exceptions.HomeduinoException;

/**
 * Test helper for Homeduino tests
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class TestHelper {
    public static HomeduinoMessage getInterpretation(HomeduinoEventMessage rfEvent, PacketType packetType)
            throws HomeduinoException {
        for (HomeduinoMessage msg : rfEvent.getInterpretations()) {
            if (msg.getPacketType() == packetType) {
                return msg;
            }
        }
        throw new IllegalStateException("Expected message not found");
    }
}
