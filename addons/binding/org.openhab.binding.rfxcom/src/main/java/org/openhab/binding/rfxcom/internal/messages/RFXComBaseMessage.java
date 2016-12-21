/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;

import javax.xml.bind.DatatypeConverter;

import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;

/**
 * Base class for RFXCOM data classes. All other data classes should extend this class.
 *
 * @author Pauli Anttila - Initial contribution
 */
public abstract class RFXComBaseMessage implements RFXComMessage {

    public final static String ID_DELIMITER = ".";

    public byte[] rawMessage;
    public PacketType packetType = PacketType.UNKNOWN;
    public byte packetId = 0;
    public byte subType = 0;
    public byte seqNbr = 0;
    public byte id1 = 0;
    public byte id2 = 0;

    public RFXComBaseMessage() {

    }

    public RFXComBaseMessage(byte[] data) {
        encodeMessage(data);
    }

    @Override
    public void encodeMessage(byte[] data) {

        rawMessage = data;

        packetId = data[1];
        packetType = PacketType.fromByte(data[1]);
        subType = data[2];
        seqNbr = data[3];
        id1 = data[4];

        if (data.length > 5) {
            id2 = data[5];
        }
    }

    @Override
    public String toString() {
        String str = "";

        if (rawMessage == null) {
            str += "Raw data = unknown";
        } else {
            str += "Raw data = " + DatatypeConverter.printHexBinary(rawMessage);
        }

        str += ", Packet type = " + packetType;
        str += ", Seq number = " + (short) (seqNbr & 0xFF);

        return str;
    }

    @Override
    public String getDeviceId() {
        return id1 + ID_DELIMITER + id2;
    }

    @Override
    public PacketType getPacketType() {
        return this.packetType;
    }

    @Override
    public Object convertSubType() throws RFXComException {
        return convertSubType(String.valueOf(this.subType));
    }

}
