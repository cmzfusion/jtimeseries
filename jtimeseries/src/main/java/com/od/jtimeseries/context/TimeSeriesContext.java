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
import com.od.jtimeseries.context.impl.ContextMetricCreator;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 17-Dec-2008
 * Time: 17:25:10
 */
public interface TimeSeriesContext extends Identifiable, ContextQueries, ContextMetricCreator {

    TimeSeriesContext getParent();

    TimeSeriesContext getRoot();

    List<ValueSource> getSources();

    List<Capture> getCaptures();

    List<TimeSeriesContext> getChildContexts();

    List<IdentifiableTimeSeries> getTimeSeries();

    TimeSeriesContext addChild(Identifiable... identifiables);

    IdentifiableTimeSeries getTimeSeries(String id);

    ValueSource getSource(String id);

    TimeSeriesContext getChildContext(String id);

    Capture getCapture(String id);

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

    /**
     * Get or create context for the given path, creating any missing context nodes
     */
    TimeSeriesContext createContextForPath(String path);

    /**
     * Get or create timeseries for the given path, creating any missing context nodes
     */
    IdentifiableTimeSeries createTimeSeriesForPath(String path, String description);

    /**
     * Create a child context with the given id, which will also be used for the description
     */
    TimeSeriesContext createChildContext(String id);

    /**
     * Create a child context with the given id and description
     */
    TimeSeriesContext createChildContext(String id, String description);

    /**
     * Create a new IdentifiableTimeSeries and add it to this context
     */
    IdentifiableTimeSeries createTimeSeries(String id, String description);

    /**
     * Create a new Capture to capture values from source into series, and add it to this context
     */
    Capture createCapture(String id, ValueSource source, IdentifiableTimeSeries series);

    /**
     * Create a new TimedCapture to periodically capture values from source into series using the captureFunction, and add it to this context
     */
    TimedCapture createTimedCapture(String id, ValueSource source, IdentifiableTimeSeries series, CaptureFunction captureFunction);

    /**
     * Create a ValueRecorder and add it to this context, without creating an associated Capture and TimeSeries
     */
    ValueRecorder createValueRecorder(String id, String description);

    /**
     * Create a QueueTimer and add it to this context, without creating an associated Capture and TimeSeries
     */
    QueueTimer createQueueTimer(String id, String description);

    /**
     * Create a Counter and add it to this context, without creating an associated Capture and TimeSeries
     */
    Counter createCounter(String id, String description);

    /**
     * Create a EventTimer and add it to this context, without creating an associated Capture and TimeSeries
     */
    EventTimer createEventTimer(String id, String description);

    /**
     * Create a TimedValueSource and add it to this context, without creating an associated Capture and TimeSeries
     */
    TimedValueSource createTimedValueSource(String id, String description, ValueSupplier valueSupplier, TimePeriod timePeriod);
}
