package com.sree.swingengine.util;

import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.Collections;

public final class NetworkUtil {

    private NetworkUtil() {
    }

    public static String getLocalIp() {

        try {

            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {

                if (!networkInterface.isUp()
                        || networkInterface.isLoopback()
                        || networkInterface.isVirtual()) {
                    continue;
                }

                for (var address : Collections.list(networkInterface.getInetAddresses())) {

                    if (address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }

                }

            }

        } catch (Exception e) {
            throw new RuntimeException("Unable to determine local IP", e);
        }

        throw new RuntimeException("No active network interface found.");
    }

    public static String getMacAddress() {

        try {

            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {

                if (!networkInterface.isUp()
                        || networkInterface.isLoopback()
                        || networkInterface.isVirtual()) {
                    continue;
                }

                byte[] mac = networkInterface.getHardwareAddress();

                if (mac == null) {
                    continue;
                }

                StringBuilder builder = new StringBuilder();

                for (int i = 0; i < mac.length; i++) {
                    builder.append(String.format("%02X%s",
                            mac[i],
                            (i < mac.length - 1) ? ":" : ""));
                }

                return builder.toString();
            }

        } catch (Exception e) {
            throw new RuntimeException("Unable to determine MAC Address", e);
        }

        throw new RuntimeException("No MAC Address found.");
    }
}