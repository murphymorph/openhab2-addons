/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.messages;

import java.util.List;

import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.Type;
import org.openhab.binding.homeduino.HomeduinoValueSelector;
import org.openhab.binding.homeduino.internal.exceptions.HomeduinoException;

/**
 * This interface defines interface which every message class should implement.
 *
 * @author Pauli Anttila - Initial contribution of code for RFXCom-binding
 * @author Martin van Wingerden - adapted for usage for the Homeduino-binding
 */
public interface HomeduinoMessage {

    String ID_DELIMITER = ".";

    /**
     * Procedure for converting Homeduino value to Openhab state.
     *
     * @param valueSelector
     *
     * @return Openhab state.
     */
    State convertToState(HomeduinoValueSelector valueSelector) throws HomeduinoException;

    /**
     * Procedure for converting Openhab state to Homeduino object.
     *
     */
    void convertFromState(HomeduinoValueSelector valueSelector, Type type) throws HomeduinoException;

    /**
     * Procedure to get device id.
     *
     * @return device Id.
     */
    String getDeviceId() throws HomeduinoException;

    /**
     * Procedure to set device id.
     *
     */
    void setDeviceId(String deviceId) throws HomeduinoException;

    /**
     * Procedure to get packet type.
     *
     * @return packet Type
     */
    PacketType getPacketType() throws HomeduinoException;

    /**
     * Procedure for get supported value selector list for input values.
     *
     * @return List of supported value selectors.
     */
    List<HomeduinoValueSelector> getSupportedInputValueSelectors() throws HomeduinoException;

    /**
     * Procedure for get supported value selector list for output values.
     *
     * @return List of supported value selectors.
     */
    List<HomeduinoValueSelector> getSupportedOutputValueSelectors() throws HomeduinoException;

}
