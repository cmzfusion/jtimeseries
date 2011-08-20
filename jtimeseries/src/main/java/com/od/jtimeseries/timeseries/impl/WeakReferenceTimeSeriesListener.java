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

import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesListener;
import com.od.jtimeseries.timeseries.TimeSeriesEvent;

import java.lang.ref.WeakReference;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 31-Dec-2008
 * Time: 14:27:17
 *
 * A weak reference listener which can be interposed as a proxy for the
 * main listener, and will allow the main listener implementation to be garbage collected
 */
public class WeakReferenceTimeSeriesListener implements TimeSeriesListener {

    private WeakReference<TimeSeriesListener> timeSeriesListenerDelegate;
    private TimeSeries observedSeries;

    public WeakReferenceTimeSeriesListener(TimeSeries observedSeries, TimeSeriesListener seriesListener) {
        this.observedSeries = observedSeries;
        this.timeSeriesListenerDelegate = new WeakReference<TimeSeriesListener>(seriesListener);
    }

    public void itemsAddedOrInserted(TimeSeriesEvent h) {
        TimeSeriesListener l = timeSeriesListenerDelegate.get();
        if ( l != null ) {
          l.itemsAddedOrInserted(h);
        } else {
            //it should be iterating over a snapshot of the listener list
            //so we don't get a concurrent modification exception
            observedSeries.removeTimeSeriesListener(this);
        }
    }

    public void itemsRemoved(TimeSeriesEvent h) {
        TimeSeriesListener l = timeSeriesListenerDelegate.get();
        if ( l != null ) {
          l.itemsRemoved(h);
        }  else {
            observedSeries.removeTimeSeriesListener(this);
        }
    }

    public void seriesChanged(TimeSeriesEvent e) {
        TimeSeriesListener l = timeSeriesListenerDelegate.get();
        if ( l != null ) {
          l.seriesChanged(e);
        }  else {
            observedSeries.removeTimeSeriesListener(this);
        }
    }

}
