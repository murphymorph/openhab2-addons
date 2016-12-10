package org.openhab.binding.rfxcom.internal.messages.homeduino;

import static org.openhab.binding.rfxcom.RFXComValueSelector.*;

import java.util.Arrays;
import java.util.List;

import org.openhab.binding.rfxcom.RFXComValueSelector;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.messages.PacketType;
import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;

// TODO add file / class level docblocks!!!
public class RFXComHomeduinoSwitch1Message extends RFXComHomeduinoMessage implements RFXComMessage {
    public RFXComHomeduinoSwitch1Message() {
        // deliberately empty
    }

    public RFXComHomeduinoSwitch1Message(HomeduinoProtocol.Result result) {
        super(result);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.HOMEDUINO_SWITCH1;
    }

    @Override
    public List<RFXComValueSelector> getSupportedInputValueSelectors() {
        return Arrays.asList(COMMAND, CONTACT);
    }

    @Override
    public List<RFXComValueSelector> getSupportedOutputValueSelectors() {
        return Arrays.asList(RFXComValueSelector.COMMAND);
    }

    @Override
    HomeduinoProtocol getProtocol() {
        return new Protocol();
    }

    @Override
    public void setDeviceId(String deviceId) throws RFXComException {
        super.setDeviceId(deviceId);

        if ("0".equals(command.getUnitCode())) {
            command.setGroup(true);
        }
    }

    public static final class Protocol extends HomeduinoCoCo2 {
        private static final int PULSE_COUNT = 132;

        public Protocol() {
            super(PULSE_COUNT);
        }
    }
}
