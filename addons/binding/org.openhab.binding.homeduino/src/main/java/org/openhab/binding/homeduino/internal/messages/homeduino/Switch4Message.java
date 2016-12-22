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
import org.openhab.binding.homeduino.internal.messages.HomeduinoMessage;
import org.openhab.binding.homeduino.internal.messages.PacketType;

/**
 * Homeduino message class for switch4 message
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Switch4Message extends AbstractHomeduinoMessage implements HomeduinoMessage {
    public Switch4Message() {
        // deliberately empty
    }

    Switch4Message(Result result) {
        super(result);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.SWITCH4;
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

    public static final class Protocol extends HomeduinoCoCo1 {

        @Override
        public HomeduinoMessage constructMessage(Result result) {
            return new Switch4Message(result);
        }
    }
}
