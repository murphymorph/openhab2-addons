package org.openhab.binding.rfxcom.internal.messages.homeduino.protocols;

import java.util.Arrays;

public class HomeduinoShutter3 extends HomeduinoProtocol {
    private static final int PULSE_COUNT = 82;
    private static final int[] PULSE_LENGTHS = { 366, 736, 1600, 5204, 10896 };

    public HomeduinoShutter3() {
        super(PULSE_COUNT, PULSE_LENGTHS);
    }

    @Override
    public Result process(String pulses) {
        pulses = pulses.replace("32", "").replace("14", "").replace("04", "");
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < pulses.length(); i += 2) {
            String pulse = pulses.substring(i, i + 2);
            char c;
            if ("01".equals(pulse)) {
                c = '0';
            } else {
                c = '1';
            }

            output.append(c);
        }

        System.out.println(output);

        int id = Integer.parseInt(output.substring(0, 29).toString(), 2);
        int channel = Integer.parseInt(output.substring(29, 32).toString(), 2);
        int state = Integer.parseInt(output.substring(33, 36).toString(), 2);

        return new Result(id, channel, state, false, null);
    }

    @Override
    public Result process(Pulses pulses) {
        System.out.println(pulses.getPulseCount());
        System.out.println(pulses.getPulses());
        System.out.println(Arrays.toString(pulses.getPulseLengths()));
        System.out.println("got here");

        return process(pulses.getPulses());
    }

}
