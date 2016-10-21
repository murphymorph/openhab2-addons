package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComNotImpException;
import org.openhab.binding.rfxcom.internal.messages.PacketType;
import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.*;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class RFXComHomeduinoMessageFactory {
    private static final Map<Class<? extends HomeduinoProtocol>, PacketType> MAP = Collections.unmodifiableMap(new HashMap<Class<? extends HomeduinoProtocol>, PacketType>() {
        {
            put(HomeduinoDimmer1.class, PacketType.HOMEDUINO_DIMMER1);
            put(HomeduinoPir1.class, PacketType.HOMEDUINO_PIR1);
            put(HomeduinoShutter3.class, PacketType.HOMEDUINO_SHUTTER3);
            put(HomeduinoSwitch1.class, PacketType.HOMEDUINO_SWITCH1);
            put(HomeduinoSwitch2.class, PacketType.HOMEDUINO_SWITCH2);
            put(HomeduinoSwitch4.class, PacketType.HOMEDUINO_SWITCH4);
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
