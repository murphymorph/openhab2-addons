/**
 * Copyright (c) 2010-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages.homeduino;

import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.messages.PacketType;

import static org.openhab.binding.rfxcom.internal.messages.PacketType.HOMEDUINO_ACK;

public class HomeduinoAcknowledgementMessage extends HomeduinoResponseMessage {

    @Override
    public PacketType getPacketType() throws RFXComException {
        return HOMEDUINO_ACK;
    }
}
