package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoPir1;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoProtocol;

public class RFXComHomeduinoPir1Message extends RFXComHomeduinoMessage implements RFXComMessage {
    public RFXComHomeduinoPir1Message() {
        super(new HomeduinoPir1());
    }
}
