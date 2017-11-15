/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
  * Copyright (c) 2010-2017 by the respective copyright holders.
 * <p>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.miflora.handler;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.binding.bluetooth.BluetoothCharacteristic;
import org.eclipse.smarthome.binding.bluetooth.BluetoothCompletionStatus;
import org.eclipse.smarthome.binding.bluetooth.BluetoothDeviceListener;
import org.eclipse.smarthome.binding.bluetooth.GenericBluetoothHandler;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MiFloraBluetoothHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Hakan Tandogan - Initial contribution
 */
public class MiFloraBluetoothHandler extends GenericBluetoothHandler implements BluetoothDeviceListener {

    private final Logger logger = LoggerFactory.getLogger(MiFloraBluetoothHandler.class);

    private ScheduledFuture<?> discoveryScheduler;

    private static final UUID HANDLE_READ_VERSION_BATTERY = UUID.fromString("0000fef5-0000-0000-0000-000000000000");
    private static final UUID HANDLE_READ_NAME = UUID.fromString("00001800-0000-0000-0000-000000000000");
    private static final UUID HANDLE_READ_SENSOR_DATA = UUID.fromString("00000035-0000-0000-0000-000000000000");
    private static final UUID HANDLE_WRITE_MODE_CHANGE = UUID.fromString("00001001-0000-0000-0000-000000000000");
    private static final UUID DATA_MODE_CHANGE = UUID.fromString("00001FA0-0000-0000-0000-000000000000");

    public MiFloraBluetoothHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        super.initialize();

        // it seems needed to retry this???
        discoveryScheduler = scheduler.scheduleAtFixedRate(this::connectAndDiscoverServices, 0, 15, TimeUnit.SECONDS);
    }

    public void connectAndDiscoverServices() {
        device.connect();
        device.discoverServices();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            // In case of communication error or somesuch
            // updateStatus(ThingStatus.OFFLINE);
            // else
            // updateStatus(ThingStatus.ONLINE);
            // and update data

            logger.debug("Handling command {} with data {}", command, channelUID);
        } else {
            logger.debug("Unsupported command {}! Supported commands: REFRESH", command);
        }
    }

    @Override
    public void onServicesDiscovered() {
        if (discoveryScheduler != null && !discoveryScheduler.isCancelled()) {
            discoveryScheduler.cancel(true);
            discoveryScheduler = null;
        }

        // Everything is initialised now - get the characteristics we want to use

        BluetoothCharacteristic v = device.getCharacteristic(HANDLE_READ_VERSION_BATTERY);
        boolean value = device.readCharacteristic(v);
        System.out.println(value);

        v = device.getCharacteristic(HANDLE_READ_NAME);
        value = device.readCharacteristic(v);
        System.out.println(value);

        // Read the current value so we can update the UI
        readStatus();
    }

    @Override
    public void onCharacteristicWriteComplete(BluetoothCharacteristic characteristic,
            BluetoothCompletionStatus status) {
        // If this was a write to the control, then read back the state
        /*
         * if (characteristic.getUuid().equals(UUID_YEELIGHT_CONTROL)) {
         * readStatus();
         * }
         */
    }

    @Override
    public void onCharacteristicUpdate(BluetoothCharacteristic characteristic) {
        // this method can pass null to its child is that desired?

        if (characteristic.getUuid().equals(HANDLE_WRITE_MODE_CHANGE)) {
            return;
        } else if (characteristic.getUuid().equals(HANDLE_READ_VERSION_BATTERY)) {

        }
        System.out.println(characteristic.getUuid());
    }

    private void readStatus() {
        /**
         * if (characteristicRequest == null) {
         * logger.debug("YeeLightBlue status characteristic not known");
         * return;
         * }
         *
         * characteristicRequest.setValue("S");
         * device.writeCharacteristic(characteristicRequest);
         **/
    }
}
