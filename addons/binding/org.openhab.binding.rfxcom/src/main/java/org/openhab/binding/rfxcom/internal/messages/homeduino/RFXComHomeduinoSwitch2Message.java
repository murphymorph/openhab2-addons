package org.openhab.binding.rfxcom.internal.messages.homeduino;

import static org.openhab.binding.rfxcom.RFXComValueSelector.*;

import java.util.Arrays;
import java.util.List;

import org.openhab.binding.rfxcom.RFXComValueSelector;
import org.openhab.binding.rfxcom.internal.messages.PacketType;
import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RFXComHomeduinoSwitch2Message extends RFXComHomeduinoMessage implements RFXComMessage {
    private static final Logger LOGGER = LoggerFactory.getLogger(RFXComHomeduinoSwitch2Message.class);
    private static final int[] PULSE_LENGTHS = { 306, 957, 9808 };
    private static final int PULSE_COUNT = 50;

    public RFXComHomeduinoSwitch2Message(HomeduinoProtocol.Result result) {
        super(result);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.HOMEDUINO_SWITCH2;
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

    public static final class Protocol extends HomeduinoProtocol {
        public Protocol() {
            super(PULSE_COUNT, PULSE_LENGTHS);
        }

        @Override
        public Result process(String pulses) {
            pulses = pulses.replace("02", "");
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < pulses.length(); i += 4) {
                String pulse = pulses.substring(i, i + 4);
                char c;
                if ("0101".equals(pulse)) {
                    c = '1';
                } else {
                    c = '0';
                }

                output.append(c);
            }

            int unit = Integer.parseInt(output.substring(0, 4).toString(), 2);
            int id = Integer.parseInt(output.substring(5, 9).toString(), 2);
            int state = 1 - Integer.parseInt(output.substring(11).toString(), 2);

            LOGGER.warn("state: " + state); // could also indicate presence
            LOGGER.warn("Id: " + id);
            LOGGER.warn("Unitcode: " + unit);

            return new Result(id, unit, state, false, null);
        }
    }
}
