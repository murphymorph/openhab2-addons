package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.messages.PacketType;

public abstract class HomeduinoResponseMessage extends HomeduinoBaseMessage {
    public HomeduinoResponseMessage(PacketType packetType) {
        super(packetType);
    }
}
