/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.messages;

import org.openhab.binding.homeduino.internal.messages.homeduino.AbstractHomeduinoMessage;
import org.openhab.binding.homeduino.internal.messages.homeduino.Contact4Message;
import org.openhab.binding.homeduino.internal.messages.homeduino.Dimmer1Message;
import org.openhab.binding.homeduino.internal.messages.homeduino.Pir1Message;
import org.openhab.binding.homeduino.internal.messages.homeduino.Pir2Message;
import org.openhab.binding.homeduino.internal.messages.homeduino.Shutter3Message;
import org.openhab.binding.homeduino.internal.messages.homeduino.Switch1Message;
import org.openhab.binding.homeduino.internal.messages.homeduino.Switch2Message;
import org.openhab.binding.homeduino.internal.messages.homeduino.Switch3Message;
import org.openhab.binding.homeduino.internal.messages.homeduino.Switch4Message;
import org.openhab.binding.homeduino.internal.messages.homeduino.Weather1Message;

/**
 * Supported packet types for Homeduino
 *
 * @author Martin van Wingerden - Initial contribution
 */
public enum PacketType {
    CONTACT4(Contact4Message.class),
    SWITCH1(Switch1Message.class),
    SWITCH2(Switch2Message.class),
    SWITCH3(Switch3Message.class),
    SWITCH4(Switch4Message.class),
    DIMMER1(Dimmer1Message.class),
    PIR1(Pir1Message.class),
    PIR2(Pir2Message.class),
    SHUTTER3(Shutter3Message.class),
    WEATHER1(Weather1Message.class);

    private final Class<? extends AbstractHomeduinoMessage> messageClazz;

    PacketType(Class<? extends AbstractHomeduinoMessage> clazz) {
        this.messageClazz = clazz;
    }

    public Class<? extends AbstractHomeduinoMessage> getMessageClass() {
        return messageClazz;
    }

    public String thingTypeId() {
        return name().toLowerCase();
    }
}