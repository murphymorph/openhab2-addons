package org.openhab.binding.rfxcom.internal.messages.homeduino.protocols;

import org.openhab.binding.rfxcom.internal.messages.PacketType;

public class HomeduinoPir1 extends HomeduinoCoCo1 {

    @Override
    public PacketType getPacketType() {
        return PacketType.HOMEDUINO_PIR1;
    }

}
