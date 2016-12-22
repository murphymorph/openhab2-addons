/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.handler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.homeduino.internal.DeviceMessageListener;
import org.openhab.binding.homeduino.internal.config.HomeduinoBridgeConfiguration;
import org.openhab.binding.homeduino.internal.connector.HomeduinoEventListener;
import org.openhab.binding.homeduino.internal.connector.HomeduinoSerialConnector;
import org.openhab.binding.homeduino.internal.exceptions.HomeduinoException;
import org.openhab.binding.homeduino.internal.messages.HomeduinoAcknowledgementMessage;
import org.openhab.binding.homeduino.internal.messages.HomeduinoErrorMessage;
import org.openhab.binding.homeduino.internal.messages.HomeduinoEventMessage;
import org.openhab.binding.homeduino.internal.messages.HomeduinoInterfaceMessage;
import org.openhab.binding.homeduino.internal.messages.HomeduinoInterfaceMessageFactory;
import org.openhab.binding.homeduino.internal.messages.HomeduinoMessage;
import org.openhab.binding.homeduino.internal.messages.HomeduinoReadyMessage;
import org.openhab.binding.homeduino.internal.messages.HomeduinoResponseMessage;
import org.openhab.binding.homeduino.internal.messages.homeduino.AbstractHomeduinoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for the Homeduino bridge. Process the input and output of the message
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class HomeduinoBridgeHandler extends BaseBridgeHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeduinoBridgeHandler.class);
    private static final int TIMEOUT = 5000;
    private static final int DEFAULT_REPEAT_COUNT = 3;

    private static HomeduinoInterfaceMessage responseMessage;
    private final HomeduinoEventListener homeduinoEventListener = new HomeduinoMessageListener();

    private final Object notifierObject = new Object();

    private HomeduinoEventListener eventListener;
    private List<DeviceMessageListener> deviceStatusListeners = new CopyOnWriteArrayList<>();
    private HomeduinoSerialConnector connector;
    private HomeduinoBridgeConfiguration configuration;
    private ScheduledFuture<?> connectorTask;

    public HomeduinoBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void initialize() {
        LOGGER.debug("Initializing Homeduino bridge handler");
        updateStatus(ThingStatus.OFFLINE);

        configuration = getConfigAs(HomeduinoBridgeConfiguration.class);

        if (connectorTask == null || connectorTask.isCancelled()) {
            connectorTask = scheduler.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    LOGGER.debug("Checking Homeduino transceiver connection, thing status = {}", thing.getStatus());
                    if (thing.getStatus() != ThingStatus.ONLINE) {
                        connect();
                    }
                }
            }, 0, 60, TimeUnit.SECONDS);
        }
    }

    @Override
    public void dispose() {
        LOGGER.debug("Handler disposed.");

        for (DeviceMessageListener deviceStatusListener : deviceStatusListeners) {
            unregisterDeviceStatusListener(deviceStatusListener);
        }

        if (connector != null) {
            connector.removeEventListener(eventListener);
            connector.disconnect();
        }

        if (connectorTask != null && !connectorTask.isCancelled()) {
            connectorTask.cancel(true);
            connectorTask = null;
        }

        super.dispose();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        LOGGER.debug("Bridge commands not supported.");
    }

    public boolean registerDeviceStatusListener(DeviceMessageListener deviceStatusListener) {
        if (deviceStatusListener == null) {
            throw new IllegalArgumentException("It's not allowed to pass a null deviceStatusListener.");
        }
        return !deviceStatusListeners.contains(deviceStatusListener) && deviceStatusListeners.add(deviceStatusListener);
    }

    public boolean unregisterDeviceStatusListener(DeviceMessageListener deviceStatusListener) {
        if (deviceStatusListener == null) {
            throw new IllegalArgumentException("It's not allowed to pass a null deviceStatusListener.");
        }
        return deviceStatusListeners.remove(deviceStatusListener);
    }

    private List<DeviceMessageListener> getDeviceStatusListeners() {
        return deviceStatusListeners;
    }

    private static synchronized HomeduinoInterfaceMessage getResponseMessage() {
        return responseMessage;
    }

    private static synchronized void setResponseMessage(HomeduinoInterfaceMessage respMessage) {
        responseMessage = respMessage;
    }

    private void connect() {
        if (configuration.serialPort == null) {
            return;
        }

        LOGGER.debug("Connecting to Homeduino transceiver");

        try {
            if (connector == null) {
                connector = new HomeduinoSerialConnector();
            }

            connector.disconnect();
            connector.connect(configuration);
            connector.addEventListener(homeduinoEventListener);

            Thread.sleep(200);

            updateStatus(ThingStatus.ONLINE);

        } catch (Exception e) {
            LOGGER.error("Connection to Homeduino transceiver failed", e);
        } catch (UnsatisfiedLinkError e) {
            LOGGER.error("Error occurred when trying to load native library for OS '{}' version '{}', processor '{}'",
                    System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"), e);
        }
    }

    void sendMessage(AbstractHomeduinoMessage msg) throws HomeduinoException {
        setResponseMessage(null);

        try {
            connector.sendMessage(msg.decodeToHomeduinoMessage(configuration.transmitterPin.intValue(),
                    getRepeats(configuration)));
        } catch (IOException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
            throw new HomeduinoException("Error while sending message", e);
        }
        try {
            HomeduinoInterfaceMessage response;
            synchronized (notifierObject) {
                notifierObject.wait(TIMEOUT);
                response = getResponseMessage();
            }

            if (response instanceof HomeduinoAcknowledgementMessage) {
                LOGGER.debug("Command successfully transmitted, 'ACK' received");
            } else if (response instanceof HomeduinoErrorMessage) {
                LOGGER.error("Command transmit failed, 'ERR' received");
            } else {
                LOGGER.warn("No response received from transceiver");
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
            }

        } catch (InterruptedException ie) {
            LOGGER.error("No acknowledge received from Homeduino controller, TIMEOUT {}ms ", TIMEOUT);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
        }
    }

    private int getRepeats(HomeduinoBridgeConfiguration repeats) {
        return repeats.repeats == null ? DEFAULT_REPEAT_COUNT : repeats.repeats.intValue();
    }

    private class HomeduinoMessageListener implements HomeduinoEventListener {

        @Override
        public void packetReceived(byte[] packet) {
            try {
                HomeduinoInterfaceMessage message = HomeduinoInterfaceMessageFactory.createMessage(packet);

                if (message instanceof HomeduinoReadyMessage) {
                    if (configuration.receiverPin != null) {
                        String configMessage = "RF receive " + configuration.receiverPin;
                        HomeduinoBridgeHandler.this.connector.sendMessage(configMessage);
                    }
                } else if (message instanceof HomeduinoResponseMessage) {
                    LOGGER.debug("Response received: {}", message.toString());
                    setResponseMessage(message);
                    synchronized (notifierObject) {
                        notifierObject.notify();
                    }

                } else if (message instanceof HomeduinoEventMessage) {
                    HomeduinoEventMessage event = (HomeduinoEventMessage) message;

                    for (HomeduinoMessage interpretedMsg : event.getInterpretations()) {
                        for (DeviceMessageListener deviceStatusListener : getDeviceStatusListeners()) {
                            try {
                                deviceStatusListener.onDeviceMessageReceived(getThing().getUID(), interpretedMsg);
                            } catch (Exception e) {
                                LOGGER.error("An exception occurred while calling the DeviceStatusListener", e);
                            }
                        }
                    }
                }
            } catch (HomeduinoException e) {
                LOGGER.error("Error occurred during packet receiving, data: {}",
                        DatatypeConverter.printHexBinary(packet), e);
            } catch (IOException e) {
                LOGGER.error("Error occurred during packet processing", e);
            }

            updateStatus(ThingStatus.ONLINE);
        }

        @Override
        public void errorOccurred(String error) {
            LOGGER.error("Error occurred: {}", error);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
        }
    }
}
