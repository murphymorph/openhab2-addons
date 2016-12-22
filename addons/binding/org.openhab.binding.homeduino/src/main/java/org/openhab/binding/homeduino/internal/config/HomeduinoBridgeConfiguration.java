/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.config;

import java.math.BigDecimal;

/**
 * Configuration class for the Homeduino bridge
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class HomeduinoBridgeConfiguration {
    public String serialPort;
    public BigDecimal baudrate;
    public BigDecimal transmitterPin;
    public BigDecimal receiverPin;
    public BigDecimal repeats;
}