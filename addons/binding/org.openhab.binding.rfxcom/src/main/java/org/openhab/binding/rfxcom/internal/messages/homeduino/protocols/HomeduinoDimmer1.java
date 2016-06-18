package org.openhab.binding.rfxcom.internal.messages.homeduino.protocols;

public class HomeduinoDimmer1 extends HomeduinoCoCo2 {
    private static int PULSE_COUNT = 148;

    public HomeduinoDimmer1() {
        super(PULSE_COUNT);
    }

}
