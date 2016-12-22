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
import static org.openhab.binding.homeduino.internal.messages.PacketType.SHUTTER3;

import java.nio.charset.StandardCharsets;

import org.eclipse.smarthome.core.library.types.StopMoveType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.Type;
import org.eclipse.smarthome.core.types.UnDefType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openhab.binding.homeduino.HomeduinoValueSelector;
import org.openhab.binding.homeduino.internal.exceptions.HomeduinoException;
import org.openhab.binding.homeduino.internal.messages.homeduino.AbstractHomeduinoMessage;
import org.openhab.binding.homeduino.internal.messages.homeduino.HomeduinoProtocol;

/**
 * Test for Homeduino shutter3 messages
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Shutter3MessageTest {
    private static final String PULSES = "366 736 1600 5204 10896 0 0 0 ";

    private static final String ACTUAL_DATA_DOWN = "3201010110101010100110010101010101100110101010010101100110010101100101101010100104";
    private static final String RF_RECEIVE = "RF receive ";
    private static final String RF_EVENTS_SHUTTER_DOWN = RF_RECEIVE + PULSES + ACTUAL_DATA_DOWN;

    private static final String ACTUAL_DATA_UP = "3201010110101010100110010101010101100110101010010101100110010101100101011001010114";
    private static final String RF_EVENTS_SHUTTER_UP = RF_RECEIVE + PULSES + ACTUAL_DATA_UP;

    private static final String ACTUAL_DATA_UP_2 = "3201010110101010100110010101010101100110101010010101100110010101100101011001010114";
    private static final String RF_EVENTS_SHUTTER_UP_2 = RF_RECEIVE + PULSES + ACTUAL_DATA_UP_2;

    private static final String ACTUAL_DATA_STOP = "3210011010011010100110010101010101100110101001101001100110010101100110011001100114";
    private static final String RF_EVENTS_SHUTTER_STOP = RF_RECEIVE + PULSES + ACTUAL_DATA_STOP;

    private static final String TEST = "RF receive 5108 1720 724 360 11012 0 0 0 0123232323232323223223323232323232322332233232233232232332322332323223322332233224";

    @Test
    public void testIncomingMessageDown() throws Exception {
        testIncomingMessage(RF_EVENTS_SHUTTER_DOWN, SHUTTER3, HomeduinoValueSelector.SHUTTER, UpDownType.DOWN,
                "65542026.1", ACTUAL_DATA_DOWN, UpDownType.DOWN);
    }

    @Test
    public void testIncomingMessageUp() throws Exception {
        testIncomingMessage(RF_EVENTS_SHUTTER_UP, SHUTTER3, HomeduinoValueSelector.SHUTTER, UpDownType.UP, "65542026.1",
                ACTUAL_DATA_UP, UpDownType.UP);
    }

    @Test
    public void testIncomingMessageUp2() throws Exception {
        testIncomingMessage(RF_EVENTS_SHUTTER_UP_2, SHUTTER3, HomeduinoValueSelector.SHUTTER, UpDownType.UP,
                "65542026.1", ACTUAL_DATA_UP_2, UpDownType.UP);
    }

    @Test
    public void testIncomingMessageStop() throws Exception {
        testIncomingMessage(RF_EVENTS_SHUTTER_STOP, SHUTTER3, HomeduinoValueSelector.SHUTTER, UnDefType.UNDEF,
                "384309098.1", ACTUAL_DATA_STOP, StopMoveType.STOP);
    }

    @Ignore("Used to test some example messages from the forum thread")
    @Test
    public void testIncomingMessageTEST() throws Exception {
        testIncomingMessage(TEST, SHUTTER3, HomeduinoValueSelector.SHUTTER, UpDownType.UP, "65542026.1",
                ACTUAL_DATA_STOP, UpDownType.UP);
    }

    private void testIncomingMessage(String incomingMessage, PacketType expectedEvent,
            HomeduinoValueSelector valueSelector, Type type, String deviceId, String expectedData, Type command)
            throws HomeduinoException {
        System.out.println(HomeduinoProtocol.prepareAndFixCompressedPulses(incomingMessage.getBytes()));

        HomeduinoInterfaceMessage result = HomeduinoInterfaceMessageFactory
                .createMessage(incomingMessage.getBytes(StandardCharsets.US_ASCII));
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result instanceof HomeduinoEventMessage);

        HomeduinoEventMessage rfEvent = (HomeduinoEventMessage) result;
        HomeduinoMessage event = TestHelper.getInterpretation(rfEvent, PacketType.SHUTTER3);

        Assert.assertEquals(expectedEvent, event.getPacketType());
        Assert.assertEquals(deviceId, event.getDeviceId());
        Assert.assertEquals(type, event.convertToState(valueSelector));

        System.out.println(StopMoveType.STOP + " "
                + convertEventToNewMessage(event, StopMoveType.STOP).decodeToHomeduinoMessage(1, 3));
        System.out.println(
                UpDownType.UP + " " + convertEventToNewMessage(event, UpDownType.UP).decodeToHomeduinoMessage(1, 3));
        System.out.println(UpDownType.DOWN + " "
                + convertEventToNewMessage(event, UpDownType.DOWN).decodeToHomeduinoMessage(1, 3));

        assertEquals("RF send 1 3 " + PULSES + expectedData.substring(0, 74),
                convertEventToNewMessage(event, command).decodeToHomeduinoMessage(1, 3).substring(0, 116));
    }

    private AbstractHomeduinoMessage convertEventToNewMessage(HomeduinoMessage event, Type command)
            throws HomeduinoException {
        AbstractHomeduinoMessage msg = HomeduinoMessageFactory.createMessage(SHUTTER3);
        msg.setDeviceId(event.getDeviceId());
        State state = event.convertToState(HomeduinoValueSelector.SHUTTER);
        msg.convertFromState(HomeduinoValueSelector.SHUTTER, command);
        return msg;
    }
}
