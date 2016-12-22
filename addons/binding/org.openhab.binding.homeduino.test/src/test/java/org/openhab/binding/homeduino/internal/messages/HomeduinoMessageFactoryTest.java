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

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.junit.Test;
import org.openhab.binding.homeduino.HomeduinoValueSelector;
import org.openhab.binding.homeduino.internal.messages.homeduino.AbstractHomeduinoMessage;

/**
 * Test for Homeduino message factory
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class HomeduinoMessageFactoryTest {
    @Test
    public void testOutgoingMessageSwitch1() throws Exception {
        AbstractHomeduinoMessage msg = HomeduinoMessageFactory.createMessage(PacketType.SWITCH1);
        msg.setDeviceId("9390234.1");
        msg.convertFromState(HomeduinoValueSelector.COMMAND, OnOffType.ON);

        assertEquals(
                "RF send 1 3 260 1300 2700 10400 0 0 0 0 020001000101000001000100010100010001000100000101000001000101000001000100010100000100010100010000010100000100010100000100010001000103",
                msg.decodeToHomeduinoMessage(1, 3));
    }

}
