/**
 * Copyright (c) 2010-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages.homeduino;

import static org.openhab.binding.rfxcom.RFXComValueSelector.*;

import java.util.Arrays;
import java.util.List;

import org.openhab.binding.rfxcom.RFXComValueSelector;
import org.openhab.binding.rfxcom.internal.messages.PacketType;
import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;

public class RFXComHomeduinoSwitch4Message extends RFXComHomeduinoMessage implements RFXComMessage {
    public RFXComHomeduinoSwitch4Message(HomeduinoProtocol.Result result) {
        super(result);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.HOMEDUINO_SWITCH4;
    }

    @Override
    public List<RFXComValueSelector> getSupportedInputValueSelectors() {
        return Arrays.asList(COMMAND, CONTACT);
    }

    @Override
    public List<RFXComValueSelector> getSupportedOutputValueSelectors() {
        return Arrays.asList(COMMAND);
    }

    @Override
    HomeduinoProtocol getProtocol() {
        return new Protocol();
    }

    public static final class Protocol extends HomeduinoCoCo1 {

    }
}
