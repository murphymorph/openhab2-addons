/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.messages.homeduino;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.Type;
import org.openhab.binding.homeduino.HomeduinoValueSelector;
import org.openhab.binding.homeduino.internal.exceptions.HomeduinoException;

/**
 * Command class for sending commands via the Homeduino bridge
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Command {
    private int sensorId;
    private String unitCode;
    private boolean group;

    private HomeduinoValueSelector valueSelector;
    private Type command;

    void setDeviceId(String deviceId) {
        String[] parts = deviceId.split("\\.");
        sensorId = Integer.parseInt(parts[0]);
        unitCode = parts[1];
    }

    public void setGroup() {
        this.group = true;
    }

    public int getSensorId() {
        return sensorId;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public int getUnitCodeAsInt() {
        return Integer.parseInt(unitCode);
    }

    public boolean isGroup() {
        return group;
    }

    public Type getCommand() {
        return command;
    }

    public void convertFromState(HomeduinoValueSelector valueSelector, Type type) throws HomeduinoException {
        this.valueSelector = valueSelector;
        this.command = type;
    }

    public static Command inverse(Command source) {
        Command target = new Command();

        target.sensorId = source.sensorId;
        target.unitCode = source.unitCode;
        target.group = source.group;
        target.valueSelector = source.valueSelector;
        target.command = inverse(source.command);

        return target;
    }

    private static Type inverse(Type command) {
        if (command instanceof OnOffType) {
            return command == OnOffType.OFF ? OnOffType.ON : OnOffType.OFF;
        } else {
            throw new IllegalArgumentException("This inverse is not yet supported: " + command);
        }
    }
}
