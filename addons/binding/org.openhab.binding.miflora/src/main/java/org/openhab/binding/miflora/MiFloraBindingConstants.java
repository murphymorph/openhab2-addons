/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.miflora;

import org.eclipse.smarthome.binding.bluetooth.BluetoothBindingConstants;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link MiFloraBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Hakan Tandogan - Initial contribution
 */
public class MiFloraBindingConstants {
    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_BLE_FLORA = new ThingTypeUID(BluetoothBindingConstants.BINDING_ID,
            "miflora");

    // List of all Channel ids
    public static final String CHANNEL_1 = "channel1";

    public static final String MIFLORA_DEVICE_NAME = "Flower care";
}
