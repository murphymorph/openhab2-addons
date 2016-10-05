package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoProtocol;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoSwitch4;

public class RFXComHomeduinoSwitch4Message extends RFXComHomeduinoMessage implements RFXComMessage {

    public RFXComHomeduinoSwitch4Message() {
        super(new HomeduinoSwitch4());
    }
}
