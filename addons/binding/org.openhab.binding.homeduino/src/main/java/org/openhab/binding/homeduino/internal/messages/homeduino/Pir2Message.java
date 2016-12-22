/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.messages.homeduino;

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
 * Homeduino message class for pir2 message
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Pir2Message extends AbstractHomeduinoMessage implements HomeduinoMessage {
    public Pir2Message() {
        // deliberately empty
    }

    Pir2Message(Result result) {
        super(result);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.PIR2;
    }

    @Override
    public List<HomeduinoValueSelector> getSupportedInputValueSelectors() {
        return Collections.singletonList(HomeduinoValueSelector.CONTACT);
    }

    @Override
    public List<HomeduinoValueSelector> getSupportedOutputValueSelectors() {
        return Collections.emptyList();
    }

    @Override
    HomeduinoProtocol getProtocol() {
        throw new IllegalArgumentException("Cannot send PIR2 via openHAB");
    }

    public static final class Protocol extends HomeduinoProtocol {
        private static final String POSTFIX = "02";

        private static final int[] PULSE_LENGTHS = { 451, 1402, 14356 };
        private static final int PULSE_COUNT = 50;

        private static Map<String, Character> PULSES_TO_BINARY_MAPPING = initializePulseBinaryMapping();

        public Protocol() {
            super(PULSE_COUNT, PULSE_LENGTHS);
        }

        @Override
        public HomeduinoMessage constructMessage(Result result) {
            return new Pir2Message(result);
        }

        private static Map<String, Character> initializePulseBinaryMapping() {
            Map<String, Character> map = new HashMap<>();
            map.put("10", '0');
            map.put("01", '1');
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

            int unit = Integer.parseInt(output.substring(0, 5), 2);
            int id = Integer.parseInt(output.substring(5, 10), 2);
            int state = 1;

            return new Result.Builder(this, id, unit).withState(state).build();
        }

        @Override
        public String decode(Command command, int transmitterPin, int repeats) {
            throw new IllegalArgumentException("Cannot send PIR2 via openHAB");
        }
    }
}
