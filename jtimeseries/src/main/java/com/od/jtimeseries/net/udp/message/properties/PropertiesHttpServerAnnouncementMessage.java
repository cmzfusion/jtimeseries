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

import com.od.jtimeseries.net.udp.message.HttpServerAnnouncementMessage;
import com.od.jtimeseries.net.udp.message.MessageType;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 13-Jan-2009
 * Time: 12:22:13
 *
 * Annouces that a Http timeseries server exists on the given host/port
 */
public class PropertiesHttpServerAnnouncementMessage extends PropertiesAnnouncementMessage implements HttpServerAnnouncementMessage {

    public static final String MESSAGE_TYPE = "HttpServerAnnouncementMessage";

    PropertiesHttpServerAnnouncementMessage(int httpPort, String serverDescription) {
        super(MESSAGE_TYPE);
        setProperty(PORT_KEY, String.valueOf(httpPort));
        setProperty(DESCRIPTION_KEY, serverDescription);
    }

    PropertiesHttpServerAnnouncementMessage(Properties p) {
        super(p);
    }

    public MessageType getMessageType() {
        return MessageType.SERVER_ANNOUNCE;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ! ( o instanceof HttpServerAnnouncementMessage)) return false;
        if (!super.equals(o)) return false;
        return true;
    }

    public String toString() {
        return getClass().getSimpleName() + "{" +
                super.toString() +
                "} ";
    }
}
