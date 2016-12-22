/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino;

import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.openhab.binding.homeduino.internal.messages.PacketType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * The {@link HomeduinoBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Pauli Anttila - build an ancestor of this code for the RFXCom binding
 * @author Martin van Wingerden - Initial contribution of the homeduino binding
 */
public class HomeduinoBindingConstants {
    private static final String BINDING_ID = "homeduino";

    public static final String DEVICE_ID = "deviceId";

    private static final String BRIDGE_TYPE_HOMEDUINO = "homeduino";

    public final static ThingTypeUID BRIDGE_HOMEDUINO = new ThingTypeUID(BINDING_ID, BRIDGE_TYPE_HOMEDUINO);

    /**
     * Presents all supported Bridge types by Homeduino binding.
     */
    public final static Set<ThingTypeUID> SUPPORTED_BRIDGE_THING_TYPES_UIDS = ImmutableSet.of(BRIDGE_HOMEDUINO);

    // List of all Channel ids
    public final static String CHANNEL_SHUTTER = "shutter";
    public final static String CHANNEL_COMMAND = "command";
    public final static String CHANNEL_DIMMING_LEVEL = "dimmingLevel";
    public final static String CHANNEL_TEMPERATURE = "temperature";
    public final static String CHANNEL_HUMIDITY = "humidity";
    public final static String CHANNEL_BATTERY_LEVEL = "batteryLevel";
    public final static String CHANNEL_LOW_BATTERY = "lowBattery";
    public final static String CHANNEL_PRESSURE = "pressure";
    public final static String CHANNEL_MOTION = "motion";
    public final static String CHANNEL_CONTACT = "contact";

    /**
     * Map Homeduino packet types to Homeduino Thing types and vice versa.
     */
    public final static Map<PacketType, ThingTypeUID> PACKET_TYPE_THING_MAP = getPacketTypeThingMap();

    private static ImmutableMap<PacketType, ThingTypeUID> getPacketTypeThingMap() {
        ImmutableMap.Builder<PacketType, ThingTypeUID> builder = ImmutableMap.builder();

        for (PacketType type : PacketType.values()) {
            builder.put(type, new ThingTypeUID(BINDING_ID, type.thingTypeId()));
        }

        return builder.build();
    }

    /**
     * Presents all supported Thing types by Homeduino binding.
     */
    public final static Set<ThingTypeUID> SUPPORTED_DEVICE_THING_TYPES_UIDS = getSupportedThingTypes();

    private static Set<ThingTypeUID> getSupportedThingTypes() {
        ImmutableSet.Builder<ThingTypeUID> typesBuilder = ImmutableSet.builder();

        for (PacketType type : PacketType.values()) {
            typesBuilder.add(PACKET_TYPE_THING_MAP.get(type));
        }

        return typesBuilder.build();
    }
}
