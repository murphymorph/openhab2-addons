package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.messages.PacketType;

public class HomeduinoReadyMessage extends HomeduinoBaseMessage {
    @Override
    public PacketType getPacketType() throws RFXComException {
        return null; // return add type?
    }
}
