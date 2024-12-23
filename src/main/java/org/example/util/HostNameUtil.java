package org.example.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostNameUtil {
    private static final String UNKNOWN_HOST = "unknown-host";
    public static String getHostName() {
        try {
            // Retrieve the local machine's InetAddress
            InetAddress inetAddress = InetAddress.getLocalHost();

            // Get the hostname of the local machine
            return inetAddress.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return UNKNOWN_HOST;
        }
    }
}
