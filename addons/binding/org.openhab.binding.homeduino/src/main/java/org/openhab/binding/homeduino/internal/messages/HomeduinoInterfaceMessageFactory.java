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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory to map incoming messages to message classes
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class HomeduinoInterfaceMessageFactory {
    private static final Map<String, Class<? extends HomeduinoInterfaceMessage>> MAP = initialize();

    private static Map<String, Class<? extends HomeduinoInterfaceMessage>> initialize() {
        Map<String, Class<? extends HomeduinoInterfaceMessage>> result = new HashMap<>();
        result.put("rea", HomeduinoReadyMessage.class);
        result.put("ACK", HomeduinoAcknowledgementMessage.class);
        result.put("ERR", HomeduinoErrorMessage.class);
        result.put("RF ", HomeduinoEventMessage.class);
        return result;
    }

    public static HomeduinoInterfaceMessage createMessage(byte[] packet) throws HomeduinoException {

        try {
            Class<? extends HomeduinoInterfaceMessage> clazz = getResponseType(Arrays.copyOfRange(packet, 0, 3));

            if (clazz == HomeduinoEventMessage.class) {
                return new HomeduinoEventMessage(packet);
            } else {
                return clazz.newInstance();
            }
        } catch (Exception e) {
            throw new HomeduinoException(e);
        }
    }

    private static Class<? extends HomeduinoInterfaceMessage> getResponseType(byte[] copyOfRange) {
        Class<? extends HomeduinoInterfaceMessage> result = MAP.get(new String(copyOfRange));
        if (result == null) {
            throw new IllegalStateException("Invalid response received");
        }
        return result;
    }
}
