package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoSwitch2;

public class RFXComHomeduinoSwitch2Message extends RFXComHomeduinoMessage implements RFXComMessage {
    public RFXComHomeduinoSwitch2Message() {
        super(new HomeduinoSwitch2());
    }
}
