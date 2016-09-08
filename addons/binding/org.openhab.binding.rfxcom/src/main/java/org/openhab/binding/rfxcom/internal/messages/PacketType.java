package org.openhab.binding.rfxcom.internal.messages;

import org.openhab.binding.rfxcom.internal.messages.homeduino.*;

public enum PacketType {
    INTERFACE_CONTROL(0, RFXComControlMessage.class),
    INTERFACE_MESSAGE(1, RFXComInterfaceMessage.class),
    TRANSMITTER_MESSAGE(2, RFXComTransmitterMessage.class),
    UNDECODED_RF_MESSAGE(3, null), // class does not seem to exist

    LIGHTING1(16, RFXComLighting1Message.class),
    LIGHTING2(17, RFXComLighting2Message.class),
    LIGHTING3(18, null), // class does not seem to exist
    LIGHTING4(19, RFXComLighting4Message.class),
    LIGHTING5(20, RFXComLighting5Message.class),
    LIGHTING6(21, RFXComLighting6Message.class),
    CHIME(22, null),
    FAN(23, null),
    CURTAIN1(24, RFXComCurtain1Message.class),
    BLINDS1(25, RFXComBlinds1Message.class),
    RFY(26, RFXComRfyMessage.class),
    SECURITY1(32, RFXComSecurity1Message.class),
    CAMERA1(40, null),
    REMOTE_CONTROL(48, null),
    THERMOSTAT1(64, RFXComThermostat1Message.class),
    THERMOSTAT2(65, null),
    THERMOSTAT3(66, null),
    BBQ1(78, null),
    TEMPERATURE_RAIN(79, null),
    TEMPERATURE(80, RFXComTemperatureMessage.class),
    HUMIDITY(81, RFXComHumidityMessage.class),
    TEMPERATURE_HUMIDITY(82, RFXComTemperatureHumidityMessage.class),
    BAROMETRIC(83, null),
    TEMPERATURE_HUMIDITY_BAROMETRIC(84, null),
    RAIN(85, RFXComRainMessage.class),
    WIND(86, RFXComRainMessage.class),
    UV(87, null),
    DATE_TIME(88, null),
    CURRENT(89, null),
    ENERGY(90, RFXComEnergyMessage.class),
    CURRENT_ENERGY(91, null),
    POWER(92, null),
    WEIGHT(93, null),
    GAS(94, null),
    WATER(95, null),
    RFXSENSOR(112, null),
    RFXMETER(113, null),
    FS20(114, null),
    IO_LINES(128, null),

    HOMEDUINO_SWITCH1(255, RFXComHomeduinoSwitch1Message.class),
    HOMEDUINO_SWITCH4(255, RFXComHomeduinoSwitch4Message.class),
    HOMEDUINO_DIMMER1(255, RFXComHomeduinoDimmer1Message.class),
    HOMEDUINO_PIR1(255, RFXComHomeduinoPir1Message.class),
    HOMEDUINO_SHUTTER3(255, RFXComHomeduinoShutter3Message.class),

    UNKNOWN(255, RFXComLighting1Message.class);

    private final int packetType;
    private final Class<? extends RFXComMessage> messageClazz;

    PacketType(int packetType, Class<? extends RFXComMessage> clazz) {
        this.packetType = packetType;
        this.messageClazz = clazz;
    }

    public Class<? extends RFXComMessage> getMessageClass() {
        return messageClazz;
    }

    public byte toByte() {
        return (byte) packetType;
    }

}