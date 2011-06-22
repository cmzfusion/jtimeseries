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

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 16-May-2009
 * Time: 17:40:13
 * To change this template use File | Settings | File Templates.
 */
public class MessageFactory {

    public UdpMessage getMessage(Properties p) {
        String messageType = p.getProperty(UdpMessage.MESSAGE_TYPE_PROPERTY);
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
