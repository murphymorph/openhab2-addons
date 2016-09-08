package org.openhab.binding.rfxcom.internal.messages.homeduino.protocols;

import static org.openhab.binding.rfxcom.RFXComValueSelector.*;

import java.util.Arrays;
import java.util.List;

import org.openhab.binding.rfxcom.RFXComValueSelector;
import org.openhab.binding.rfxcom.internal.messages.PacketType;

public final class HomeduinoSwitch1 extends HomeduinoCoCo2 {
    private static final int PULSE_COUNT = 132;

    public HomeduinoSwitch1() {
        super(PULSE_COUNT);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.HOMEDUINO_SWITCH1;
    }

    @Override
    public List<RFXComValueSelector> getSupportedInputValueSelectors() {
        return Arrays.asList(COMMAND, CONTACT);
    }
}
