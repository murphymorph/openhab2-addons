package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoProtocol;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoShutter3;

public class RFXComHomeduinoShutter3Message extends RFXComHomeduinoMessage implements RFXComMessage {
    public RFXComHomeduinoShutter3Message() {
        super(new HomeduinoShutter3());
    }
}
