package org.openhab.binding.rfxcom.internal.messages.homeduino;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComNotImpException;
import org.openhab.binding.rfxcom.internal.messages.PacketType;

public class HomeduinoMessageFactory {

    final static String classUrl = "org.openhab.binding.rfxcom.internal.messages.homeduino.";

    @SuppressWarnings("serial")
    private static final Map<PacketType, String> messageClasses = Collections
            .unmodifiableMap(new HashMap<PacketType, String>() {
                {
                    put(PacketType.HOMEDUINO_ACK, "HomeduinoAcknowledgementMessage");
                    put(PacketType.HOMEDUINO_ERROR, "HomeduinoErrorMessage");
                    put(PacketType.HOMEDUINO_RF_EVENT, "HomeduinoEventMessage");
                    put(PacketType.HOMEDUINO_READY, "HomeduinoReadyMessage");
                }
            });

    public static HomeduinoMessage createMessage(byte[] packet) throws RFXComNotImpException, RFXComException {
        PacketType packetType = getPacketType(Arrays.copyOfRange(packet, 0, 3));

        try {
            String className = messageClasses.get(packetType);
            Class<?> cl = Class.forName(classUrl + className);
            try {
                Constructor<?> c = cl.getConstructor(byte[].class);
                return (HomeduinoMessage) c.newInstance(packet);
            } catch (NoSuchMethodException e) {
                Constructor<?> c = cl.getConstructor();
                return (HomeduinoMessage) c.newInstance();
            }
        } catch (ClassNotFoundException e) {
            throw new RFXComNotImpException("Message " + packetType + " not implemented", e);

        } catch (Exception e) {
            throw new RFXComException(e);
        }
    }

    private static PacketType getPacketType(byte[] copyOfRange) {
        return HomeduinoBaseMessage.valueOfString(new String(copyOfRange));
    }

}
