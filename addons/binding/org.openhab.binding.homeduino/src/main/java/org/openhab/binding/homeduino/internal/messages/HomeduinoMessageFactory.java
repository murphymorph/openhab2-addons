/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.messages;

import java.lang.reflect.Constructor;

import org.openhab.binding.homeduino.internal.exceptions.HomeduinoException;
import org.openhab.binding.homeduino.internal.messages.homeduino.AbstractHomeduinoMessage;

/**
 * Factory to map message types to message classes for outgoing messages
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class HomeduinoMessageFactory {
    public static AbstractHomeduinoMessage createMessage(PacketType packetType)
            throws HomeduinoException {

        try {
            Class<? extends AbstractHomeduinoMessage> clazz = packetType.getMessageClass();
            Constructor<? extends AbstractHomeduinoMessage> c = clazz.getConstructor();
            return c.newInstance();
        } catch (Exception e) {
            throw new HomeduinoException(e);
        }
    }

    public static PacketType convertPacketType(String packetType) throws IllegalArgumentException {
        for (PacketType p : PacketType.values()) {
            if (p.toString().replace("_", "").equals(packetType.replace("_", ""))) {
                return p;
            }
        }

        throw new IllegalArgumentException("Unknown packet type " + packetType);
    }

}
