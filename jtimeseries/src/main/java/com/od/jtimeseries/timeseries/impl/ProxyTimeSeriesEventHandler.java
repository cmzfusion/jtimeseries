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
import com.od.jtimeseries.timeseries.TimeSeriesListener;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 22-Jan-2009
 * Time: 12:14:30
 *
 * Utility class used by time series which wrap another time series which
 * stores the actual item data
 *
 * Listeners which register with the wrapper time series will expect the source of events to be the
 * wrapper instance rather than the wrapped time series implementation
 */
public class ProxyTimeSeriesEventHandler extends TimeSeriesListenerSupport implements TimeSeriesListener {

    private Object proxySource;

    public ProxyTimeSeriesEventHandler(Object proxySource) {
        this.proxySource = proxySource;
    }

    public void itemsAddedOrInserted(TimeSeriesEvent h) {
        TimeSeriesEvent e = TimeSeriesEvent.createEvent(proxySource, h.getItems(), h.getEventType(), h.getSeriesModCount());
        fireItemsAddedOrInserted(e);
    }

    public void itemsRemoved(TimeSeriesEvent h) {
        TimeSeriesEvent e = TimeSeriesEvent.createEvent(proxySource, h.getItems(), h.getEventType(), h.getSeriesModCount());
        fireItemsRemoved(e);
    }

    public void seriesChanged(TimeSeriesEvent h) {
        TimeSeriesEvent e = TimeSeriesEvent.createEvent(proxySource, h.getItems(), h.getEventType(), h.getSeriesModCount());
        fireSeriesChanged(e);
    }
}
