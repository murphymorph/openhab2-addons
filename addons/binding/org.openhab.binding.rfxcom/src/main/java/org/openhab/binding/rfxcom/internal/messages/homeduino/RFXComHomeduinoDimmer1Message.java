package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoDimmer1;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoProtocol;

public class RFXComHomeduinoDimmer1Message extends RFXComHomeduinoMessage implements RFXComMessage {
    public RFXComHomeduinoDimmer1Message() {
        super(new HomeduinoDimmer1());
    }
}
