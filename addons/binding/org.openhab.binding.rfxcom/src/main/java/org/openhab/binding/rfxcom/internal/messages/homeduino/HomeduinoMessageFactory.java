package org.openhab.binding.rfxcom.internal.messages.homeduino;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComNotImpException;
import org.openhab.binding.rfxcom.internal.messages.PacketType;
import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoDimmer1;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoPir1;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoProtocol;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoShutter3;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoSwitch1;
import org.openhab.binding.rfxcom.internal.messages.homeduino.protocols.HomeduinoSwitch4;

public class HomeduinoMessageFactory {
    @SuppressWarnings("serial")
    private static final Map<PacketType, Class<? extends HomeduinoProtocol>> PROTOCOL_CLASSES = Collections
            .unmodifiableMap(new HashMap<PacketType, Class<? extends HomeduinoProtocol>>() {
                {
                    put(PacketType.HOMEDUINO_DIMMER1, HomeduinoDimmer1.class);
                    put(PacketType.HOMEDUINO_PIR1, HomeduinoPir1.class);
                    put(PacketType.HOMEDUINO_SHUTTER3, HomeduinoShutter3.class);
                    put(PacketType.HOMEDUINO_SWITCH1, HomeduinoSwitch1.class);
                    put(PacketType.HOMEDUINO_SWITCH4, HomeduinoSwitch4.class);
                }
            });

    public static HomeduinoMessage createMessage(byte[] packet) throws RFXComNotImpException, RFXComException {
        PacketTypeHomeduino packetTypeHomeduino = getPacketType(Arrays.copyOfRange(packet, 0, 3));

        try {
            Class<? extends HomeduinoMessage> clazz = packetTypeHomeduino.getMessageClass();
            try {
                Constructor<? extends HomeduinoMessage> c = clazz.getConstructor(byte[].class);
                return c.newInstance(packet);
            } catch (NoSuchMethodException e) {
                Constructor<? extends HomeduinoMessage> c = clazz.getConstructor();
                return c.newInstance();
            }
        } catch (Exception e) {
            throw new RFXComException(e);
        }
    }

    private static PacketTypeHomeduino getPacketType(byte[] copyOfRange) {
        return HomeduinoBaseMessage.valueOfString(new String(copyOfRange));
    }

    public static PacketTypeHomeduino convertPacketType(String packetType) {
        for (PacketTypeHomeduino p : PacketTypeHomeduino.values()) {
            if (p.toString().equals(packetType)) {
                return p;
            }
        }

        throw new IllegalArgumentException("Unknown packet type " + packetType);
    }

    public static RFXComMessage createMessage(PacketType packetType) throws RFXComException {
        try {
            Class<? extends HomeduinoProtocol> clazz = PROTOCOL_CLASSES.get(packetType);
            return new RFXComHomeduinoCommand(clazz.newInstance());
        } catch (Exception e) {
            throw new RFXComException(e);
        }
    }

}
