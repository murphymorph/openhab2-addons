/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.messages.homeduino;

import static org.openhab.binding.homeduino.HomeduinoValueSelector.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openhab.binding.homeduino.HomeduinoValueSelector;
import org.openhab.binding.homeduino.internal.exceptions.HomeduinoException;
import org.openhab.binding.homeduino.internal.messages.HomeduinoMessage;
import org.openhab.binding.homeduino.internal.messages.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Homeduino message class for contact4 message
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Contact4Message extends AbstractHomeduinoMessage implements HomeduinoMessage {
    public Contact4Message() {
        // deliberately empty
    }

    Contact4Message(Result result) {
        super(result);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.CONTACT4;
    }

    @Override
    public List<HomeduinoValueSelector> getSupportedInputValueSelectors() {
        return Arrays.asList(CONTACT, LOW_BATTERY);
    }

    @Override
    public String getDeviceId() throws HomeduinoException {
        return Integer.toString(getResult().getId());
    }

    @Override
    public List<HomeduinoValueSelector> getSupportedOutputValueSelectors() {
        return Collections.emptyList();
    }

    @Override
    HomeduinoProtocol getProtocol() {
        throw new IllegalArgumentException("Cannot send Contact4 via openHAB");
    }

    public static final class Protocol extends HomeduinoProtocol {
        private static final String POSTFIX = "02";

        private static final int[] PULSE_LENGTHS = { 468, 1364, 14096 };
        private static final int PULSE_COUNT = 50;

        private static Map<String, Character> PULSES_TO_BINARY_MAPPING = initializePulseBinaryMapping();

        public Protocol() {
            super(PULSE_COUNT, PULSE_LENGTHS);
        }

        @Override
        public HomeduinoMessage constructMessage(Result result) {
            return new Contact4Message(result);
        }

        private static Map<String, Character> initializePulseBinaryMapping() {
            Map<String, Character> map = new HashMap<>();
            map.put("10", '1');
            map.put("01", '0');
            return map;
        }

        @Override
        public Result process(String pulses) {
            pulses = pulses.replace(POSTFIX, "");
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < pulses.length(); i += 2) {
                String pulse = pulses.substring(i, i + 2);
                output.append(map(PULSES_TO_BINARY_MAPPING, pulse));
            }

            int id = Integer.parseInt(output.substring(0, 20), 2);
            boolean lowBattery = (Integer.parseInt(output.substring(20, 21), 2) != 1);
            int state = 1 - Integer.parseInt(output.substring(21, 22), 2);

            return new Result.Builder(this, id).withState(state).withLowBattery(lowBattery).build();
        }

        @Override
        public String decode(Command command, int transmitterPin, int repeats) {
            throw new IllegalArgumentException("Cannot send Contact4 via openHAB");
        }
    }
}
