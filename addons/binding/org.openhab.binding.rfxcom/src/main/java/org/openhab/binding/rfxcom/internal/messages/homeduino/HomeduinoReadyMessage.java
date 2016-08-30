package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.messages.PacketType;

public class HomeduinoReadyMessage extends HomeduinoBaseMessage {

    public HomeduinoReadyMessage() {
        super(PacketType.HOMEDUINO_READY);
    }
}