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
package com.od.jtimeseries.net.udp;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 16-May-2009
 * Time: 17:40:13
 *
 * Message factory with support for parsing message properties xml using regular expressions
 * which is more efficient than the built in Properties xml decoder
 */
class MessageFactory {

    private Pattern pattern = Pattern.compile("key=\"(\\w*?)\">([^<]*?)</entry>");
    private String lastMessageType;

    private ThreadLocal<Properties> threadLocalProperties = new ThreadLocal<Properties>() {
        public Properties initialValue() {
            return new Properties();
        }
    };

    public UdpMessage getMessage(String propertiesXml) {
        Properties p = threadLocalProperties.get();
        UdpMessage result = null;
        try {
            Matcher m = pattern.matcher(propertiesXml);
            while ( m.find() ) {
                p.put(m.group(1), m.group(2));
            }
            result = getMessage(p);
        } finally {
            p.clear();
        }
        return result;
    }

    public String getLastMessageType() {
        return lastMessageType;
    }

    public UdpMessage getMessage(Properties p) {
        String messageType = p.getProperty(UdpMessage.MESSAGE_TYPE_PROPERTY);
        lastMessageType = messageType;
        UdpMessage result = null;
        if ( messageType != null) {
            if ( messageType.equals(HttpServerAnnouncementMessage.MESSAGE_TYPE)) {
                result = new HttpServerAnnouncementMessage(p);
            } else if ( messageType.equals(TimeSeriesValueMessage.MESSAGE_TYPE)) {
                result = new TimeSeriesValueMessage(p);
            } else if ( messageType.equals(ClientAnnouncementMessage.MESSAGE_TYPE)) {
                result = new ClientAnnouncementMessage(p);
            }
        }
        return result;
    }
}
