package org.openhab.binding.rfxcom.handler;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.config.RFXComBridgeConfiguration;

public class HomeduinoBridgeHandlerTest {
    private static final String RF_EVENT = "RF receive 280 2804 1364 10776 0 0 0 0 010002020000020002000200020200020000020200000200020200000200020002020002000200020002000200020002000200000200020002000200020002000203";

    private HomeduinoBridgeHandler subject = new HomeduinoBridgeHandler(null);
    private HomeduinoConnectorMock connector;

    @Before
    public void setUp() {
        connector = new HomeduinoConnectorMock();
    }

    @Test
    public void testConnect() throws Exception {
        constructSubject(defaultProperties(false));

        subject.connect();
        subject.dispose();

        Assert.assertThat(connector.isConnectCalled(), Is.is(false));
        Assert.assertThat(connector.isConnect2Called(), Is.is(true));

        List<String> receivedMessages = connector.getReceivedMessages();
        Assert.assertThat(receivedMessages.size(), Is.is(0));
    }

    @Test
    public void testConnectWithReceiver() throws Exception {
        constructSubject(defaultProperties(true));

        subject.connect();
        subject.dispose();

        Assert.assertThat(connector.isConnectCalled(), Is.is(false));
        Assert.assertThat(connector.isConnect2Called(), Is.is(true));

        List<String> receivedMessages = connector.getReceivedMessages();
        Assert.assertThat(receivedMessages.size(), Is.is(1));
        Assert.assertThat(receivedMessages.get(0), Is.is("RF receive 1"));
    }

    @Test
    public void testReceiveMessage() throws Exception {
        constructSubject(defaultProperties(true));

        subject.connect();
        Thing thing = null;
        subject.registerDeviceStatusListener(new RFXComHandler(thing));

        connector.mockReceiveMessage(RF_EVENT);
        // home
    }

    private RFXComBridgeConfiguration defaultProperties(boolean withReceiver) {
        RFXComBridgeConfiguration configuration = new RFXComBridgeConfiguration();
        configuration.serialPort = "test";
        configuration.baudrate = BigDecimal.ONE;

        if (withReceiver) {
            configuration.receiverPin = BigDecimal.ONE;
        }
        return configuration;
    }

    private void constructSubject(RFXComBridgeConfiguration configuration) {
        subject = new HomeduinoBridgeHandler(null);
        subject.connector = connector;
        subject.configuration = configuration;
        ThingHandlerCallback callback = new ThingHandlerCallbackStub();
        subject.setCallback(callback);
    }

    private static class ThingHandlerCallbackStub implements ThingHandlerCallback {

        @Override
        public void stateUpdated(ChannelUID channelUID, State state) {
            // TODO Auto-generated method stub

        }

        @Override
        public void postCommand(ChannelUID channelUID, Command command) {
            // TODO Auto-generated method stub

        }

        @Override
        public void statusUpdated(Thing thing, ThingStatusInfo thingStatus) {
            // TODO Auto-generated method stub

        }

        @Override
        public void thingUpdated(Thing thing) {
            // TODO Auto-generated method stub

        }

        @Override
        public void configurationUpdated(Thing thing) {
            // TODO Auto-generated method stub

        }

        @Override
        public void changeThingType(Thing thing, ThingTypeUID thingTypeUID, Configuration configuration) {
            // TODO Auto-generated method stub

        }
    }

}
