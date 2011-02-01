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
package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.TimeSeriesEvent;
import com.od.jtimeseries.timeseries.TimeSeriesListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class TimeSeriesListenerSupport {

    private final List<TimeSeriesListener> timeSeriesListeners = Collections.synchronizedList(new ArrayList<TimeSeriesListener>());

    public void addTimeSeriesListener(TimeSeriesListener l) {
        synchronized (timeSeriesListeners) {
            if ( ! timeSeriesListeners.contains(l)) {
                timeSeriesListeners.add(l);
            }
        }
    }

    public void removeTimeSeriesListener(TimeSeriesListener l) {
        synchronized (timeSeriesListeners) {
            timeSeriesListeners.remove(l);
        }
    }

    public void fireItemsRemoved(TimeSeriesEvent e) {
        TimeSeriesListener[] listeners = getListenerSnapshot();
        for (TimeSeriesListener l : listeners) {
            l.itemsRemoved(e);
        }
    }

    public void fireItemsAddedOrInserted(TimeSeriesEvent e) {
        TimeSeriesListener[] listeners = getListenerSnapshot();
        for (TimeSeriesListener l : listeners) {
            l.itemsAddedOrInserted(e);
        }
    }

    public void fireItemsChanged(TimeSeriesEvent e) {
        TimeSeriesListener[] listeners = getListenerSnapshot();
        for (TimeSeriesListener l : listeners) {
            l.itemsChanged(e);
        }
    }

    public void fireSeriesChanged(TimeSeriesEvent e) {
        TimeSeriesListener[] listeners = getListenerSnapshot();
        for (TimeSeriesListener l : listeners) {
            l.seriesChanged(e);
        }
    }

    private TimeSeriesListener[] getListenerSnapshot() {
        TimeSeriesListener[] listeners;
        synchronized (timeSeriesListeners) {
            listeners = timeSeriesListeners.toArray(new TimeSeriesListener[timeSeriesListeners.size()]);
        }
        return listeners;
    }
}
