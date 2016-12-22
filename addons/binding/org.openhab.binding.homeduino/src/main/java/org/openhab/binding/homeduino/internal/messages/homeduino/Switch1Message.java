/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.messages.homeduino;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openhab.binding.homeduino.HomeduinoValueSelector;
import org.openhab.binding.homeduino.internal.exceptions.HomeduinoException;
import org.openhab.binding.homeduino.internal.messages.HomeduinoMessage;
import org.openhab.binding.homeduino.internal.messages.PacketType;

/**
 * Homeduino message class for switch1 message
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Switch1Message extends AbstractHomeduinoMessage implements HomeduinoMessage {
    public Switch1Message() {
        // deliberately empty
    }

    Switch1Message(Result result) {
        super(result);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.SWITCH1;
    }

    @Override
    public List<HomeduinoValueSelector> getSupportedInputValueSelectors() {
        return Arrays.asList(HomeduinoValueSelector.COMMAND, HomeduinoValueSelector.CONTACT);
    }

    @Override
    public List<HomeduinoValueSelector> getSupportedOutputValueSelectors() {
        return Collections.singletonList(HomeduinoValueSelector.COMMAND);
    }

    @Override
    HomeduinoProtocol getProtocol() {
        return new Protocol();
    }

    @Override
    public void setDeviceId(String deviceId) throws HomeduinoException {
        super.setDeviceId(deviceId);

        if ("0".equals(getCommand().getUnitCode())) {
            getCommand().setGroup();
        }
    }

    public static final class Protocol extends HomeduinoCoCo2 {
        private static final int PULSE_COUNT = 132;

        public Protocol() {
            super(PULSE_COUNT);
        }

        @Override
        public HomeduinoMessage constructMessage(Result result) {
            return new Switch1Message(result);
        }
    }
}
