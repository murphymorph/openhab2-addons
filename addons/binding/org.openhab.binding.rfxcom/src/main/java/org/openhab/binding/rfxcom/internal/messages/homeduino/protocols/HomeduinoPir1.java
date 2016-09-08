package org.openhab.binding.rfxcom.internal.messages.homeduino.protocols;

import static org.openhab.binding.rfxcom.RFXComValueSelector.CONTACT;

import java.util.Arrays;
import java.util.List;

import org.openhab.binding.rfxcom.RFXComValueSelector;
import org.openhab.binding.rfxcom.internal.messages.PacketType;

public class HomeduinoPir1 extends HomeduinoCoCo1 {

    @Override
    public PacketType getPacketType() {
        return PacketType.HOMEDUINO_PIR1;
    }

    @Override
    public List<RFXComValueSelector> getSupportedInputValueSelectors() {
        return Arrays.asList(CONTACT);
    }

}
