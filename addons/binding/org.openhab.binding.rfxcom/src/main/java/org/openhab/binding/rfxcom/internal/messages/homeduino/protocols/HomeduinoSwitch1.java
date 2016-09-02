package org.openhab.binding.rfxcom.internal.messages.homeduino.protocols;

import org.openhab.binding.rfxcom.internal.messages.PacketType;

public final class HomeduinoSwitch1 extends HomeduinoCoCo2 {
    private static int PULSE_COUNT = 132;

    public HomeduinoSwitch1() {
        super(PULSE_COUNT);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.HOMEDUINO_SWITCH1;
    }
}
