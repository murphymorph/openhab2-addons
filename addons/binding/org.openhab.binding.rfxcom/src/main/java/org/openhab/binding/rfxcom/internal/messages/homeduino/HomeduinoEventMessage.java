/**
 * Copyright (c) 2010-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages.homeduino;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComNotImpException;
import org.openhab.binding.rfxcom.internal.messages.PacketType;
import org.openhab.binding.rfxcom.internal.messages.RFXComLighting2Message;
import org.openhab.binding.rfxcom.internal.messages.RFXComLighting2Message.Commands;
import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.HomeduinoProtocol.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeduinoEventMessage extends HomeduinoBaseMessage {
    private final byte[] data;

    private Logger logger = LoggerFactory.getLogger(HomeduinoEventMessage.class);

    private final static List<HomeduinoProtocol> SUPPORTED_PROTOCOLS = initializeProtocols();

    private static List<HomeduinoProtocol> initializeProtocols() {
        List<HomeduinoProtocol> result = new ArrayList<>();
        result.add(new RFXComHomeduinoSwitch1Message.Protocol());
        result.add(new RFXComHomeduinoSwitch2Message.Protocol());
        result.add(new RFXComHomeduinoSwitch4Message.Protocol());
        result.add(new RFXComHomeduinoDimmer1Message.Protocol());
        result.add(new RFXComHomeduinoPir1Message.Protocol());
        result.add(new RFXComHomeduinoShutter3Message.Protocol());
        return result;
    }

    public HomeduinoEventMessage(byte[] data) {
        this.data = data;
    }

    public static String decodeMessage(RFXComMessage message) {
        if (message instanceof RFXComLighting2Message) {
            RFXComLighting2Message lightingMessage = (RFXComLighting2Message) message;

            // first convert it to a binary string
            String binary = printBinaryWithWidth(lightingMessage.sensorId, 26);
            // all
            Commands command = lightingMessage.command;
            binary += (command == Commands.GROUP_OFF || command == Commands.GROUP_ON) ? "1" : "0";
            binary += commandToBinaryState(command);
            binary += printBinaryWithWidth(lightingMessage.unitCode, 4);
            if (command == Commands.SET_LEVEL) {
                binary += printBinaryWithWidth(lightingMessage.signalLevel, 4);
            }

            // make the output pin variable
            StringBuilder output = new StringBuilder("RF send 4 3 284 2800 1352 10760 0 0 0 0 01");
            for (int i = 0; i < binary.length(); i++) {
                char c = binary.charAt(i);
                String pulse;
                if (c == '1') {
                    pulse = "0200";
                } else if (c == 'N') {
                    pulse = "0000";
                } else {
                    pulse = "0002";
                }

                output.append(pulse);
            }
            output.append("03");
            return output.toString();
        }
        return null;
    }

    private static String commandToBinaryState(Commands command) {
        if (command == Commands.ON || command == Commands.GROUP_ON) {
            return "1";
        } else if (command == Commands.OFF || command == Commands.GROUP_OFF) {
            return "0";
        } else {
            return "N";
        }
    }

    private static String printBinaryWithWidth(int number, int width) {
        return String.format("%" + width + "s", Integer.toBinaryString(number)).replace(' ', '0');
    }

    public String getDeviceId(Result result) throws RFXComException {
        return result.getId() + "." + result.getUnit();
    }

    public List<RFXComMessage> getInterpretations() throws RFXComNotImpException, RFXComException {
        List<RFXComMessage> list = new ArrayList<>();

        // the result is a compressed set of timings (from rfcontrol https://github.com/pimatic/RFControl)
        // the first 8 numbers are buckets which refer to pulse lengths,
        // all the other values refer back to these buckets.

        // the strategy we use here is based on the strategy described for rfcontroljs
        String value = new String(data, StandardCharsets.US_ASCII);
        Pattern p = Pattern.compile(".*? (([0-9]+ ){8})(([0-7][0-7])+)$");
        Matcher m = p.matcher(value);

        if (m.matches()) {
            HomeduinoProtocol.Pulses pulses = HomeduinoProtocol.prepareAndFixCompressedPulses(data);

            for (HomeduinoProtocol protocol : SUPPORTED_PROTOCOLS) {
                if (protocol.matches(pulses)) {
                    list.add(RFXComHomeduinoMessageFactory.createMessage(protocol.process(pulses)));
                }
            }
        } else {
            logger.warn("Panic: could not parse message");
        }

        return list;
    }

    @Override
    public PacketType getPacketType() throws RFXComException {
        return null;
    }
}
