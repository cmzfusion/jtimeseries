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
import com.od.jtimeseries.capture.TimedCapture;
import com.od.jtimeseries.capture.ValueSourceCapture;
import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.source.ValueSource;
import com.od.jtimeseries.source.ValueSourceListener;
import com.od.jtimeseries.timeseries.DefaultTimeSeriesItem;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.util.TimeSeriesExecutorFactory;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.time.TimePeriod;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 04-Feb-2009
 * Time: 22:37:28
 *
 * A Capture which subscribes to values from a ValueSource, collects values over a given time period and aggregates them
 * using an aggregation function, before adding the aggregated value to a time series.
 *
 * e.g. We may set up a timed capture to record the mean of the values from a value source every five minutes.
 *
 * Some implementation notes:
 *
 * It is very important not to block the value source thread sending us the new values for longer than is
 * absolutely necessary. This thread is controlled by the application using jtimeseries - not the library itself -
 * it may be performance sensitive
 *
 * Some notes on this implementation:
 *
 * - does not add listener to value source until started.
 *   (No point in adding the overhead of observation unless we are actually going to capture values)
 *
 * - During each capture period stores values into a new instance of the aggregate function
 *   When the timer triggers we switch the instances and then perform the aggregate function calculation on the
 *   old function instance, once we have released the lock - this means we never block the value source thread
 *   on the function lock for longer than it takes to do the switch
 *
 * - Initially when started we record values into a dummy function instance
 *   We never capture any values for this dummy instance into the time series - the real capture starts
 *   when the first timer initialTriggerReceived function switch takes place.
 *   This solves the problem where we are 'started' half way through a scheduler time period, for schedulers
 *   which are already running and which group and execute together all captures with the same period
 *
 * - Scheduling and calculation take place on separate threads. A thread from sheduling thread pool switches the
 *   initiates the capture and switches the function instance. The actual work of calculating a value and adding to the
 *   series takes place on a thread from capture calculation pool, so long calculations don't affect the timeliness
 *   of scheduling for other captures
 */
public class DefaultTimedCapture extends AbstractCapture implements TimedCapture, ValueSourceCapture {

    private static final LogMethods logMethods = LogUtils.getLogMethods(DefaultTimedCapture.class);

    private AtomicBoolean initialTriggerReceived = new AtomicBoolean();
    private CaptureFunction captureFunction;
    private AggregateFunction function;
    private final Object functionLock = new Object();
    private ValueSourceListener valueListener;


    public DefaultTimedCapture(String id, ValueSource source, IdentifiableTimeSeries timeSeries, CaptureFunction captureFunction) {
        super(id, "Capture " + captureFunction.getDescription() + " to timeSeries " + timeSeries.getId() + " from " + source.getId() + " every " + captureFunction.getCapturePeriod(), timeSeries, source);
        this.captureFunction = captureFunction;
        this.function = captureFunction.nextFunctionInstance(captureFunction.getPrototypeFunction());
    }

    public TimePeriod getTimePeriod() {
        return getCaptureFunction().getCapturePeriod();
    }

    public void trigger(long timestamp) {
        if ( getState() == CaptureState.STARTED || getState() == CaptureState.STARTING) {
            AggregateFunction oldFunctionInstance;

            boolean isFirstTrigger;

            //hold the lock while we switch functions, so that the current function becomes a new instance but we
            //keep a reference to the old, which contains any values collected during the last period
            synchronized (functionLock) {
                isFirstTrigger = ! initialTriggerReceived.getAndSet(true);
                oldFunctionInstance = this.function;
                function = getCaptureFunction().nextFunctionInstance(oldFunctionInstance);
                if ( isFirstTrigger ) {
                    //this was the first trigger, we are now writing into a real function instance
                    //which means we can transition from STARTING to STARTED
                    changeStateAndFireEvent(CaptureState.STARTED);
                }
            }

            //the first trigger starts the capture period, before then we may have values added
            //to function but may get triggered at any time, so we don't capture anything until the
            //second trigger, the end of the first complete period
            if ( ! isFirstTrigger ) {
                //do the aggregate calculation on the old function instance, while we are not holding the functionlock
                //otherwise, the new values thread from the data source will be blocked waiting for the aggregate calculation to be performed
                //this would be very bad, since that thread may be very performance sensitive
                //this way the source is free to update the new function instance while the calculation takes place on a calculation
                //subthread / the old function instance
                queueCaptureCalculation(timestamp, oldFunctionInstance);
            }

            //fire an event to tell observers this timed capture has been initialTriggerReceived by the scheduler
            fireTriggerEvent();
        }
    }

    private void queueCaptureCalculation(final long timestamp, final AggregateFunction oldFunctionInstance) {
        Runnable runnable = new Runnable() {
            public void run() {
                TimeSeries timeSeries = getTimeSeries();
                Numeric value = null;
                try {
                    value = oldFunctionInstance.calculateResult();
                    timeSeries.addItem(
                        new DefaultTimeSeriesItem(timestamp, value)
                    );
                    fireCaptureCompleteEvent(value, timeSeries);
                } catch (Throwable t) {
                    logMethods.logDebug("Failed to calculate and add new value " + value + " to timeseries " + timeSeries + " using function " + oldFunctionInstance, t);
                }
            }
        };
        TimeSeriesExecutorFactory.getCaptureProcessingExecutor(this).execute(runnable);
    }

    public void start() {
        synchronized (functionLock) {
            if ( getState() == CaptureState.STOPPED) {
                //although we set the listener here the function we are writing to should be dummyFunction
                //until the first timerTrigger takes place, and we move from STARTING to STARTED
                valueListener = new FunctionCallingSourceListener();
                getValueSource().addValueListener(valueListener);
                changeStateAndFireEvent(CaptureState.STARTING);
            }
        }
    }

    public void stop() {
        synchronized (functionLock) {
            if ( getState() == CaptureState.STARTED ) {
                getValueSource().removeValueListener(valueListener);
                valueListener = null;
                initialTriggerReceived.getAndSet(false);
                changeStateAndFireEvent(CaptureState.STOPPED);
            }
        }
    }


    public CaptureFunction getCaptureFunction() {
        return captureFunction;
    }

    //call the function when the value source produces a new value
    private class FunctionCallingSourceListener implements ValueSourceListener {

        public void newValue(long value) {
            synchronized( functionLock) {
                function.addValue(value);
            }
        }

        public void newValue(double value) {
            synchronized( functionLock) {
                function.addValue(value);
            }
        }

        public void newValue(Numeric sourceValue) {
            synchronized (functionLock) {
                function.addValue(sourceValue);
            }
        }
    }
}
