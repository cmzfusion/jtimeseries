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
package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.net.udp.UdpPingTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.swing.eventbus.EventSender;
import com.od.swing.eventbus.UIEventBus;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 11-Dec-2010
 * Time: 21:52:10
 * To change this template use File | Settings | File Templates.
 *
 * TimeSeriesServerDictionary which notified listeners via UIEventBus
 * when server are added or removed
 */
public class UiTimeSeriesServerDictionary extends UdpPingTimeSeriesServerDictionary {

    protected void serverAdded(final TimeSeriesServer s) {
        UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
            new EventSender<TimeSeriousBusListener>() {
                public void sendEvent(TimeSeriousBusListener listener) {
                    listener.serverAdded(s);
                }
            }
        );
    }

    protected void serverRemoved(final TimeSeriesServer s) {
        UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
            new EventSender<TimeSeriousBusListener>() {
                public void sendEvent(TimeSeriousBusListener listener) {
                    listener.serverRemoved(s);
                }
            }
        );
    }

}
