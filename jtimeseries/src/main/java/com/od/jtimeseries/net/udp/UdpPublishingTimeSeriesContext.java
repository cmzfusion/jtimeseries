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

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultContextFactory;
import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.identifiable.IdentifiablePathUtils;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 19-Feb-2010
* Time: 23:14:35
* To change this template use File | Settings | File Templates.
*
* A context which adds listener to child timeseries which sends a UDP datagram when values are added to the series
* By default, child contexts created by this context will also be UdpPublishingTimeSeriesContext
*/
public class UdpPublishingTimeSeriesContext extends DefaultTimeSeriesContext {

    private Map<Identifiable, UdpPublishingListener.UdpPublishingTimeSeriesListener> listenersByIdentifiable = new IdentityHashMap<Identifiable, UdpPublishingListener.UdpPublishingTimeSeriesListener>();
    private UdpClient udpClient;
    private int minSendIntervalMillis;

    /**
     * Create a UdpPublishingTimeSeriesContext as a root context 
     */
    public UdpPublishingTimeSeriesContext(UdpClient udpClient) {
        this(udpClient, UdpPublishingListener.DEFAULT_MIN_SEND_INTERVAL_MILLIS, IdentifiablePathUtils.DEFAULT_ROOT_CONTEXT_ID, IdentifiablePathUtils.DEFAULT_ROOT_CONTEXT_ID, true);
    }

    public UdpPublishingTimeSeriesContext(UdpClient udpClient, String id, String description) {
        this(udpClient, UdpPublishingListener.DEFAULT_MIN_SEND_INTERVAL_MILLIS, id, description, false);
    }

    public UdpPublishingTimeSeriesContext(UdpClient udpClient, int minSendIntervalMillis, String id, String description, boolean rootContext) {
        super(id, description, rootContext);
        this.udpClient = udpClient;
        this.minSendIntervalMillis = minSendIntervalMillis;
        setContextFactory(new UdpPublishingContextFactory());
    }

    public TimeSeriesContext addChild_Locked(Identifiable... identifiables) {
        TimeSeriesContext t = super.addChild_Locked(identifiables);
        for ( Identifiable i : identifiables) {
            if ( i instanceof IdentifiableTimeSeries) {
                UdpPublishingListener.UdpPublishingTimeSeriesListener listener = new UdpPublishingListener.UdpPublishingTimeSeriesListener(udpClient, (IdentifiableTimeSeries) i, minSendIntervalMillis);
                listenersByIdentifiable.put(i, listener);
                ((IdentifiableTimeSeries)i).addTimeSeriesListener(listener);
            }
        }
        return t;
    }

    public boolean removeChild_Locked(Identifiable identifiable) {
        boolean result = super.removeChild_Locked(identifiable);
        if (result) {
            UdpPublishingListener.UdpPublishingTimeSeriesListener l = listenersByIdentifiable.get(identifiable);
            if ( l != null) {
                ((IdentifiableTimeSeries)identifiable).removeTimeSeriesListener(l);
            }
        }
        return result;
    }

    public class UdpPublishingContextFactory extends DefaultContextFactory {

        @Override
        public TimeSeriesContext createContext(TimeSeriesContext parent, String id, String description, Class classType, Object... parameters) {
            TimeSeriesContext result;
            if ( classType.isAssignableFrom(UdpPublishingTimeSeriesContext.class)) {
                result = new UdpPublishingTimeSeriesContext(udpClient, minSendIntervalMillis, id, description, false);
            } else {
                result = super.createContext(parent, id, description, classType, parameters);
            }
            return result;
        }

    }
}
