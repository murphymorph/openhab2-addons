package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.messages.PacketType;

public class HomeduinoAcknowledgementMessage extends HomeduinoResponseMessage {

    public HomeduinoAcknowledgementMessage() {
        super(PacketType.HOMEDUINO_ACK);
    }
}
