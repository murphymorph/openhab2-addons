package org.openhab.binding.rfxcom.handler;

import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.rfxcom.internal.messages.RFXComMessage;

class RFXComHandlerMock extends RFXComHandler {
    private RFXComMessage captureMessage;

    RFXComHandlerMock() {
        super(null);
    }

    @Override
    public void onDeviceMessageReceived(ThingUID bridge, RFXComMessage message) {
        captureMessage = message;
    }

    public RFXComMessage getCaptureMessage() {
        return captureMessage;
    }
}
