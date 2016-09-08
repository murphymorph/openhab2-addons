package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoProtocol;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoSwitch1;

public class RFXComHomeduinoSwitch1Message extends RFXComHomeduinoMessage implements RFXComMessage {
    public RFXComHomeduinoSwitch1Message() {
        super(new HomeduinoSwitch1());
    }


}
