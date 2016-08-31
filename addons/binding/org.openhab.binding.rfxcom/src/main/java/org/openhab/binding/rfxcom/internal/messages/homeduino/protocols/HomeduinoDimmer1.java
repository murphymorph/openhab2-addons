package org.openhab.binding.rfxcom.internal.messages.homeduino.protocols;

import java.util.List;

import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.Type;
import org.openhab.binding.rfxcom.RFXComValueSelector;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.messages.PacketType;

public class HomeduinoDimmer1 extends HomeduinoCoCo2 {
    private static int PULSE_COUNT = 148;

    public HomeduinoDimmer1() {
        super(PULSE_COUNT);
    }
}
