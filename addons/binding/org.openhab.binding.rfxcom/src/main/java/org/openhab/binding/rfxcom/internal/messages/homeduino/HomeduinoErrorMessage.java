package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.messages.PacketType;

public class HomeduinoErrorMessage extends HomeduinoResponseMessage {

    public HomeduinoErrorMessage() {
        super(PacketType.HOMEDUINO_ERROR);
    }
}
