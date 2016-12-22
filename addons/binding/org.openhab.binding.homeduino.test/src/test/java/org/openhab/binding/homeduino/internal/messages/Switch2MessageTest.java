/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.messages;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.homeduino.HomeduinoValueSelector;
import org.openhab.binding.homeduino.internal.messages.homeduino.AbstractHomeduinoMessage;

/**
 * Test for Homeduino switch2 messages
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Switch2MessageTest {
    private static final String PULSES = "306 957 9808 0 0 0 0 0 ";
    private static final String ACTUAL_DATA = "01010101011001100101010101100110011001100101011002";
    private static final String RF_EVENT_SWITCH2 = "RF receive " + PULSES + ACTUAL_DATA;

    @Test
    public void testOutgoingMessage() throws Exception {
        AbstractHomeduinoMessage msg = HomeduinoMessageFactory.createMessage(PacketType.SWITCH2);
        msg.setDeviceId("25.16");
        msg.convertFromState(HomeduinoValueSelector.COMMAND, OnOffType.ON);

        assertEquals("RF send 1 3 " + PULSES + ACTUAL_DATA, msg.decodeToHomeduinoMessage(1, 3));
    }

    @Test
    public void testIncomingMessage() throws Exception {
        HomeduinoInterfaceMessage result = HomeduinoInterfaceMessageFactory
                .createMessage(RF_EVENT_SWITCH2.getBytes(StandardCharsets.US_ASCII));
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result instanceof HomeduinoEventMessage);

        HomeduinoEventMessage rfEvent = (HomeduinoEventMessage) result;
        HomeduinoMessage event = TestHelper.getInterpretation(rfEvent, PacketType.SWITCH2);

        Assert.assertEquals(PacketType.SWITCH2, event.getPacketType());
        Assert.assertEquals("25.16", event.getDeviceId());
        Assert.assertEquals(event.convertToState(HomeduinoValueSelector.COMMAND), OnOffType.ON);
    }
}
