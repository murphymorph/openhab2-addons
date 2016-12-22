/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.discovery;

import java.util.Set;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.homeduino.HomeduinoBindingConstants;
import org.openhab.binding.homeduino.handler.HomeduinoBridgeHandler;
import org.openhab.binding.homeduino.internal.DeviceMessageListener;
import org.openhab.binding.homeduino.internal.messages.HomeduinoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HomeduinoDeviceDiscoveryService} class is used to discover Homeduino
 * devices that send messages to Homeduino bridge.
 *
 * @author Pauli Anttila - Initial contribution of code for RFXCom-binding
 * @author Martin van Wingerden - adapted for usage for the Homeduino-binding
 */
public class HomeduinoDeviceDiscoveryService extends AbstractDiscoveryService implements DeviceMessageListener {

    private final static Logger logger = LoggerFactory.getLogger(HomeduinoDeviceDiscoveryService.class);

    private HomeduinoBridgeHandler bridgeHandler;

    public HomeduinoDeviceDiscoveryService(HomeduinoBridgeHandler homeduinoBridgeHandler) {
        super(null, 1, false);
        this.bridgeHandler = homeduinoBridgeHandler;
    }

    public void activate() {
        bridgeHandler.registerDeviceStatusListener(this);
    }

    @Override
    public void deactivate() {
        bridgeHandler.unregisterDeviceStatusListener(this);
    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypes() {
        return HomeduinoBindingConstants.SUPPORTED_DEVICE_THING_TYPES_UIDS;
    }

    @Override
    protected void startScan() {
        // this can be ignored here as we discover devices from received messages
    }

    @Override
    public void onDeviceMessageReceived(ThingUID bridge, HomeduinoMessage message) {
        logger.trace("Received: bridge: {} message: {}", bridge, message);

        try {
            if (message != null) {
                String id = message.getDeviceId();
                ThingTypeUID uid = HomeduinoBindingConstants.PACKET_TYPE_THING_MAP.get(message.getPacketType());
                ThingUID thingUID = new ThingUID(uid, bridge, id.replace(HomeduinoMessage.ID_DELIMITER, "_"));
                logger.trace("Adding new Homeduino {} with id '{}' to smarthome inbox", thingUID, id);

                String label = message.getPacketType() + "-" + id;
                DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withLabel(label)
                        .withProperty(HomeduinoBindingConstants.DEVICE_ID, id).withBridge(bridge).build();
                thingDiscovered(discoveryResult);
            } else {
                logger.warn("An unsupported device, was talking");
            }
        } catch (Exception e) {
            logger.debug("Error occurred during device discovery", e);
        }
    }
}
