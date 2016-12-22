/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.messages;

import static org.openhab.binding.homeduino.HomeduinoValueSelector.*;

import java.nio.charset.StandardCharsets;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.homeduino.HomeduinoValueSelector;
import org.openhab.binding.homeduino.internal.exceptions.HomeduinoException;

/**
 * Test for Homeduino weather1 messages
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Weather1MessageTest {
    private static final String PULSES = "504 1936 3888 9188 0 0 0 0 ";
    private static final String ACTUAL_DATA = "01020102010101020201010102010201010101010202010201010102010102020102010203";
    private static final String RF_EVENT = "RF receive " + PULSES + ACTUAL_DATA;

    @Test
    public void testIncomingMessage() throws Exception {
        HomeduinoInterfaceMessage result = HomeduinoInterfaceMessageFactory
                .createMessage(RF_EVENT.getBytes(StandardCharsets.US_ASCII));
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result instanceof HomeduinoEventMessage);

        HomeduinoEventMessage rfEvent = (HomeduinoEventMessage) result;
        HomeduinoMessage event = TestHelper.getInterpretation(rfEvent, PacketType.WEATHER1);

        Assert.assertEquals(PacketType.WEATHER1, event.getPacketType());
        Assert.assertEquals("24.3", event.getDeviceId());
        Assert.assertEquals(20.9, convertState(event, TEMPERATURE), 0.001);
        Assert.assertEquals(53, convertState(event, HUMIDITY), 0.1);
        Assert.assertEquals(OnOffType.OFF, event.convertToState(LOW_BATTERY));
    }

    private double convertState(HomeduinoMessage event, HomeduinoValueSelector valueSelector)
            throws HomeduinoException {
        return ((DecimalType) (event.convertToState(valueSelector))).toBigDecimal().doubleValue();
    }
}
