/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.miflora.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.binding.bluetooth.BluetoothBindingConstants;
import org.eclipse.smarthome.binding.bluetooth.BluetoothDevice;
import org.eclipse.smarthome.binding.bluetooth.discovery.BluetoothDiscoveryParticipant;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.miflora.MiFloraBindingConstants;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true)
public class MifloraDiscoveryParticipant implements BluetoothDiscoveryParticipant {

    @Override
    public @NonNull Set<@NonNull ThingTypeUID> getSupportedThingTypeUIDs() {
        return Collections.singleton(MiFloraBindingConstants.THING_TYPE_BLE_FLORA);
    }

    @Override
    public @Nullable ThingUID getThingUID(@NonNull BluetoothDevice device) {
        if (MiFloraBindingConstants.MIFLORA_DEVICE_NAME.equals(device.getName())) {
            return new ThingUID(MiFloraBindingConstants.THING_TYPE_BLE_FLORA, device.getAdapter().getUID(),
                    device.getAddress().toString().toLowerCase().replace(":", ""));
        } else {
            return null;
        }
    }

    @Override
    public DiscoveryResult createResult(@NonNull BluetoothDevice device) {
        ThingUID thingUID = getThingUID(device);

        if (thingUID != null) {
            String label = device.getName();

            Map<String, Object> properties = new HashMap<>();
            properties.put(BluetoothBindingConstants.CONFIGURATION_ADDRESS, device.getAddress().toString());
            properties.put(Thing.PROPERTY_VENDOR, "MiFlora");
            Integer txPower = device.getTxPower();
            if (txPower != null) {
                properties.put(BluetoothBindingConstants.PROPERTY_TXPOWER, Integer.toString(txPower));
            }

            // Create the discovery result and add to the inbox
            return DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                    .withBridge(device.getAdapter().getUID()).withLabel(label).build();
        } else {
            return null;
        }
    }

}
