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
package com.od.jtimeseries.capture.impl;

import com.od.jtimeseries.capture.Capture;
import com.od.jtimeseries.capture.CaptureState;
import com.od.jtimeseries.capture.ValueSourceCapture;
import com.od.jtimeseries.source.ValueSource;
import com.od.jtimeseries.source.ValueSourceListener;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.LongNumeric;
import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-Dec-2008
 * Time: 11:53:28
 */
public class DefaultCapture extends AbstractCapture implements Capture, ValueSourceCapture {

    private ValueSourceListener valueSourceListener;
    private static final Object startStopLock = new Object();

    public DefaultCapture(String id, ValueSource source, IdentifiableTimeSeries timeSeries) {
        super(id, "Capture to timeSeries " + timeSeries.getId() + " from " + source.getId(), timeSeries, source);
    }

    public void start() {
        synchronized (startStopLock) {
            if ( getState() == CaptureState.STOPPED) {
                valueSourceListener = new ValueSourceListener() {
                    public void newValue(long value) {
                        getTimeSeries().append(new TimeSeriesItem(System.currentTimeMillis(), new LongNumeric(value)));
                    }

                    public void newValue(double value) {
                        getTimeSeries().append(new TimeSeriesItem(System.currentTimeMillis(), new DoubleNumeric(value)));
                    }

                    public void newValue(Numeric sourceValue) {
                        getTimeSeries().append(new TimeSeriesItem(System.currentTimeMillis(), sourceValue));
                    }
                };
                getValueSource().addValueListener(valueSourceListener);
                changeStateAndFireEvent(CaptureState.STARTED);
            }
        }
    }

    public void stop() {
        synchronized (startStopLock) {
            if ( getState() == CaptureState.STARTED ) {
                getValueSource().removeValueListener(valueSourceListener);
                valueSourceListener = null;
                changeStateAndFireEvent(CaptureState.STOPPED);
            }
        }
    }

    public String toString() {
        return getDescription();
    }

}
