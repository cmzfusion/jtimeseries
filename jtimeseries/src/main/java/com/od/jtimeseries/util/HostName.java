package com.od.jtimeseries.util;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 28/03/12
 * Time: 11:31
 *
 * Find the local hostname once, share the value
 */
public class HostName {

    private static final LogMethods logMethods = LogUtils.getLogMethods(HostName.class);

    private static String hostname = "";

    static {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logMethods.error("Could not find inet address for Utf8MessageFactory", e);
        }
    }

    public static String getLocalHostname() {
        return hostname;
    }
}
