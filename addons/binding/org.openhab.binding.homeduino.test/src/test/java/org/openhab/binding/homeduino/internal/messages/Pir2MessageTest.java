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
 * Test for Homeduino pir2 messages
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Pir2MessageTest {
    private static final String PULSES = "451 1402 14356 0 0 0 0 0 ";
    private static final String ACTUAL_DATA = "01100110011001100110011001100110011001100110011002";
    private static final String RF_EVENT = "RF receive " + PULSES + ACTUAL_DATA;

    @Test
    public void testIncomingMessage_1() throws Exception {
        HomeduinoInterfaceMessage result = HomeduinoInterfaceMessageFactory
                .createMessage(RF_EVENT.getBytes(StandardCharsets.US_ASCII));
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result instanceof HomeduinoEventMessage);

        HomeduinoEventMessage rfEvent = (HomeduinoEventMessage) result;
        HomeduinoMessage event = TestHelper.getInterpretation(rfEvent, PacketType.PIR2);

        Assert.assertEquals(PacketType.PIR2, event.getPacketType());
        Assert.assertEquals("10.21", event.getDeviceId());
        Assert.assertEquals(event.convertToState(HomeduinoValueSelector.COMMAND), OnOffType.ON);
        Assert.assertEquals(event.convertToState(HomeduinoValueSelector.CONTACT), OpenClosedType.OPEN);
    }
}
