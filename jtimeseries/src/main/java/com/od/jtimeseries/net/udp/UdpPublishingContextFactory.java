/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
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

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultContextFactory;
import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesEvent;
import com.od.jtimeseries.timeseries.TimeSeriesListenerAdapter;
import com.od.jtimeseries.util.identifiable.Identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Jun-2009
 * Time: 11:56:36
 */
public class UdpPublishingContextFactory extends DefaultContextFactory {

    private UdpClient udpClient;

    public UdpPublishingContextFactory(UdpClient udpClient) {
        this.udpClient = udpClient;
    }

    public TimeSeriesContext createContext(TimeSeriesContext parent, String id, String description) {

        //return a subclass of DefaultTimeSeriesContext which adds a PublishingTimeSeriesListener
        //to all ListTimeSeries added to the context
        return new DefaultTimeSeriesContext(parent, id, description) {

            public TimeSeriesContext addChild(Identifiable... identifiables) {
                return addUdpPublishingListener(identifiables);
            }

            private TimeSeriesContext addUdpPublishingListener(Identifiable... identifiables) {
                TimeSeriesContext t = super.addChild(identifiables);
                for ( Identifiable i : identifiables) {
                    if ( i instanceof IdentifiableTimeSeries) {
                        ((IdentifiableTimeSeries)i).addTimeSeriesListener(
                                new PublishingTimeSeriesListener((IdentifiableTimeSeries)i));
                    }
                }
                return t;
            }
        };
    }

    /**
     * Listen for insert events which insert one item and send these as a datagram
     * Only send a datagram if it is at least MIN_DATAGRAM_SEND_INTERVAL millis since the last one
     */
    private class PublishingTimeSeriesListener extends TimeSeriesListenerAdapter {

        private IdentifiableTimeSeries i;
        private volatile long lastSentTime;
        private final int MIN_DATAGRAM_SEND_INTERVAL = 5000;

        public PublishingTimeSeriesListener(IdentifiableTimeSeries i) {
            this.i = i;
        }

        public void itemsAdded(TimeSeriesEvent e) {
            long time = System.currentTimeMillis();
            if ( e.getItems().size() == 1 && (time - lastSentTime) > MIN_DATAGRAM_SEND_INTERVAL) {
                udpClient.sendMessage(new TimeSeriesValueMessage(
                        i.getContextPath(),
                        i.getDescription(),
                        e.getItems().get(0)
                ));
                lastSentTime = time;
            }
        }
    }

}
