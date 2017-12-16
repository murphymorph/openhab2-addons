/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ipp.internal.discovery;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jmdns.ServiceInfo;

import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.config.discovery.mdns.MDNSDiscoveryParticipant;
import org.openhab.binding.ipp.IppBindingConstants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Discovers ipp printers announced by mDNS
 *
 * @author Tobias Bräutigam - Initial Contribution
 * @author Martin van Wingerden - Added support for ipv6
 */
@Component(immediate = true)
public class IppPrinterDiscoveryParticipant implements MDNSDiscoveryParticipant {

    private Logger logger = LoggerFactory.getLogger(IppPrinterDiscoveryParticipant.class);

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return Collections.singleton(IppBindingConstants.PRINTER_THING_TYPE);
    }

    @Override
    public String getServiceType() {
        return "_ipp._tcp.local.";
    }

    @Override
    public ThingUID getThingUID(ServiceInfo service) {
        if (service != null) {
            logger.trace("ServiceInfo: {}", service);
            if (service.getType() != null) {
                if (service.getType().equals(getServiceType())) {
                    String uidName = getUIDName(service);
                    return new ThingUID(IppBindingConstants.PRINTER_THING_TYPE, uidName);
                }
            }
        }
        return null;
    }

    private String getUIDName(ServiceInfo service) {
        return service.getName().replaceAll("[^A-Za-z0-9_]", "_");
    }

    private InetAddress getIpAddress(ServiceInfo service) {
        Inet4Address[] inet4Addresses = service.getInet4Addresses();
        if (inet4Addresses.length > 0) {
            return inet4Addresses[0];
        }
        // Fallback for Inet6addresses
        Inet6Address[] inet6Addresses = service.getInet6Addresses();
        if (inet6Addresses.length > 0) {
            return inet6Addresses[0];
        }
        return null;
    }

    @Override
    public DiscoveryResult createResult(ServiceInfo service) {
        DiscoveryResult result;

        ThingUID uid = getThingUID(service);
        InetAddress ip = getIpAddress(service);
        String rp = service.getPropertyString("rp");

        if (uid != null && ip != null && rp != null) {
            Map<String, Object> properties = new HashMap<>(2);

            String label = service.getName();

            properties.put(IppBindingConstants.PRINTER_PARAMETER_URL, getUrl(ip, service.getPort(), rp));
            properties.put(IppBindingConstants.PRINTER_PARAMETER_NAME, label);

            result = DiscoveryResultBuilder.create(uid).withProperties(properties).withLabel(label).build();
            logger.debug("Created a DiscoveryResult {} for ipp printer on host '{}' name '{}'", result,
                    properties.get(IppBindingConstants.PRINTER_PARAMETER_URL), label);
            return result;
        } else {
            return null;
        }
    }

    private String getUrl(InetAddress inetAddress, int port, String rp) {
        String hostAddress = inetAddress.getHostAddress();
        if (inetAddress instanceof Inet6Address) {
            hostAddress = "[" + hostAddress + "]";
        }
        return "http://" + hostAddress + ":" + port + "/" + rp;
    }
}
