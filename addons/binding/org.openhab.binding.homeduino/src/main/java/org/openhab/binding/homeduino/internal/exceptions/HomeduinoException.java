/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeduino.internal.exceptions;

/**
 * Exception for Homeduino errors.
 *
 * @author Martin van Wingerden - Initial contribution
 */
public class HomeduinoException extends Exception {
    public HomeduinoException(String message) {
        super(message);
    }

    public HomeduinoException(String message, Throwable cause) {
        super(message, cause);
    }

    public HomeduinoException(Throwable cause) {
        super(cause);
    }

}
