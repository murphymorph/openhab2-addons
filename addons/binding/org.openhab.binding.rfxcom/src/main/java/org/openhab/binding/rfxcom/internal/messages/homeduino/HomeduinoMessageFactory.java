package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComNotImpException;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class HomeduinoMessageFactory {
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
}
