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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openhab.binding.homeduino.HomeduinoValueSelector;
import org.openhab.binding.homeduino.internal.messages.HomeduinoMessage;
import org.openhab.binding.homeduino.internal.messages.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Homeduino message class for switch2 message
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Switch2Message extends AbstractHomeduinoMessage implements HomeduinoMessage {
    public Switch2Message() {
        // deliberately empty
    }

    Switch2Message(Result result) {
        super(result);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.SWITCH2;
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
        private static final String POSTFIX = "02";

        static final int[] PULSE_LENGTHS = { 306, 957, 9808 };
        static final int PULSE_COUNT = 50;

        private static Map<String, Character> PULSES_TO_BINARY_MAPPING = initializePulseBinaryMapping();
        private static Map<Character, String> BINARY_TO_PULSE_MAPPING = inverse(PULSES_TO_BINARY_MAPPING);

        public Protocol() {
            super(PULSE_COUNT, PULSE_LENGTHS);
        }

        private static Map<String, Character> initializePulseBinaryMapping() {
            Map<String, Character> map = new HashMap<>();
            map.put("0110", '0');
            map.put("0101", '1');
            return map;
        }

        @Override
        public HomeduinoMessage constructMessage(Result result) {
            return new Switch2Message(result);
        }

        @Override
        public Result process(String pulses) {
            pulses = pulses.replace(POSTFIX, "");
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < pulses.length(); i += 4) {
                String pulse = pulses.substring(i, i + 4);
                output.append(map(PULSES_TO_BINARY_MAPPING, pulse));
            }

            int houseCode = Integer.parseInt(output.substring(0, 5), 2);
            int unitCode = Integer.parseInt(output.substring(5, 10), 2);
            int state = 1 - Integer.parseInt(output.substring(11, 12), 2);

            return new Result.Builder(this, houseCode, unitCode).withState(state).build();
        }

        @Override
        public String decode(Command command, int transmitterPin, int repeats) {
            StringBuilder binary = getMessageStart(transmitterPin, repeats, PULSE_LENGTHS);

            convert(binary, printBinaryWithWidth(command.getSensorId(), 5), BINARY_TO_PULSE_MAPPING);
            convert(binary, printBinaryWithWidth(command.getUnitCodeAsInt(), 5), BINARY_TO_PULSE_MAPPING);
            convert(binary, commandToBinaryState(command.getCommand()), BINARY_TO_PULSE_MAPPING);
            convert(binary, inverse(commandToBinaryState(command.getCommand())), BINARY_TO_PULSE_MAPPING);

            return binary.append(POSTFIX).toString();
        }

    }
}
