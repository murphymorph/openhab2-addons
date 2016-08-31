package org.openhab.binding.rfxcom.internal.messages.homeduino.protocols;

import java.util.List;

import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.Type;
import org.openhab.binding.rfxcom.RFXComValueSelector;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.messages.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeduinoSwitch1 extends HomeduinoCoCo2 {
    private Logger logger = LoggerFactory.getLogger(HomeduinoSwitch1.class);

    private static int PULSE_COUNT = 132;

    public HomeduinoSwitch1() {
        super(PULSE_COUNT);
    }
}
