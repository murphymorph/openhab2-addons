package org.openhab.binding.rfxcom.internal.messages.homeduino;

import java.util.List;

import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.Type;
import org.openhab.binding.rfxcom.RFXComValueSelector;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.messages.PacketType;
import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoProtocol;

public class RFXComHomeduinoCommand implements RFXComMessage {
    private HomeduinoProtocol protocol;

    public RFXComHomeduinoCommand(HomeduinoProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void encodeMessage(byte[] data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] decodeMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public State convertToState(RFXComValueSelector valueSelector) throws RFXComException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void convertFromState(RFXComValueSelector valueSelector, Type type) throws RFXComException {
        // TODO Auto-generated method stub

    }

    @Override
    public Object convertSubType(String subType) throws RFXComException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object convertSubType() throws RFXComException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSubType(Object subType) throws RFXComException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getDeviceId() throws RFXComException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDeviceId(String deviceId) throws RFXComException {
        // TODO Auto-generated method stub

    }

    @Override
    public PacketType getPacketType() throws RFXComException {
        return protocol.getPacketType();
    }

    @Override
    public List<RFXComValueSelector> getSupportedInputValueSelectors() throws RFXComException {
        // TODO should the command and the message both implement both
        return protocol.getSupportedInputValueSelectors();
    }

    @Override
    public List<RFXComValueSelector> getSupportedOutputValueSelectors() throws RFXComException {
        // TODO should the command and the message both implement both
        return protocol.getSupportedOutputValueSelectors();
    }

}
