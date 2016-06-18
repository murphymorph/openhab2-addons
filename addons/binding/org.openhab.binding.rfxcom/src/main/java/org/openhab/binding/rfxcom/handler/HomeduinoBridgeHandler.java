package org.openhab.binding.rfxcom.handler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.openhab.binding.rfxcom.internal.DeviceMessageListener;
import org.openhab.binding.rfxcom.internal.config.RFXComBridgeConfiguration;
import org.openhab.binding.rfxcom.internal.connector.HomeduinoConnectorInterface;
import org.openhab.binding.rfxcom.internal.connector.HomeduinoSerialConnector;
import org.openhab.binding.rfxcom.internal.connector.RFXComEventListener;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComNotImpException;
import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.HomeduinoEventMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.HomeduinoMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.HomeduinoMessageFactory;
import org.openhab.binding.rfxcom.internal.messages.homeduino.HomeduinoReadyMessage;
import org.openhab.binding.rfxcom.internal.messages.homeduino.HomeduinoResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeduinoBridgeHandler extends BaseRFXComBridgeHandler {

    private Logger logger = LoggerFactory.getLogger(HomeduinoBridgeHandler.class);

    HomeduinoConnectorInterface connector = null;
    RFXComBridgeConfiguration configuration = null;

    private RFXComEventListener homeduinoEventListener = new HomeduinoMessageListener();

    private static final int timeout = 5000;
    private static HomeduinoResponseMessage responseMessage = null;
    private Object notifierObject = new Object();
    private ScheduledFuture<?> connectorTask;

    public HomeduinoBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing Homeduino bridge handler");
        updateStatus(ThingStatus.OFFLINE);

        configuration = getConfigAs(RFXComBridgeConfiguration.class);

        if (connectorTask == null || connectorTask.isCancelled()) {
            connectorTask = scheduler.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    logger.debug("Checking Homeduino transceiver connection, thing status = {}", thing.getStatus());
                    if (thing.getStatus() != ThingStatus.ONLINE) {
                        connect();
                    }
                }
            }, 0, 60, TimeUnit.SECONDS);
        }
    }

    @Override
    public void dispose() {
        logger.debug("Handler disposed.");

        if (connector != null) {
            connector.removeEventListener(homeduinoEventListener);
            connector.disconnect();
        }

        if (connectorTask != null && !connectorTask.isCancelled()) {
            connectorTask.cancel(true);
            connectorTask = null;
        }

        super.dispose();
    }

    private static synchronized HomeduinoResponseMessage getResponseMessage() {
        return responseMessage;
    }

    private static synchronized void setResponseMessage(HomeduinoResponseMessage respMessage) {
        responseMessage = respMessage;
    }

    @Override
    protected void connect() {
        if (configuration.serialPort == null) {
            return;
        }

        logger.debug("Connecting to Homeduino transceiver");

        try {
            if (connector == null) {
                connector = new HomeduinoSerialConnector();
            }

            connector.disconnect();
            connector.connect(configuration.serialPort, configuration.baudrate.intValue());
            connector.addEventListener(homeduinoEventListener);

            Thread.sleep(200);

            updateStatus(ThingStatus.ONLINE);

        } catch (Exception e) {
            e.printStackTrace();

            logger.error("Connection to RFXCOM transceiver failed: {}", e.getMessage());
        } catch (UnsatisfiedLinkError e) {
            logger.error("Error occured when trying to load native library for OS '{}' version '{}', processor '{}'",
                    System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"), e);
        }
    }

    private class HomeduinoMessageListener implements RFXComEventListener {

        @Override
        public void packetReceived(byte[] packet) {
            try {
                HomeduinoMessage message = HomeduinoMessageFactory.createMessage(packet);
                logger.debug("Message received: {}", message);

                if (message instanceof HomeduinoReadyMessage) {
                    if (configuration.receiverPin != null) {
                        String configMessage = "RF receive " + configuration.receiverPin;
                        HomeduinoBridgeHandler.this.connector.sendMessage(configMessage);
                    }
                } else if (message instanceof HomeduinoResponseMessage) {
                    HomeduinoResponseMessage resp = (HomeduinoResponseMessage) message;

                    logger.debug("Response received: {}", message.toString());
                    setResponseMessage(resp);
                    synchronized (notifierObject) {
                        notifierObject.notify();
                    }

                } else if (message instanceof HomeduinoEventMessage) {
                    HomeduinoEventMessage event = (HomeduinoEventMessage) message;
                    List<RFXComMessage> messages = event.getInterpretations();

                    for (RFXComMessage interprentedMsg : messages) {
                        for (DeviceMessageListener deviceStatusListener : getDeviceStatusListeners()) {
                            try {
                                deviceStatusListener.onDeviceMessageReceived(getThing().getUID(), interprentedMsg);
                            } catch (Exception e) {
                                logger.error("An exception occurred while calling the DeviceStatusListener", e);
                            }
                        }
                    }
                } else {
                    // todo handle this situation
                }
            } catch (RFXComNotImpException e) {
                logger.debug("Message not supported, data: {}", DatatypeConverter.printHexBinary(packet));
            } catch (RFXComException e) {
                logger.error("Error occured during packet receiving, data: {}",
                        DatatypeConverter.printHexBinary(packet), e.getMessage());
            } catch (IOException e) {
                logger.error("Error occured during packet processing", e.getMessage());
            }

            updateStatus(ThingStatus.ONLINE);
        }

        @Override
        public void errorOccured(String error) {
            logger.error("Error occured: {}", error);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
        }
    }

    @Override
    public void sendMessage(RFXComMessage msg) throws RFXComException {
        try {
            connector.sendMessage(HomeduinoEventMessage.decodeMessage(msg));
        } catch (IOException e) {
            throw new RFXComException("Error while sending message", e);
        }
    }
}
