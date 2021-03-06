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

import com.od.jtimeseries.net.udp.message.AnnouncementMessage;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 24-Jun-2009
 * Time: 12:45:00
 * To change this template use File | Settings | File Templates.
 */
abstract class PropertiesAnnouncementMessage extends AbstractPropertiesUdpMessage implements AnnouncementMessage {

    public static final String PORT_KEY = "PORT";
    public static final String DESCRIPTION_KEY = "DESCRIPTION";

    PropertiesAnnouncementMessage(String messageType) {
        super(messageType);
    }

    PropertiesAnnouncementMessage(Properties p) {
        super(p);
    }

    public int getPort() {
        return Integer.parseInt(getProperty(PORT_KEY));
    }

    public String getDescription() {
        return getProperty(DESCRIPTION_KEY);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ! (o instanceof AnnouncementMessage)) return false;
        if (!super.equals(o)) return false;

        AnnouncementMessage that = (AnnouncementMessage) o;

        if (getPort() != that.getPort()) return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null) return false;

        return true;
    }

    public int hashCode() {
        int result = getPort();
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }
}
