/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.messages.homeduino;

import static java.util.Collections.singletonList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.core.library.types.StopMoveType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.eclipse.smarthome.core.types.Type;
import org.openhab.binding.homeduino.HomeduinoValueSelector;
import org.openhab.binding.homeduino.internal.messages.HomeduinoMessage;
import org.openhab.binding.homeduino.internal.messages.PacketType;

/**
 * Homeduino message class for shutter3 message
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Shutter3Message extends AbstractHomeduinoMessage implements HomeduinoMessage {
    public Shutter3Message() {
        // deliberately empty
    }

    Shutter3Message(Result result) {
        super(result);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.SHUTTER3;
    }

    @Override
    public List<HomeduinoValueSelector> getSupportedInputValueSelectors() {
        return singletonList(HomeduinoValueSelector.SHUTTER);
    }

    @Override
    public List<HomeduinoValueSelector> getSupportedOutputValueSelectors() {
        return singletonList(HomeduinoValueSelector.SHUTTER);
    }

    @Override
    HomeduinoProtocol getProtocol() {
        return new Protocol();
    }

    public static final class Protocol extends HomeduinoProtocol {
        private static final String PREFIX = "32";
        private static final String POSTFIX = "14";
        private static final String POSTFIX_PROGRAM = "04";

        private static final int PULSE_COUNT = 82;
        private static final int[] PULSE_LENGTHS = { 366, 736, 1600, 5204, 10896 };
        private static Map<String, Character> PULSES_TO_BINARY_MAPPING = initializePulseBinaryMapping();
        private static Map<Character, String> BINARY_TO_PULSE_MAPPING = inverse(PULSES_TO_BINARY_MAPPING);

        public Protocol() {
            super(PULSE_COUNT, PULSE_LENGTHS);
        }

        private static Map<String, Character> initializePulseBinaryMapping() {
            Map<String, Character> map = new HashMap<>();
            map.put("01", '0');
            map.put("10", '1');
            return map;
        }

        @Override
        public HomeduinoMessage constructMessage(Result result) {
            return new Shutter3Message(result);
        }

        @Override
        public Result process(String pulses) {
            pulses = pulses.replace(PREFIX, "").replace(POSTFIX, "").replace(POSTFIX_PROGRAM, "");
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < pulses.length(); i += 2) {
                String pulse = pulses.substring(i, i + 2);
                output.append(map(PULSES_TO_BINARY_MAPPING, pulse));
            }

            int id = Integer.parseInt(output.substring(0, 29), 2);
            int channel = Integer.parseInt(output.substring(29, 32), 2);
            int state = Integer.parseInt(output.substring(33, 36), 2);

            return new Result.Builder(this, id, channel).withState(state).build();
        }

        @Override
        public String decode(Command command, int transmitterPi, int repeats) {
            // first convert it to a binary string
            StringBuilder binary = getMessageStart(transmitterPi, repeats, PULSE_LENGTHS).append(PREFIX);
            convert(binary, printBinaryWithWidth(command.getSensorId(), 29), BINARY_TO_PULSE_MAPPING);
            convert(binary, printBinaryWithWidth(command.getUnitCodeAsInt(), 3), BINARY_TO_PULSE_MAPPING);
            binary.append("01");
            convert(binary, shutterCommandToBinaryState(command.getCommand()), BINARY_TO_PULSE_MAPPING);

            return binary.append(POSTFIX).toString();
        }

        private String shutterCommandToBinaryState(Type command) {
            if (command instanceof UpDownType) {
                switch ((UpDownType) command) {
                    case UP:
                        return "001000";
                    case DOWN:
                        return "011001";
                }
            } else if (command instanceof StopMoveType) {
                switch ((StopMoveType) command) {
                    case STOP:
                        return "101010";
                }
            }
            throw new IllegalArgumentException("Cannot convert command " + command + " to valid response");
        }

    }
}
