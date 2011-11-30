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
package com.od.jtimeseries.capture.impl;

import com.od.jtimeseries.capture.CaptureState;
import com.od.jtimeseries.capture.ValueSourceCapture;
import com.od.jtimeseries.source.ValueSource;
import com.od.jtimeseries.source.ValueSourceListener;
import com.od.jtimeseries.timeseries.DefaultTimeSeriesItem;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.util.TimeSeriesExecutorFactory;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.LongNumeric;
import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-Dec-2008
 * Time: 11:53:28
 *
 * Capture values from a source to a timeseries, without aggregation
 */
public class DefaultCapture extends AbstractCapture implements ValueSourceCapture {

    private static final LogMethods logMethods = LogUtils.getLogMethods(DefaultCapture.class);
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
                        addValueToSeries(LongNumeric.valueOf(value));
                    }

                    public void newValue(double value) {
                        addValueToSeries(DoubleNumeric.valueOf(value));
                    }

                    public void newValue(Numeric sourceValue) {
                        addValueToSeries(sourceValue);
                    }
                };
                getValueSource().addValueListener(valueSourceListener);
                changeStateAndFireEvent(CaptureState.STARTED);
            }
        }
    }

    private void addValueToSeries(final Numeric v) {
        TimeSeries timeSeries = getTimeSeries();
        try {
            timeSeries.addItem(new DefaultTimeSeriesItem(System.currentTimeMillis(), v));
            fireCaptureCompleteEvent(v, timeSeries);
        } catch (Throwable t) {
            logMethods.logError("Failed to capture value " + v + " to series " + timeSeries, t);
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
