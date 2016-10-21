package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoProtocol;

public class RFXComHomeduinoSwitch1Message extends RFXComHomeduinoMessage implements RFXComMessage {
    public RFXComHomeduinoSwitch1Message(HomeduinoProtocol.Result result) {
        super(result);
    }

}
