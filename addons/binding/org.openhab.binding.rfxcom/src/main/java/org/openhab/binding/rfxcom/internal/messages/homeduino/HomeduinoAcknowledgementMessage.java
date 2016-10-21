package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.messages.PacketType;

import static org.openhab.binding.rfxcom.internal.messages.PacketType.HOMEDUINO_ACK;

public class HomeduinoAcknowledgementMessage extends HomeduinoResponseMessage {

    @Override
    public PacketType getPacketType() throws RFXComException {
        return HOMEDUINO_ACK;
    }
}
