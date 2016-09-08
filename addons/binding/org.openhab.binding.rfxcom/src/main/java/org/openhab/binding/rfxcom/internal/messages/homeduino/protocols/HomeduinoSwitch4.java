package org.openhab.binding.rfxcom.internal.messages.homeduino.protocols;

import static org.openhab.binding.rfxcom.RFXComValueSelector.*;

import java.util.Arrays;
import java.util.List;

import org.openhab.binding.rfxcom.RFXComValueSelector;
import org.openhab.binding.rfxcom.internal.messages.PacketType;

public final class HomeduinoSwitch4 extends HomeduinoCoCo1 {
    @Override
    public PacketType getPacketType() {
        return PacketType.HOMEDUINO_SWITCH4;
    }

    @Override
    public List<RFXComValueSelector> getSupportedInputValueSelectors() {
        return Arrays.asList(COMMAND, CONTACT);
    }
}
