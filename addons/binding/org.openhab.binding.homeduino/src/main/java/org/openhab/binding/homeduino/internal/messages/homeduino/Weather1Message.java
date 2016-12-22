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
import org.openhab.binding.homeduino.internal.messages.HomeduinoMessage;
import org.openhab.binding.homeduino.internal.messages.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Homeduino message class for weather1 message
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Weather1Message extends AbstractHomeduinoMessage implements HomeduinoMessage {
    public Weather1Message() {
        // deliberately empty
    }

    Weather1Message(Result result) {
        super(result);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.WEATHER1;
    }

    @Override
    public List<HomeduinoValueSelector> getSupportedInputValueSelectors() {
        return Arrays.asList(TEMPERATURE, HUMIDITY, LOW_BATTERY);
    }

    @Override
    public List<HomeduinoValueSelector> getSupportedOutputValueSelectors() {
        return Collections.emptyList();
    }

    @Override
    HomeduinoProtocol getProtocol() {
        throw new IllegalArgumentException("Cannot send weather via openHAB");
    }

    public static final class Protocol extends HomeduinoProtocol {
        private static final String POSTFIX = "03";

        private static final int[] PULSE_LENGTHS = { 456, 1990, 3940, 9236 };
        private static final int PULSE_COUNT = 74;

        private static Map<String, Character> PULSES_TO_BINARY_MAPPING = initializePulseBinaryMapping();

        public Protocol() {
            super(PULSE_COUNT, PULSE_LENGTHS);
        }

        private static Map<String, Character> initializePulseBinaryMapping() {
            Map<String, Character> map = new HashMap<>();
            map.put("01", '0');
            map.put("02", '1');
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

            int id = Integer.parseInt(output.substring(4, 12), 2);
            int channel = Integer.parseInt(output.substring(14, 16), 2) + 1;
            double temperature = (double) Integer.parseInt(output.substring(16, 28), 2) / 10;
            int humidity = Integer.parseInt(output.substring(28, 36), 2);
            boolean lowBattery = Character.getNumericValue(output.charAt(12)) == 0;

            return new Result.Builder(this, id, channel).withTemperature(temperature).withHumidity(humidity)
                    .withLowBattery(lowBattery).build();
        }

        @Override
        public HomeduinoMessage constructMessage(Result result) {
            return new Weather1Message(result);
        }

        @Override
        public String decode(Command command, int transmitterPin, int repeats) {
            throw new IllegalArgumentException("Cannot send weather via openHAB");
        }

    }
}
