/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComNotImpException;

public class RFXComMessageFactory {
    /**
     * Command to reset RFXCOM controller.
     * 
     */
    public final static byte[] CMD_RESET = new byte[] { 0x0D, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00 };

    /**
     * Command to get RFXCOM controller status.
     * 
     */
    public final static byte[] CMD_GET_STATUS = new byte[] { 0x0D, 0x00, 0x00, 0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00 };

    /**
     * Command to save RFXCOM controller configuration.
     * 
     */
    public final static byte[] CMD_SAVE = new byte[] { 0x0D, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00 };

    /**
     * Command to start RFXCOM receiver.
     * 
     */
    public final static byte[] CMD_START_RECEIVER = new byte[] { 0x0D, 0x00, 0x00, 0x03, 0x07, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00 };

    public static RFXComMessage createMessage(PacketType packetType) throws RFXComException, RFXComNotImpException {

        try {
            Class<? extends RFXComMessage> clazz = packetType.getMessageClass();
            Constructor<? extends RFXComMessage> c = clazz.getConstructor(byte[].class);
            return c.newInstance();
        } catch (Exception e) {
            throw new RFXComException(e);
        }
    }

    public static RFXComMessage createMessage(byte[] packet) throws RFXComException, RFXComNotImpException {

        PacketType packetType = getPacketType(packet[1]);

        try {
            Class<? extends RFXComMessage> clazz = packetType.getMessageClass();
            Constructor<? extends RFXComMessage> c = clazz.getConstructor(byte[].class);
            return c.newInstance(packet);
        } catch (Exception e) {
            throw new RFXComException(e);
        }
    }

    public static PacketType convertPacketType(String packetType) throws IllegalArgumentException {

        for (PacketType p : PacketType.values()) {
            if (p.toString().equals(packetType)) {
                return p;
            }
        }

        throw new IllegalArgumentException("Unknown packet type " + packetType);
    }

    private static PacketType getPacketType(byte packetType) {
        for (PacketType p : PacketType.values()) {
            if (p.toByte() == packetType) {
                return p;
            }
        }

        return PacketType.UNKNOWN;
    }
}
