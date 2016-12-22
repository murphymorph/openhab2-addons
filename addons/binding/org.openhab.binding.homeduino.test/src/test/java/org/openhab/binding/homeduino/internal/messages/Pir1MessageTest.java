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
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.homeduino.HomeduinoValueSelector;
import org.openhab.binding.homeduino.internal.messages.homeduino.AbstractHomeduinoMessage;

/**
 * Test for Homeduino pir1 messages
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Pir1MessageTest {
    private static final String PULSES = "358 1095 11244 0 0 0 0 0 ";
    private static final String ACTUAL_DATA_1 = "01100101011001100110011001100110011001010110011002";
    private static final String RF_EVENT_1 = "RF receive " + PULSES + ACTUAL_DATA_1;

    private static final String ACTUAL_DATA_2 = "01100110011001100110010101100110011001010110011002";
    private static final String RF_EVENT_2 = "RF receive " + PULSES + ACTUAL_DATA_2;

    private static final String ACTUAL_DATA_3 = "01100110011001010110011001100110010101100110011002";
    private static final String RF_EVENT_3 = "RF receive " + PULSES + ACTUAL_DATA_3;

    @Test
    public void testOutgoingMessage() throws Exception {
        AbstractHomeduinoMessage msg = HomeduinoMessageFactory.createMessage(PacketType.PIR1);
        msg.setDeviceId("1.8");
        msg.convertFromState(HomeduinoValueSelector.CONTACT, OpenClosedType.OPEN);

        assertEquals("RF send 1 3 " + PULSES + ACTUAL_DATA_1, msg.decodeToHomeduinoMessage(1, 3));
    }

    @Test
    public void testIncomingMessage_1() throws Exception {
        HomeduinoInterfaceMessage result = HomeduinoInterfaceMessageFactory
                .createMessage(RF_EVENT_1.getBytes(StandardCharsets.US_ASCII));
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result instanceof HomeduinoEventMessage);

        HomeduinoEventMessage rfEvent = (HomeduinoEventMessage) result;
        HomeduinoMessage event = TestHelper.getInterpretation(rfEvent, PacketType.PIR1);

        Assert.assertEquals(PacketType.PIR1, event.getPacketType());
        Assert.assertEquals("1.8", event.getDeviceId());
        Assert.assertEquals(event.convertToState(HomeduinoValueSelector.COMMAND), OnOffType.ON);
        Assert.assertEquals(event.convertToState(HomeduinoValueSelector.CONTACT), OpenClosedType.OPEN);
    }

    @Test
    public void testIncomingMessage_2() throws Exception {
        HomeduinoInterfaceMessage result = HomeduinoInterfaceMessageFactory
                .createMessage(RF_EVENT_2.getBytes(StandardCharsets.US_ASCII));
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result instanceof HomeduinoEventMessage);

        HomeduinoEventMessage rfEvent = (HomeduinoEventMessage) result;
        HomeduinoMessage event = TestHelper.getInterpretation(rfEvent, PacketType.PIR1);

        Assert.assertEquals(PacketType.PIR1, event.getPacketType());
        Assert.assertEquals("17.0", event.getDeviceId());
        Assert.assertEquals(event.convertToState(HomeduinoValueSelector.COMMAND), OnOffType.ON);
        Assert.assertEquals(event.convertToState(HomeduinoValueSelector.CONTACT), OpenClosedType.OPEN);
    }

    @Test
    public void testIncomingMessage_3() throws Exception {
        HomeduinoInterfaceMessage result = HomeduinoInterfaceMessageFactory
                .createMessage(RF_EVENT_3.getBytes(StandardCharsets.US_ASCII));
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result instanceof HomeduinoEventMessage);

        HomeduinoEventMessage rfEvent = (HomeduinoEventMessage) result;
        HomeduinoMessage event = TestHelper.getInterpretation(rfEvent, PacketType.PIR1);

        Assert.assertEquals(PacketType.PIR1, event.getPacketType());
        Assert.assertEquals("2.2", event.getDeviceId());
        Assert.assertEquals(event.convertToState(HomeduinoValueSelector.COMMAND), OnOffType.ON);
        Assert.assertEquals(event.convertToState(HomeduinoValueSelector.CONTACT), OpenClosedType.OPEN);
    }

}
