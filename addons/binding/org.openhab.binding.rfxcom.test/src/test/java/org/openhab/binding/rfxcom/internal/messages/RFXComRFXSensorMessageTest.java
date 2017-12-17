/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;

import static org.junit.Assert.assertEquals;
import static org.openhab.binding.rfxcom.internal.messages.RFXComRFXSensorMessage.SubType.A_D;
import static org.openhab.binding.rfxcom.internal.messages.RFXComRFXSensorMessage.SubType.TEMPERATURE;
import static org.openhab.binding.rfxcom.internal.messages.RFXComRFXSensorMessage.SubType.VOLTAGE;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.types.State;
import org.junit.Test;
import org.openhab.binding.rfxcom.RFXComValueSelector;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;

import javax.xml.bind.DatatypeConverter;

/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 * @since 2.1.0
 */
public class RFXComRFXSensorMessageTest {
    private void testMessage(String hexMsg, RFXComRFXSensorMessage.SubType subType, int seqNbr, String deviceId,
                             Double temperature, Double voltage, int signalLevel) throws RFXComException {
        final RFXComRFXSensorMessage msg = (RFXComRFXSensorMessage) RFXComMessageFactory
                .createMessage(DatatypeConverter.parseHexBinary(hexMsg));
        assertEquals("SubType", subType, msg.subType);
        assertEquals("Seq Number", seqNbr, (short) (msg.seqNbr & 0xFF));
        assertEquals("Sensor Id", deviceId, msg.getDeviceId());
        assertEquals("Signal Level", signalLevel, msg.signalLevel);
        assertEquals("Temperature", temperature, getMessageTemperature(msg));
        assertEquals("Voltage", voltage, getMessageVoltage(msg));

        byte[] decoded = msg.decodeMessage();

        assertEquals("Message converted back", hexMsg, DatatypeConverter.printHexBinary(decoded));
    }

    @Test
    public void testSomeMessages() throws RFXComException {
        testMessage("0770000008080270", TEMPERATURE, 0, "8", 20.5d, null, 7);
        testMessage("0770000208809650", TEMPERATURE, 2, "8", -1.5d, null, 5);
        testMessage("077002010801F270", VOLTAGE, 1, "8", null, 4.98, 7);
        testMessage("077001020800F470", A_D, 2, "8", null, null, 7);
    }

    private Double getMessageVoltage(RFXComRFXSensorMessage msg) throws RFXComException {
        return getStateAsDouble(msg.convertToState(RFXComValueSelector.VOLTAGE));
    }

    private Double getMessageTemperature(RFXComRFXSensorMessage msg) throws RFXComException {
        return getStateAsDouble(msg.convertToState(RFXComValueSelector.TEMPERATURE));
    }

    private Double getStateAsDouble(State state) {
        if (state instanceof DecimalType) {
            return ((DecimalType) state).doubleValue();
        } else {
            return null;
        }
    }
}
