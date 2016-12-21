/**
 * Copyright (c) 2010-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */package org.openhab.binding.rfxcom.internal.messages.homeduino;

import static org.openhab.binding.rfxcom.RFXComValueSelector.CONTACT;

import java.util.Arrays;
import java.util.List;

import org.openhab.binding.rfxcom.RFXComValueSelector;
import org.openhab.binding.rfxcom.internal.messages.PacketType;
import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;

public class RFXComHomeduinoPir1Message extends RFXComHomeduinoMessage implements RFXComMessage {
    public RFXComHomeduinoPir1Message(HomeduinoProtocol.Result result) {
        super(result);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.HOMEDUINO_PIR1;
    }

    @Override
    public List<RFXComValueSelector> getSupportedInputValueSelectors() {
        return Arrays.asList(CONTACT);
    }

    @Override
    public List<RFXComValueSelector> getSupportedOutputValueSelectors() {
        return Arrays.asList(RFXComValueSelector.COMMAND);
    }

    @Override
    HomeduinoProtocol getProtocol() {
        return new Protocol();
    }

    public static class Protocol extends HomeduinoCoCo1 {

    }
}
