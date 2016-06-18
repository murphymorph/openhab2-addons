package org.openhab.binding.rfxcom.handler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.rfxcom.internal.DeviceMessageListener;
import org.openhab.binding.rfxcom.internal.config.RFXComBridgeConfiguration;
import org.openhab.binding.rfxcom.internal.connector.RFXComConnectorInterface;
import org.openhab.binding.rfxcom.internal.connector.RFXComEventListener;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseRFXComBridgeHandler extends BaseBridgeHandler {

    private Logger logger = LoggerFactory.getLogger(BaseRFXComBridgeHandler.class);

    RFXComConnectorInterface connector = null;

    private RFXComEventListener eventListener;
    private List<DeviceMessageListener> deviceStatusListeners = new CopyOnWriteArrayList<>();
    protected RFXComBridgeConfiguration configuration = null;
    private ScheduledFuture<?> connectorTask;

    public BaseRFXComBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void dispose() {
        logger.debug("Handler disposed.");

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
    public void initialize() {
        logger.debug("Initializing RFXCOM bridge handler");
        updateStatus(ThingStatus.OFFLINE);

        configuration = getConfigAs(RFXComBridgeConfiguration.class);

        if (connectorTask == null || connectorTask.isCancelled()) {
            connectorTask = scheduler.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    logger.debug("Checking RFXCOM transceiver connection, thing status = {}", thing.getStatus());
                    if (thing.getStatus() != ThingStatus.ONLINE) {
                        connect();
                    }
                }
            }, 0, 60, TimeUnit.SECONDS);
        }
    }

    protected void setMessageListener(RFXComEventListener eventListener) {
        this.eventListener = eventListener;
    }

    protected RFXComEventListener getEventListener() {
        return eventListener;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Bridge commands not supported.");
    }

    public boolean registerDeviceStatusListener(DeviceMessageListener deviceStatusListener) {
        if (deviceStatusListener == null) {
            throw new IllegalArgumentException("It's not allowed to pass a null deviceStatusListener.");
        }
        return deviceStatusListeners.add(deviceStatusListener);
    }

    public boolean unregisterDeviceStatusListener(DeviceMessageListener deviceStatusListener) {
        if (deviceStatusListener == null) {
            throw new IllegalArgumentException("It's not allowed to pass a null deviceStatusListener.");
        }
        return deviceStatusListeners.remove(deviceStatusListener);
    }

    protected List<DeviceMessageListener> getDeviceStatusListeners() {
        return deviceStatusListeners;
    }

    abstract protected void connect();

    abstract public void sendMessage(RFXComMessage msg) throws RFXComException;
}
