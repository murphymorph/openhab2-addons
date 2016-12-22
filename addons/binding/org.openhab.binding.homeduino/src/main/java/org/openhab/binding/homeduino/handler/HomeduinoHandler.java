/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.handler;

import static org.openhab.binding.homeduino.HomeduinoBindingConstants.*;

import java.util.List;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.homeduino.HomeduinoValueSelector;
import org.openhab.binding.homeduino.internal.DeviceMessageListener;
import org.openhab.binding.homeduino.internal.config.HomeduinoDeviceConfiguration;
import org.openhab.binding.homeduino.internal.exceptions.HomeduinoException;
import org.openhab.binding.homeduino.internal.messages.HomeduinoMessage;
import org.openhab.binding.homeduino.internal.messages.HomeduinoMessageFactory;
import org.openhab.binding.homeduino.internal.messages.PacketType;
import org.openhab.binding.homeduino.internal.messages.homeduino.AbstractHomeduinoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HomeduinoHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Pauli Anttila - Initial contribution of code for RFXCom-binding
 * @author Martin van Wingerden - adapted for usage for the Homeduino-binding
 */
public class HomeduinoHandler extends BaseThingHandler implements DeviceMessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeduinoHandler.class);

    private HomeduinoBridgeHandler bridgeHandler;
    private HomeduinoDeviceConfiguration config;

    public HomeduinoHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        LOGGER.debug("Received channel: {}, command: {}", channelUID, command);

        if (bridgeHandler != null) {
            if (command instanceof RefreshType) {
                // Not supported
                LOGGER.trace("Received unsupported Refresh command");
            } else {
                try {
                    PacketType packetType = HomeduinoMessageFactory
                            .convertPacketType(channelUID.getThingUID().getThingTypeId().toUpperCase());

                    AbstractHomeduinoMessage msg = HomeduinoMessageFactory.createMessage(packetType);

                    List<HomeduinoValueSelector> supportedValueSelectors = msg.getSupportedOutputValueSelectors();

                    HomeduinoValueSelector valSelector = HomeduinoValueSelector.getValueSelector(channelUID.getId());

                    if (supportedValueSelectors.contains(valSelector)) {
                        msg.setDeviceId(config.deviceId);
                        msg.convertFromState(valSelector, command);

                        bridgeHandler.sendMessage(msg);
                    } else {
                        LOGGER.warn("Homeduino doesn't support transmitting for channel '{}'", channelUID.getId());
                    }
                } catch (HomeduinoException e) {
                    LOGGER.error("Transmitting error", e);
                }
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        LOGGER.debug("Initializing thing {}", getThing().getUID());
        initializeBridge((getBridge() == null) ? null : getBridge().getHandler(),
                (getBridge() == null) ? null : getBridge().getStatus());
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        LOGGER.debug("bridgeStatusChanged {} for thing {}", bridgeStatusInfo, getThing().getUID());
        initializeBridge((getBridge() == null) ? null : getBridge().getHandler(), bridgeStatusInfo.getStatus());
    }

    private void initializeBridge(ThingHandler thingHandler, ThingStatus bridgeStatus) {
        LOGGER.debug("initializeBridge {} for thing {}", bridgeStatus, getThing().getUID());

        config = getConfigAs(HomeduinoDeviceConfiguration.class);
        if (config.deviceId == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Homeduino device missing deviceId");
        } else if (thingHandler != null && bridgeStatus != null) {

            bridgeHandler = (HomeduinoBridgeHandler) thingHandler;
            bridgeHandler.registerDeviceStatusListener(this);

            if (bridgeStatus == ThingStatus.ONLINE) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            }
        } else {
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.smarthome.core.thing.binding.BaseThingHandler#dispose()
     */
    @Override
    public void dispose() {
        LOGGER.debug("Thing {} disposed.", getThing().getUID());
        if (bridgeHandler != null) {
            bridgeHandler.unregisterDeviceStatusListener(this);
        }
        bridgeHandler = null;
        super.dispose();
    }

    @Override
    public void onDeviceMessageReceived(ThingUID bridge, HomeduinoMessage message) {
        try {
            String id = message.getDeviceId();
            if (config.deviceId.equals(id)) {
                PacketType packetType = message.getPacketType();
                String receivedId = PACKET_TYPE_THING_MAP.get(packetType).getId();
                LOGGER.debug("Received message from bridge: {} message: {}", bridge, message);

                if (receivedId.equals(getThing().getThingTypeUID().getId())) {
                    updateStatus(ThingStatus.ONLINE);

                    List<HomeduinoValueSelector> supportedValueSelectors = message.getSupportedInputValueSelectors();

                    if (supportedValueSelectors != null) {
                        for (HomeduinoValueSelector valueSelector : supportedValueSelectors) {
                            switch (valueSelector) {
                                case COMMAND:
                                    updateState(CHANNEL_COMMAND, message.convertToState(valueSelector));
                                    break;
                                case CONTACT:
                                    updateState(CHANNEL_CONTACT, message.convertToState(valueSelector));
                                    break;
                                case DIMMING_LEVEL:
                                    updateState(CHANNEL_DIMMING_LEVEL, message.convertToState(valueSelector));
                                    break;
                                case HUMIDITY:
                                    updateState(CHANNEL_HUMIDITY, message.convertToState(valueSelector));
                                    break;
                                case MOTION:
                                    updateState(CHANNEL_MOTION, message.convertToState(valueSelector));
                                    break;
                                case PRESSURE:
                                    updateState(CHANNEL_PRESSURE, message.convertToState(valueSelector));
                                    break;
                                case SHUTTER:
                                    updateState(CHANNEL_SHUTTER, message.convertToState(valueSelector));
                                    break;
                                case TEMPERATURE:
                                    updateState(CHANNEL_TEMPERATURE, message.convertToState(valueSelector));
                                    break;
                                default:
                                    LOGGER.debug("Unsupported value selector '{}'", valueSelector);
                                    break;
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred during message receiving", e);
        }
    }

}
