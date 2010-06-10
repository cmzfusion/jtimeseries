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
package com.od.jtimeseries.context;

import com.od.jtimeseries.capture.Capture;
import com.od.jtimeseries.capture.CaptureFactory;
import com.od.jtimeseries.capture.TimedCapture;
import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.scheduling.Scheduler;
import com.od.jtimeseries.source.*;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.time.TimePeriod;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 17-Dec-2008
 * Time: 17:25:10
 *
 * A context for storing and creating timeseries
 *
 * TimeSeriesContext has several convenience methods such as createValueRecorder() which allow the user to create
 * a value source, a capture and a time series within the context in one method call.
 *
 * An example is the easiest way to illustrate this:
 *
 * <pre>
 * e.g.
 * ValueRecorder vr = context.newValueRecorder("Memory", "Memory Usage", CaptureFunctions.MEAN(Time.mins(5));
 * vr.newValue(10);
 * vr.newValue(20);
 * ...
 * </pre>
 *
 * The above would create a ValueRecorder which can be used to store values, a TimedCapture to aggregate
 * the values every five minutes using the MEAN function, and TimeSeries which stores the mean values from the function.
 * If you wish to store raw values from the valueRecorder, without performing any aggregation, do not specify a CaptureFunction,
 * or use the special function CaptureFunctions.RAW_VALUES
 */
public interface TimeSeriesContext extends Identifiable, ContextQueries {

    <E extends Identifiable> E create(String id, String description, Class<E> clazz, Object... parameters);

    TimeSeriesContext getParent();

    TimeSeriesContext getRoot();

    List<ValueSource> getSources();

    List<Capture> getCaptures();

    List<TimeSeriesContext> getChildContexts();

    List<IdentifiableTimeSeries> getTimeSeries();

    TimeSeriesContext addChild(Identifiable... identifiables);

    IdentifiableTimeSeries getTimeSeries(String path);

    ValueSource getSource(String path);

    TimeSeriesContext getContext(String path);

    Capture getCapture(String path);

    Scheduler getScheduler();

    TimeSeriesContext setScheduler(Scheduler scheduler);

    boolean isSchedulerStarted();

    TimeSeriesContext startScheduling();

    TimeSeriesContext stopScheduling();

    TimeSeriesContext startDataCapture();

    TimeSeriesContext stopDataCapture();
    
    TimeSeriesContext setValueSourceFactory(ValueSourceFactory sourceFactory);

    ValueSourceFactory getValueSourceFactory();

    TimeSeriesContext setTimeSeriesFactory(TimeSeriesFactory seriesFactory);

    TimeSeriesFactory getTimeSeriesFactory();

    TimeSeriesContext setCaptureFactory(CaptureFactory captureFactory);

    CaptureFactory getCaptureFactory();

    TimeSeriesContext setContextFactory(ContextFactory contextFactory);

    ContextFactory getContextFactory();

    TimeSeriesContext createContext(String path);

    TimeSeriesContext createContext(String path, String description);

    IdentifiableTimeSeries createTimeSeries(String path, String description);

    Capture createCapture(String path, ValueSource source, IdentifiableTimeSeries series);

    TimedCapture createTimedCapture(String path, ValueSource source, IdentifiableTimeSeries series, CaptureFunction captureFunction);

    ValueRecorder createValueRecorder(String path, String description);

    ValueRecorder createValueRecorderSeries(String path, String description, CaptureFunction... captureFunctions);

    QueueTimer createQueueTimer(String path, String description);

    QueueTimer createQueueTimerSeries(String path, String description, CaptureFunction... captureFunctions);

    Counter createCounter(String path, String description);

    Counter createCounterSeries(String path, String description, CaptureFunction... captureFunctions);

    EventTimer createEventTimer(String path, String description);

    EventTimer createEventTimerSeries(String path, String description, CaptureFunction... captureFunctions);

    TimedValueSupplier createTimedValueSupplier(String path, String description, ValueSupplier valueSupplier, TimePeriod timePeriod);

    TimedValueSupplier createTimedValueSupplierSeries(String path, String description, ValueSupplier valueSupplier, TimePeriod timePeriod);
}
