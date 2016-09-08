package org.openhab.binding.rfxcom.internal.messages.homeduino;

public enum PacketTypeHomeduino {
    HOMEDUINO_READY(HomeduinoReadyMessage.class),
    HOMEDUINO_ACK(HomeduinoAcknowledgementMessage.class),
    HOMEDUINO_ERROR(HomeduinoErrorMessage.class),
    HOMEDUINO_RF_EVENT(HomeduinoEventMessage.class),
    UNKNOWN(null);

    private final Class<? extends HomeduinoMessage> messageClazz;

    PacketTypeHomeduino(Class<? extends HomeduinoMessage> clazz) {
        this.messageClazz = clazz;
    }

    public Class<? extends HomeduinoMessage> getMessageClass() {
        return messageClazz;
    }
}
