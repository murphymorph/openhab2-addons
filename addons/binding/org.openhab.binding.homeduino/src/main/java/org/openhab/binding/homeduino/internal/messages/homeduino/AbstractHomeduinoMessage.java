/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.messages.homeduino;

import java.math.BigDecimal;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.Type;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.homeduino.HomeduinoValueSelector;
import org.openhab.binding.homeduino.internal.exceptions.HomeduinoException;
import org.openhab.binding.homeduino.internal.messages.HomeduinoMessage;

/**
 * Base class for Homeduino message
 *
 * @author Pauli Anttila - Initial contribution of code for RFXCom-binding
 * @author Martin van Wingerden - adapted for usage for the Homeduino-binding
 */
abstract public class AbstractHomeduinoMessage implements HomeduinoMessage {
    private Result result;
    private Command command;

    AbstractHomeduinoMessage() {
        this.command = new Command();
    }

    AbstractHomeduinoMessage(Result result) {
        this.result = result;
    }

    Result getResult() {
        return result;
    }

    public String decodeToHomeduinoMessage(int transmitterPin, int repeats) {
        return getProtocol().decode(command, transmitterPin, repeats);
    }

    abstract HomeduinoProtocol getProtocol();

    public Command getCommand() {
        return command;
    }

    @Override
    public State convertToState(HomeduinoValueSelector valueSelector) throws HomeduinoException {
        if (valueSelector == HomeduinoValueSelector.DIMMING_LEVEL) {
            return getPercentTypeFromDimLevel(result.getDimLevel());
        } else if (valueSelector == HomeduinoValueSelector.COMMAND) {
            return result.getState() == 0 ? OnOffType.OFF : OnOffType.ON;
        } else if (valueSelector == HomeduinoValueSelector.CONTACT) {
            return result.getState() == 0 ? OpenClosedType.CLOSED : OpenClosedType.OPEN;
        } else if (valueSelector == HomeduinoValueSelector.SHUTTER) {
            if (result.getState() == 1) {
                return UpDownType.UP;
            } else if (result.getState() == 3) {
                return UpDownType.DOWN;
            } else {
                return UnDefType.UNDEF;
            }
        } else if (valueSelector == HomeduinoValueSelector.TEMPERATURE) {
            return new DecimalType(result.getTemperature());
        } else if (valueSelector == HomeduinoValueSelector.HUMIDITY) {
            return new DecimalType(result.getHumidity());
        } else if (valueSelector == HomeduinoValueSelector.LOW_BATTERY) {
            return result.isLowBattery() ? OnOffType.ON : OnOffType.OFF;
        }

        throw new HomeduinoException("Can't convert " + valueSelector + " to " + valueSelector.getItemClass());
    }

    /**
     * Convert a 0-15 scale value to a percent type.
     *
     * @param value percent type to convert
     * @return converted value 0-15
     */
    private static PercentType getPercentTypeFromDimLevel(int value) {
        value = Math.min(value, 15);

        return new PercentType(BigDecimal.valueOf(value).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(15), 0, BigDecimal.ROUND_UP).intValue());
    }

    @Override
    public void convertFromState(HomeduinoValueSelector valueSelector, Type type) throws HomeduinoException {
        if (!getSupportedOutputValueSelectors().contains(valueSelector)) {
            throw new HomeduinoException("Can't convert " + type + " to " + valueSelector);
        }

        command.convertFromState(valueSelector, type);
    }

    @Override
    public String getDeviceId() throws HomeduinoException {
        return result.getId() + "." + result.getUnit();
    }

    @Override
    public void setDeviceId(String deviceId) throws HomeduinoException {
        command.setDeviceId(deviceId);
    }
}
