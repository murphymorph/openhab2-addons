package org.openhab.binding.rfxcom.internal.messages.homeduino.protocols;

public abstract class HomeduinoCoCo1 extends HomeduinoProtocol {
    private static int[] PULSE_LENGTHS = { 358, 1095, 11244 };
    private static int PULSE_COUNT = 50;

    public HomeduinoCoCo1() {
        super(PULSE_COUNT, PULSE_LENGTHS);
    }

    @Override
    public Result process(String pulses) {
        pulses = pulses.replace("02", "");
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < pulses.length(); i += 4) {
            String pulse = pulses.substring(i, i + 4);
            char c;
            if ("0101".equals(pulse)) {
                c = '1';
            } else {
                c = '0';
            }

            output.append(c);
        }

        int unit = Integer.parseInt(output.substring(0, 5).toString(), 2);
        int id = Integer.parseInt(output.substring(5, 10).toString(), 2);
        int state = 1 - Integer.parseInt(output.substring(11).toString(), 2);

        return new Result(id, unit, state, false, null);
    }
}
