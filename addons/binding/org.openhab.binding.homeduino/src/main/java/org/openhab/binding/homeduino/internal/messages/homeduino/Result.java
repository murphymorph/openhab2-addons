/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.messages.homeduino;

import org.openhab.binding.homeduino.internal.messages.HomeduinoMessage;

/**
 * Result class for received messages from the Homeduino bridge
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class Result {
    private final int id;
    private final Integer unit;
    private final HomeduinoProtocol protocol;
    private final boolean all;

    private final Integer state;
    private final Integer dimLevel;
    private final Double temperature;
    private final Integer humidity;
    private final boolean lowBattery;

    private Result(Builder builder) {
        this.id = builder.id;
        this.unit = builder.unit;
        this.protocol = builder.protocol;
        this.state = builder.state;
        this.all = builder.all;
        this.dimLevel = builder.dimLevel;
        this.temperature = builder.temperature;
        this.humidity = builder.humidity;
        this.lowBattery = builder.lowBattery;
    }

    public HomeduinoProtocol getProtocol() {
        return protocol;
    }

    public int getId() {
        return id;
    }

    public Integer getUnit() {
        return unit;
    }

    public boolean isAll() {
        return all;
    }

    public Integer getState() {
        return state;
    }

    public Integer getDimLevel() {
        return dimLevel;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public boolean isLowBattery() {
        return lowBattery;
    }

    public HomeduinoMessage getMessage() {
        return getProtocol().constructMessage(this);
    }

    public static class Builder {
        private final HomeduinoProtocol protocol;
        private final int id;
        private final Integer unit;
        private boolean all;
        private Integer state;
        private Integer dimLevel;
        private Double temperature;
        private Integer humidity;
        private boolean lowBattery;

        public Builder(HomeduinoProtocol protocol, int id, int unit) {
            this.protocol = protocol;
            this.id = id;
            this.unit = unit;
        }

        public Builder(HomeduinoProtocol protocol, int id) {
            this.protocol = protocol;
            this.id = id;
            this.unit = null;
        }

        Builder withAll(boolean all) {
            this.all = all;
            return this;
        }

        Builder withState(Integer state) {
            this.state = state;
            return this;
        }

        Builder withDimLevel(Integer dimlevel) {
            this.dimLevel = dimlevel;
            return this;
        }

        Builder withTemperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        Builder withHumidity(int humidity) {
            this.humidity = humidity;
            return this;
        }

        Builder withLowBattery(boolean lowBattery) {
            this.lowBattery = lowBattery;
            return this;
        }

        public Result build() {
            return new Result(this);
        }
    }
}
