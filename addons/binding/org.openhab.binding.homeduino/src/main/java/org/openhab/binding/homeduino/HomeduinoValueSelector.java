/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino;

import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.library.items.ContactItem;
import org.eclipse.smarthome.core.library.items.DimmerItem;
import org.eclipse.smarthome.core.library.items.NumberItem;
import org.eclipse.smarthome.core.library.items.RollershutterItem;
import org.eclipse.smarthome.core.library.items.StringItem;
import org.eclipse.smarthome.core.library.items.SwitchItem;

/**
 * Represents all valid value selectors which could be processed by Homeduino
 * devices.
 *
 * @author Pauli Anttila - Initial contribution of code for RFXCom-binding
 * @author Martin van Wingerden - adapted for usage for the Homeduino-binding
 */
public enum HomeduinoValueSelector {

    SHUTTER(HomeduinoBindingConstants.CHANNEL_SHUTTER, RollershutterItem.class),
    COMMAND(HomeduinoBindingConstants.CHANNEL_COMMAND, SwitchItem.class),
    DIMMING_LEVEL(HomeduinoBindingConstants.CHANNEL_DIMMING_LEVEL, DimmerItem.class),
    TEMPERATURE(HomeduinoBindingConstants.CHANNEL_TEMPERATURE, NumberItem.class),
    HUMIDITY(HomeduinoBindingConstants.CHANNEL_HUMIDITY, NumberItem.class),
    BATTERY_LEVEL(HomeduinoBindingConstants.CHANNEL_BATTERY_LEVEL, NumberItem.class),
    LOW_BATTERY(HomeduinoBindingConstants.CHANNEL_LOW_BATTERY, NumberItem.class),
    PRESSURE(HomeduinoBindingConstants.CHANNEL_PRESSURE, NumberItem.class),
    MOTION(HomeduinoBindingConstants.CHANNEL_MOTION, SwitchItem.class),
    CONTACT(HomeduinoBindingConstants.CHANNEL_CONTACT, ContactItem.class);

    private final String text;
    private Class<? extends Item> itemClass;

    HomeduinoValueSelector(final String text, Class<? extends Item> itemClass) {
        this.text = text;
        this.itemClass = itemClass;
    }

    @Override
    public String toString() {
        return text;
    }

    public Class<? extends Item> getItemClass() {
        return itemClass;
    }

    /**
     * Procedure to convert selector string to value selector class.
     *
     * @param valueSelectorText
     *            selector string e.g. RawData, Command, Temperature
     * @return corresponding selector value.
     */
    public static HomeduinoValueSelector getValueSelector(String valueSelectorText) {
        for (HomeduinoValueSelector c : HomeduinoValueSelector.values()) {
            if (c.text.equals(valueSelectorText)) {
                return c;
            }
        }

        throw new IllegalArgumentException("Not valid value selector");
    }
}
