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
package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.TimeSeriesEvent;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.TimeSeriesListener;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 22-Jan-2009
 * Time: 12:14:30
 *
 * Utility class used by time series which wrap another time series in order to propagate events from the
 * wrapped series
 *
 * Listeners which register with the wrapper time series will expect the source of events to be the
 * wrapper instance rather than the wrapped time series implementation
 *
 * To achieve this without generating a lot of new event instances we use a proxy event instance which
 * delegates to the methods on the source event, apart from the getSource() method which always returns the wrapping
 * timeseries instance.
 */
public class ProxyTimeSeriesEventHandler extends TimeSeriesListenerSupport implements TimeSeriesListener {

    private Object proxySource;

    private ProxyTimeSeriesEvent proxyEvent = new ProxyTimeSeriesEvent();

    public ProxyTimeSeriesEventHandler(Object proxySource) {
        this.proxySource = proxySource;
    }

    public void itemsAddedOrInserted(TimeSeriesEvent h) {
        ProxyTimeSeriesEvent proxyEvent = getThreadLocalEventAndSetDelegate(h);
        fireItemsAddedOrInserted(proxyEvent);
        proxyEvent.clearDelegateValues(); //don't hold a reference
    }

    public void itemsRemoved(TimeSeriesEvent h) {
        ProxyTimeSeriesEvent proxyEvent = getThreadLocalEventAndSetDelegate(h);
        fireItemsRemoved(proxyEvent);
        proxyEvent.clearDelegateValues(); //don't hold a reference

    }

    public void seriesChanged(TimeSeriesEvent h) {
        ProxyTimeSeriesEvent proxyEvent = getThreadLocalEventAndSetDelegate(h);
        fireSeriesChanged(proxyEvent);
        proxyEvent.clearDelegateValues(); //don't hold a reference
    }

    private ProxyTimeSeriesEvent getThreadLocalEventAndSetDelegate(TimeSeriesEvent h) {
        proxyEvent.setDelegateValues(proxySource, h);
        return proxyEvent;
    }

    public void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * To avoid creating a lot of chaff objects simply to change the apparent source of the event
     * we can reuse the same ThreadLocal event instance to wrap each event as it comes through,
     * delegating all the method calls other than the one which returns the source
     */
    private static class ProxyTimeSeriesEvent extends TimeSeriesEvent {

        private TimeSeriesEvent sourceEvent;
        private Object source;

        public void setDelegateValues(Object source, TimeSeriesEvent event) {
            this.sourceEvent = event;
            this.source = source;
        }

        public void clearDelegateValues() {
            this.source = null;
            this.sourceEvent = null;
        }

        public List<TimeSeriesItem> getItems() {
            return sourceEvent.getItems();
        }

        public long getLastItemTimestamp() {
            return sourceEvent.getLastItemTimestamp();
        }

        public long getFirstItemTimestamp() {
            return sourceEvent.getFirstItemTimestamp();
        }

        public Object getSource() {
            return source;
        }

        public EventType getEventType() {
            return sourceEvent.getEventType();
        }

        public long getSeriesModCount() {
            return sourceEvent.getSeriesModCount();
        }
    }
}
