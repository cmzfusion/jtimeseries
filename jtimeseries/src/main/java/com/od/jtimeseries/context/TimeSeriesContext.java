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
import com.od.jtimeseries.scheduling.Scheduler;
import com.od.jtimeseries.source.*;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.util.identifiable.Identifiable;
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
    TimeSeriesContext getOrCreateContextForPath(String path);

    /**
     * Get or create timeseries for the given path, creating any missing context nodes
     */
    IdentifiableTimeSeries getOrCreateTimeSeriesForPath(String path, String description);

    TimeSeriesContext createChildContext(String id);

    TimeSeriesContext createChildContext(String id, String description);

    /**
     * @return a new time series with the given id and description, provided a timeseries with this id does not already exist
     */
    IdentifiableTimeSeries createTimeSeries(String id, String description);

}
