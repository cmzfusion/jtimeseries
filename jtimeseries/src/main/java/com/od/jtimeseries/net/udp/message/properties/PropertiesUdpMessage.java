/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.net.udp.message.properties;

import com.od.jtimeseries.net.udp.message.UdpMessage;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 16-May-2009
 * Time: 17:37:58
 *
 * A message formatted as the XML representation of java.util.Properties
 */
class PropertiesUdpMessage extends Properties implements UdpMessage {

    private static final LogMethods logMethods = LogUtils.getLogMethods(PropertiesUdpMessage.class);

    public static final String MESSAGE_TYPE_PROPERTY = "MESSAGE_TYPE";
    public static final String SOURCE_INETADDRESS_KEY = "INETADDRESS";
    public static final String SOURCE_HOSTNAME_KEY = "INETHOST";

    public PropertiesUdpMessage(Properties p) {
        putAll(p);
    }

    public PropertiesUdpMessage(String messageType) {
        setProperty(MESSAGE_TYPE_PROPERTY, messageType);
        String inetAddress = "";
        String hostname = "";
        try {
            inetAddress = InetAddress.getLocalHost().getHostAddress();
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logMethods.logError("Could not find inet address for UdpMessage", e);
        }
        setProperty(SOURCE_INETADDRESS_KEY, inetAddress);
        setProperty(SOURCE_HOSTNAME_KEY, hostname);
    }

    @Override
    public String getInetAddress() {
        return getProperty(SOURCE_INETADDRESS_KEY);
    }

    @Override
    public String getHostname() {
        return getProperty(SOURCE_HOSTNAME_KEY);
    }

    @Override
    public void serialize(OutputStream outputStream) throws IOException {
        storeToXML(outputStream, null);
    }

    @Override
    public void deserialize(InputStream inputStream) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getMaxExpectedSize() {
        return 1024;
    }
}
