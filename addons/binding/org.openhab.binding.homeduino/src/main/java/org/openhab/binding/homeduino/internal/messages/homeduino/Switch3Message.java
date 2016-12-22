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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Homeduino message class for switch3 message
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Switch3Message extends AbstractHomeduinoMessage implements HomeduinoMessage {
    public Switch3Message() {
        // deliberately empty
    }

    Switch3Message(Result result) {
        super(result);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.SWITCH3;
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

    public static final class Protocol extends HomeduinoProtocol {
        Switch2Message.Protocol switch2Protocol = new Switch2Message.Protocol();

        public Protocol() {
            super(Switch2Message.Protocol.PULSE_COUNT, Switch2Message.Protocol.PULSE_LENGTHS);
        }

        @Override
        public HomeduinoMessage constructMessage(Result result) {
            return new Switch3Message(result);
        }

        @Override
        public Result process(String pulses) {
            Result result = switch2Protocol.process(pulses);

            return new Result.Builder(this, result.getId(), result.getUnit()).withState(1 - result.getState()).build();
        }

        @Override
        public String decode(Command command, int transmitterPin, int repeats) {
            return switch2Protocol.decode(Command.inverse(command), transmitterPin, repeats);
        }
    }
}
