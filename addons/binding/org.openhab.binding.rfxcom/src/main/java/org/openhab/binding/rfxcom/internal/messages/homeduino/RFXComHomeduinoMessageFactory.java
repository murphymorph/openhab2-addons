package org.openhab.binding.rfxcom.internal.messages.homeduino;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComNotImpException;
import org.openhab.binding.rfxcom.internal.messages.PacketType;
import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;

class RFXComHomeduinoMessageFactory {
    private static final Map<Class<? extends HomeduinoProtocol>, PacketType> MAP = Collections
            .unmodifiableMap(new HashMap<Class<? extends HomeduinoProtocol>, PacketType>() {
                {
                    put(RFXComHomeduinoDimmer1Message.Protocol.class, PacketType.HOMEDUINO_DIMMER1);
                    put(RFXComHomeduinoPir1Message.Protocol.class, PacketType.HOMEDUINO_PIR1);
                    put(RFXComHomeduinoShutter3Message.Protocol.class, PacketType.HOMEDUINO_SHUTTER3);
                    put(RFXComHomeduinoSwitch1Message.Protocol.class, PacketType.HOMEDUINO_SWITCH1);
                    put(RFXComHomeduinoSwitch2Message.Protocol.class, PacketType.HOMEDUINO_SWITCH2);
                    put(RFXComHomeduinoSwitch4Message.Protocol.class, PacketType.HOMEDUINO_SWITCH4);
                }
            });

    static RFXComMessage createMessage(HomeduinoProtocol.Result result) throws RFXComNotImpException, RFXComException {

        PacketType packetType = getPacketType(result);

        try {
            Class<? extends RFXComMessage> clazz = packetType.getMessageClass();
            try {
                Constructor<? extends RFXComMessage> c = clazz.getConstructor(HomeduinoProtocol.Result.class);
                return c.newInstance(result);
            } catch (NoSuchMethodException e) {
                Constructor<? extends RFXComMessage> c = clazz.getConstructor();
                return c.newInstance();
            }
        } catch (Exception e) {
            throw new RFXComException(e);
        }
    }

    private static PacketType getPacketType(HomeduinoProtocol.Result result) {
        return MAP.get(result.getProtocol().getClass());
    }
}
