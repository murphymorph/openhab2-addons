/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.messages;

import java.nio.charset.StandardCharsets;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.homeduino.HomeduinoValueSelector;

/**
 * Test for Homeduino contact4 messages
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Contact4MessageTest {
    private static final String PULSES = "468 1364 14096 0 0 0 0 0 ";
    private static final String ACTUAL_DATA = "10101001100110011001011010100101010101011001100102";
    private static final String RF_EVENT = "RF receive " + PULSES + ACTUAL_DATA;

    @Test
    public void testIncomingMessage_1() throws Exception {
        HomeduinoInterfaceMessage result = HomeduinoInterfaceMessageFactory
                .createMessage(RF_EVENT.getBytes(StandardCharsets.US_ASCII));
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result instanceof HomeduinoEventMessage);

        HomeduinoEventMessage rfEvent = (HomeduinoEventMessage) result;
        HomeduinoMessage event = TestHelper.getInterpretation(rfEvent, PacketType.CONTACT4);

        Assert.assertEquals(PacketType.CONTACT4, event.getPacketType());
        Assert.assertEquals("960960", event.getDeviceId());
        Assert.assertEquals(OpenClosedType.OPEN, event.convertToState(HomeduinoValueSelector.CONTACT));
        Assert.assertEquals(OnOffType.OFF, event.convertToState(HomeduinoValueSelector.LOW_BATTERY));
    }
}
