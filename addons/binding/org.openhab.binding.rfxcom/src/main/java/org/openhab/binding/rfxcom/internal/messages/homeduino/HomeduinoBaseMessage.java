package org.openhab.binding.rfxcom.internal.messages.homeduino;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.rfxcom.internal.messages.PacketType;

public abstract class HomeduinoBaseMessage implements HomeduinoMessage {
    private PacketType packetType;

    public HomeduinoBaseMessage(PacketType packetType) {
        this.packetType = packetType;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    private static final Map<String, PacketType> MAP = Collections.unmodifiableMap(new HashMap<String, PacketType>() {
        {
            put("rea", PacketType.HOMEDUINO_READY);
            put("ACK", PacketType.HOMEDUINO_ACK);
            put("ERR", PacketType.HOMEDUINO_ERROR);
            put("RF ", PacketType.HOMEDUINO_RF_EVENT);
        }
    });

    static PacketType valueOfString(String value) {
        PacketType p = PacketType.UNKNOWN;
        if (MAP.containsKey(value)) {
            p = MAP.get(value);
        }

        return p;
    }
}
