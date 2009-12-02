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

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Jun-2009
 * Time: 11:56:36
 *
 * A context factory which creates contexts that add a listener with all timeseries added,
 * so that a UDP message can be sent (to a TimeSeriesServer) for each item appended to the series
 *
 * There is a minimum send interval, which may cause items to be skipped if they are appended to the
 * series in quick succession. This is to prevent flooding the network with UDP packets.
 */
public class UdpPublishingContextFactory extends DefaultContextFactory {

    private UdpClient udpClient;
    private int minSendIntervalMillis;
    public static int DEFAULT_MIN_SEND_INTERVAL_MILLIS = 5000;

    public UdpPublishingContextFactory(UdpClient udpClient) {
        this(udpClient, DEFAULT_MIN_SEND_INTERVAL_MILLIS);
    }

    public UdpPublishingContextFactory(UdpClient udpClient, int minSendIntervalMillis) {
        this.udpClient = udpClient;
        this.minSendIntervalMillis = minSendIntervalMillis;
    }

    public TimeSeriesContext createContext(TimeSeriesContext parent, String id, String description) {
        return new UdpPublishingTimeSeriesContext(udpClient, minSendIntervalMillis, parent, id, description);
    }

    private static class UdpPublishingTimeSeriesContext extends DefaultTimeSeriesContext {

        private Map<Identifiable, PublishingTimeSeriesListener> listenersByIdentifiable = new IdentityHashMap<Identifiable, PublishingTimeSeriesListener>();
        private UdpClient udpClient;
        private int minSendIntervalMillis;

        public UdpPublishingTimeSeriesContext(UdpClient udpClient, int minSendIntervalMillis, TimeSeriesContext parent, String id, String description) {
            super(parent, id, description);
            this.udpClient = udpClient;
            this.minSendIntervalMillis = minSendIntervalMillis;
        }

        public TimeSeriesContext addChild(Identifiable... identifiables) {
            synchronized (getTreeLock()) {
                TimeSeriesContext t = super.addChild(identifiables);
                for ( Identifiable i : identifiables) {
                    if ( i instanceof IdentifiableTimeSeries) {
                        PublishingTimeSeriesListener listener = new PublishingTimeSeriesListener(udpClient, (IdentifiableTimeSeries) i, minSendIntervalMillis);
                        listenersByIdentifiable.put(i, listener);
                        ((IdentifiableTimeSeries)i).addTimeSeriesListener(listener);
                    }
                }
                return t;
            }
        }

        public boolean removeChild(Identifiable identifiable) {
            synchronized (getTreeLock()) {
                boolean result = super.removeChild(identifiable);
                if (result) {
                    PublishingTimeSeriesListener l = listenersByIdentifiable.get(identifiable);
                    if ( l != null) {
                        ((IdentifiableTimeSeries)identifiable).removeTimeSeriesListener(l);
                    }
                }
                return result;
            }
        }

    }

    /**
     * Listen for insert events which insert one item and send these as a datagram
     * Only send a datagram if it is at least MIN_DATAGRAM_SEND_INTERVAL millis since the last one
     */
    private static class PublishingTimeSeriesListener extends TimeSeriesListenerAdapter {

        private UdpClient udpClient;
        private IdentifiableTimeSeries i;
        private volatile long lastSentTime;
        private final int minSendIntervalMillis;

        public PublishingTimeSeriesListener(UdpClient udpClient, IdentifiableTimeSeries i, int minSendIntervalMillis) {
            this.udpClient = udpClient;
            this.i = i;
            this.minSendIntervalMillis = minSendIntervalMillis;
        }

        public void itemsAdded(TimeSeriesEvent e) {
            long time = System.currentTimeMillis();
            if ( e.getItems().size() == 1 && (time - lastSentTime) > minSendIntervalMillis) {
                udpClient.sendMessage(new TimeSeriesValueMessage(
                        i.getPath(),
                        i.getDescription(),
                        e.getItems().get(0)
                ));
                lastSentTime = time;
            }
        }
    }

}
