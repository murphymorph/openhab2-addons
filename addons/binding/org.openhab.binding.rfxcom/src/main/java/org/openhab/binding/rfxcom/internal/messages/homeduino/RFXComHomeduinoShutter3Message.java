package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoProtocol;

public class RFXComHomeduinoShutter3Message extends RFXComHomeduinoMessage implements RFXComMessage {
    public RFXComHomeduinoShutter3Message(HomeduinoProtocol.Result result) {
        super(result);
    }
}
