package org.openhab.binding.rfxcom.internal.messages;

import static java.math.BigDecimal.ROUND_CEILING;
import static org.openhab.binding.rfxcom.RFXComValueSelector.SIGNAL_LEVEL;
import static org.openhab.binding.rfxcom.RFXComValueSelector.TEMPERATURE;
import static org.openhab.binding.rfxcom.RFXComValueSelector.VOLTAGE;

import org.eclipse.smarthome.core.library.items.NumberItem;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.Type;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.rfxcom.RFXComValueSelector;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComUnsupportedValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RFXComRFXSensorMessage extends RFXComBaseMessage {
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final Logger logger = LoggerFactory.getLogger(RFXComRFXSensorMessage.class);

    public enum SubType {
        TEMPERATURE(0, RFXComValueSelector.TEMPERATURE),
        A_D(1),
        VOLTAGE(2, RFXComValueSelector.VOLTAGE),
        MESSAGE(3);

        private final int subType;
        private final RFXComValueSelector[] supportedInputValueSelectors;

        SubType(int subType, RFXComValueSelector... supportedInputValueSelectors) {
            this.subType = subType;
            this.supportedInputValueSelectors = supportedInputValueSelectors;
        }

        public byte toByte() {
            return (byte) subType;
        }

        public static SubType fromByte(int input) throws RFXComUnsupportedValueException {
            for (SubType c : SubType.values()) {
                if (c.subType == input) {
                    return c;
                }
            }

            throw new RFXComUnsupportedValueException(SubType.class, input);
        }

        public List<RFXComValueSelector> getSupportedInputValueSelectors() {
            List<RFXComValueSelector> selectors = Arrays.asList(supportedInputValueSelectors);
            selectors.add(SIGNAL_LEVEL);
            return selectors;
        }
    }

    private final static List<RFXComValueSelector> SUPPORTED_OUTPUT_VALUE_SELECTORS = Collections.emptyList();

    public SubType subType;

    public int sensorId;
    private Double temperature;
    private BigDecimal miliVoltageTimesTen;
    public byte signalLevel;

    public RFXComRFXSensorMessage() {
        packetType = PacketType.RFXSENSOR;
    }

    public RFXComRFXSensorMessage(byte[] data) throws RFXComException {
        encodeMessage(data);
    }

    @Override
    public String toString() {
        String str = super.toString();

        str += ", Sub type = " + subType;
        str += ", Device Id = " + getDeviceId();
        str += ", Temperature = " + temperature;
        str += ", Voltage = " + getVoltage();
        str += ", Signal level = " + signalLevel;

        return str;
    }

    @Override
    public void encodeMessage(byte[] data) throws RFXComException {
        super.encodeMessage(data);

        subType = SubType.fromByte(super.subTypeAsByte);

        sensorId = (data[4] & 0xFF);

        byte msg1 = data[5];
        byte msg2 = data[6];

        if (subType == SubType.TEMPERATURE){
            encodeTemperatureMessage(msg1, msg2);
        } else if (subType == SubType.A_D) {
            encodeVoltageMessage(msg1, msg2);
        } else if (subType == SubType.VOLTAGE) {
            encodeVoltageMessage(msg1, msg2);
        } else if (subType == SubType.MESSAGE) {
            encodeStatusMessage(msg2);
        }

        signalLevel = (byte) ((data[7] & 0xF0) >> 4);
    }

    private void encodeTemperatureMessage(byte msg1, byte msg2) {
        temperature = (short) ((msg1 & 0x7F) << 8 | (msg2 & 0xFF)) * 0.01;
        if ((msg1 & 0x80) != 0) {
            temperature = -temperature;
        }
    }

    private void encodeVoltageMessage(byte msg1, byte msg2) {
        miliVoltageTimesTen = BigDecimal.valueOf((short) ((msg1 & 0xFF) << 8 | (msg2 & 0xFF)));
    }

    private void encodeStatusMessage(byte msg2) {


        //logger
    }

    @Override
    public byte[] decodeMessage() {
        byte[] data = new byte[8];

        data[0] = 0x07;
        data[1] = PacketType.RFXSENSOR.toByte();
        data[2] = subType.toByte();
        data[3] = seqNbr;
        data[4] = (byte) (sensorId & 0x00FF);

        if (subType == SubType.TEMPERATURE){
            decodeTemperatureMessage(data);
        } else if (subType == SubType.A_D) {
            decodeVoltageMessage(data);
        } else if (subType == SubType.VOLTAGE) {
            decodeVoltageMessage(data);
        } else if (subType == SubType.MESSAGE) {
            decodeStatusMessage(data);
        }

        data[7] = (byte) ((signalLevel & 0x0F) << 4);

        return data;
    }

    private void decodeTemperatureMessage(byte[] data) {
        short temp = (short) Math.abs(temperature * 100);
        data[5] = (byte) ((temp >> 8) & 0xFF);
        data[6] = (byte) (temp & 0xFF);
        if (temperature < 0) {
            data[5] |= 0x80;
        }
    }

    private void decodeVoltageMessage(byte[] data) {
        short miliVoltageTimesTenShort = this.miliVoltageTimesTen.shortValueExact();
        data[5] = (byte) ((miliVoltageTimesTenShort >> 8) & 0xFF);
        data[6] = (byte) (miliVoltageTimesTenShort & 0xFF);

    }

    private void decodeStatusMessage(byte[] data) {

    }

    @Override
    public String getDeviceId() {
        return String.valueOf(sensorId);
    }

    @Override
    public State convertToState(RFXComValueSelector valueSelector) throws RFXComException {
        State state;

        if (valueSelector.getItemClass() == NumberItem.class) {
            if (valueSelector == SIGNAL_LEVEL) {
                state = new DecimalType(signalLevel);
            } else if (valueSelector == TEMPERATURE) {
                state = temperature == null ? UnDefType.UNDEF : new DecimalType(temperature);
            } else if (valueSelector == VOLTAGE) {
                if (miliVoltageTimesTen != null && subType == SubType.VOLTAGE) {
                    state = new DecimalType(getVoltage());
                } else {
                    state = UnDefType.UNDEF;
                }
            } else {
                throw new RFXComException("Can't convert " + valueSelector + " to NumberItem");
            }
        } else {
            throw new RFXComException("Can't convert " + valueSelector + " to " + valueSelector.getItemClass());
        }

        return state;
    }

    private BigDecimal getVoltage() {
        if (miliVoltageTimesTen == null) {
            return null;
        }
        return miliVoltageTimesTen.divide(ONE_HUNDRED, 100, ROUND_CEILING);
    }

    @Override
    public void setSubType(Object subType) throws RFXComException {
        throw new RFXComException("Not supported");
    }

    @Override
    public void setDeviceId(String deviceId) throws RFXComException {
        throw new RFXComException("Not supported");
    }

    @Override
    public void convertFromState(RFXComValueSelector valueSelector, Type type) throws RFXComException {
        throw new RFXComException("Not supported");
    }

    @Override
    public Object convertSubType(String subType) throws RFXComException {
        for (SubType s : SubType.values()) {
            if (s.toString().equals(subType)) {
                return s;
            }
        }

        try {
            return SubType.fromByte(Integer.parseInt(subType));
        } catch (NumberFormatException e) {
            throw new RFXComUnsupportedValueException(SubType.class, subType);
        }
    }

    @Override
    public List<RFXComValueSelector> getSupportedInputValueSelectors() throws RFXComException {
        return subType.getSupportedInputValueSelectors();
    }

    @Override
    public List<RFXComValueSelector> getSupportedOutputValueSelectors() throws RFXComException {
        return SUPPORTED_OUTPUT_VALUE_SELECTORS;
    }
}
