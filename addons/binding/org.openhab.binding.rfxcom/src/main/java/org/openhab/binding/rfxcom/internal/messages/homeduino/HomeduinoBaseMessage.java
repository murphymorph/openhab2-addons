package org.openhab.binding.rfxcom.internal.messages.homeduino;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

abstract class HomeduinoBaseMessage implements HomeduinoMessage {
    private static final Map<String, PacketTypeHomeduino> MAP = Collections.unmodifiableMap(new HashMap<String, PacketTypeHomeduino>() {
        {
            put("rea", PacketTypeHomeduino.HOMEDUINO_READY);
            put("ACK", PacketTypeHomeduino.HOMEDUINO_ACK);
            put("ERR", PacketTypeHomeduino.HOMEDUINO_ERROR);
            put("RF ", PacketTypeHomeduino.HOMEDUINO_RF_EVENT);
        }
    });

    static PacketTypeHomeduino valueOfString(String value) {
        PacketTypeHomeduino p = PacketTypeHomeduino.UNKNOWN;
        if (MAP.containsKey(value)) {
            p = MAP.get(value);
        }

        return p;
    }
}
