package org.openhab.binding.rfxcom.internal.messages.homeduino.protocols;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HomeduinoCoCo2 extends HomeduinoProtocol {
    private Logger logger = LoggerFactory.getLogger(HomeduinoCoCo2.class);

    private static int[] PULSE_LENGTHS = { 260, 1300, 2700, 10400 };
    private int pulseCount;

    public HomeduinoCoCo2(int pulseCount) {
        super(pulseCount, PULSE_LENGTHS);
        this.pulseCount = pulseCount;
    }

    @Override
    public Result process(String pulses) {
        System.out.println(pulses);
        pulses = pulses.replace("02", "").replace("03", "");

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < pulses.length(); i += 4) {
            String pulse = pulses.substring(i, i + 4);
            char c;
            if ("0100".equals(pulse)) {
                c = '1';
            } else if ("0000".equals(pulse)) {
                c = 'N';
            } else {
                c = '0';
            }

            output.append(c);
        }

        int id = Integer.parseInt(output.substring(0, 26).toString(), 2);
        boolean all = Character.getNumericValue(output.charAt(26)) == 1;
        char stateChar = output.charAt(27);
        Integer state = null;
        if (stateChar != 'N') {
            state = Character.getNumericValue(stateChar);
        }
        int unit = Integer.parseInt(output.substring(28, 32).toString(), 2);
        Integer dimlevel = null;
        if (pulseCount > 132) {
            dimlevel = Integer.parseInt(output.substring(32).toString(), 2);
        }

        logger.warn("all: " + all);
        logger.warn("state: " + state); // could also indicate presence

        logger.warn("Id: " + id);
        logger.warn("Unitcode: " + unit);
        logger.warn("Dim level: " + dimlevel); // not yet a percentage

        return new Result(id, unit, state, all, dimlevel);
    }

    @Override
    public Result process(Pulses pulses) {
        return process(pulses.getPulses());
    }
}
