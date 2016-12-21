/**
 * Copyright (c) 2010-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages.homeduino;

import java.util.Arrays;
import java.util.List;

import org.openhab.binding.rfxcom.RFXComValueSelector;
import org.openhab.binding.rfxcom.internal.messages.PacketType;
import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;

public class RFXComHomeduinoDimmer1Message extends RFXComHomeduinoMessage implements RFXComMessage {
    public RFXComHomeduinoDimmer1Message(HomeduinoProtocol.Result result) {
        super(result);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.HOMEDUINO_DIMMER1;
    }

    @Override
    public List<RFXComValueSelector> getSupportedInputValueSelectors() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public List<RFXComValueSelector> getSupportedOutputValueSelectors() {
        return Arrays.asList(RFXComValueSelector.COMMAND);
    }

    @Override
    HomeduinoProtocol getProtocol() {
        return new Protocol();
    }

    public static class Protocol extends HomeduinoCoCo2 {
        private static final int PULSE_COUNT = 148;

        public Protocol() {
            super(PULSE_COUNT);
        }
    }
}
