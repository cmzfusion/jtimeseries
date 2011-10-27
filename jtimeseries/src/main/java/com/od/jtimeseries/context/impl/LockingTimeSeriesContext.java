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
package com.od.jtimeseries.context.impl;

import com.od.jtimeseries.capture.Capture;
import com.od.jtimeseries.capture.CaptureFactory;
import com.od.jtimeseries.capture.TimedCapture;
import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.context.ContextFactory;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.IdentifiableBase;
import com.od.jtimeseries.identifiable.QueryResult;
import com.od.jtimeseries.scheduling.Scheduler;
import com.od.jtimeseries.scheduling.Triggerable;
import com.od.jtimeseries.source.*;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.util.time.TimePeriod;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 02-Jun-2010
 * Time: 08:45:19
 *
 * Lock all the interface methods here
 *
 * Really, I suspect this is a job for aspectj, but for the time being, this keeps the boilerplate out of the main
 * implementation, at least
 */
public abstract class LockingTimeSeriesContext extends IdentifiableBase implements TimeSeriesContext {

    public LockingTimeSeriesContext(String id, String description) {
        super(id, description);
    }

    public TimeSeriesContext getRoot() {
        try {
            getTreeLock().readLock().lock();
            return isRoot() ? this : getParent().getRoot();
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    public TimeSeriesContext getParent() {
        return (TimeSeriesContext) super.getParent();
    }

    public final List<ValueSource> getSources() {
        return getChildren(ValueSource.class);
    }

    public final List<Capture> getCaptures() {
        return getChildren(Capture.class);
    }

    public final List<TimeSeriesContext> getChildContexts() {
        return getChildren(TimeSeriesContext.class);
    }

    public final List<IdentifiableTimeSeries> getTimeSeries() {
       return getChildren(IdentifiableTimeSeries.class);
    }

    public final IdentifiableTimeSeries getTimeSeries(String path) {
        return get(path, IdentifiableTimeSeries.class);
    }

    public final ValueSource getSource(String path) {
        return get(path, ValueSource.class);
    }

    public final TimeSeriesContext getContext(String path) {
        return get(path, TimeSeriesContext.class);
    }

    public final Capture getCapture(String path) {
        return get(path, Capture.class);
    }

    public final boolean isSchedulerStarted() {
        try {
            getTreeLock().readLock().lock();
            return isSchedulerStarted_Locked();
        } finally {
            getTreeLock().readLock().unlock();
        }
    }
    protected abstract boolean isSchedulerStarted_Locked();

    public final TimeSeriesContext startScheduling() {
        try {
            getTreeLock().writeLock().lock();
            return startScheduling_Locked();
        } finally {
            getTreeLock().writeLock().unlock();
        }
    }
    protected abstract TimeSeriesContext startScheduling_Locked();

    public final TimeSeriesContext stopScheduling() {
        try {
            getTreeLock().writeLock().lock();
            return stopScheduling_Locked();
        } finally {
            getTreeLock().writeLock().unlock();
        }
    }
    protected abstract TimeSeriesContext stopScheduling_Locked();

    public final TimeSeriesContext startDataCapture() {
        try {
            getTreeLock().writeLock().lock();
            return startDataCapture_Locked();
        } finally {
            getTreeLock().writeLock().unlock();
        }
    }
    protected abstract TimeSeriesContext startDataCapture_Locked();

    public final TimeSeriesContext stopDataCapture() {
        try {
            getTreeLock().writeLock().lock();
            return stopDataCapture_Locked();
        } finally {
            getTreeLock().writeLock().unlock();
        }
    }
    protected abstract TimeSeriesContext stopDataCapture_Locked();

    public final TimeSeriesContext setScheduler(Scheduler scheduler) {
        addChild(scheduler);
        return this;
    }

    public final Scheduler getScheduler() {
        return getFromAncestors(Scheduler.ID, Scheduler.class);
    }

    public final TimeSeriesContext setValueSourceFactory(ValueSourceFactory sourceFactory) {
        addChild(sourceFactory);
        return this;
    }
    public final ValueSourceFactory getValueSourceFactory() {
        return getFromAncestors(ValueSourceFactory.ID, ValueSourceFactory.class);
    }

    public final TimeSeriesContext setTimeSeriesFactory(TimeSeriesFactory seriesFactory) {
        addChild(seriesFactory);
        return this;
    }

    public final TimeSeriesFactory getTimeSeriesFactory() {
        return getFromAncestors(TimeSeriesFactory.ID, TimeSeriesFactory.class);
    }

    public final TimeSeriesContext setCaptureFactory(CaptureFactory captureFactory) {
        addChild(captureFactory);
        return this;
    }

    public final CaptureFactory getCaptureFactory() {
        return getFromAncestors(CaptureFactory.ID, CaptureFactory.class);
    }

    public final TimeSeriesContext setContextFactory(ContextFactory contextFactory) {
        addChild(contextFactory);
        return this;
    }

    public final ContextFactory getContextFactory() {
        return getFromAncestors(ContextFactory.ID, ContextFactory.class);
    }

    public final TimeSeriesContext createContext(String path) {
        return createContext(path, "TimeSeriesContext at " + path);
    }

    public final TimeSeriesContext createContext(String path, String description) {
        return create(path, description, TimeSeriesContext.class);
    }

    public final IdentifiableTimeSeries createTimeSeries(String path, String description) {
        return create(path, description, IdentifiableTimeSeries.class);
    }

    public final Capture createCapture(String path, String description, ValueSource source, IdentifiableTimeSeries series) {
        return create(path, description, Capture.class, source, series, CaptureFunctions.RAW_VALUES);
    }

    public final TimedCapture createTimedCapture(String path, String description, ValueSource source, IdentifiableTimeSeries series, CaptureFunction captureFunction) {
        return create(path, description, TimedCapture.class, source, series, captureFunction);
    }

    public final ValueRecorder createValueRecorder(String path, String description) {
        return create(path, description, ValueRecorder.class);
    }

    public final Counter createCounter(String path, String description) {
        return create(path, description, Counter.class);
    }

    public final EventTimer createEventTimer(String path, String description) {
        return create(path, description, EventTimer.class);
    }

    public final TimedValueSupplier createTimedValueSupplier(String path, String description, ValueSupplier valueSupplier, TimePeriod timePeriod) {
        return create(path, description, TimedValueSupplier.class, valueSupplier, timePeriod);
    }

    public final ValueRecorder createValueRecorderSeries(String path, String description, CaptureFunction... captureFunctions) {
        return create(path + ContextMetricCreator.SOURCE_SUFFIX, description, ValueRecorder.class, (Object[])getFunctions(captureFunctions));
    }

    public final Counter createCounterSeries(String path, String description, CaptureFunction... captureFunctions) {
        return create(path + ContextMetricCreator.SOURCE_SUFFIX, description, Counter.class, (Object[])getFunctions(captureFunctions));
    }

    public final EventTimer createEventTimerSeries(String path, String description, CaptureFunction... captureFunctions) {
        return create(path + ContextMetricCreator.SOURCE_SUFFIX, description, EventTimer.class, (Object[])getFunctions(captureFunctions));
    }

    public final TimedValueSupplier createTimedValueSupplierSeries(String path, String description, ValueSupplier valueSupplier, TimePeriod timePeriod) {
        return create(path + ContextMetricCreator.SOURCE_SUFFIX, description, TimedValueSupplier.class, CaptureFunctions.RAW_VALUES, valueSupplier, timePeriod);
    }

    //here we need to make sure we send RawValues as the function if none is specified, so that we end up creating a capture/timeseries
    //otherwise the request to create the value source will be interpreted as just that, rather than a request to create a capture/series too
    private CaptureFunction[] getFunctions(CaptureFunction... captureFunctions) {
        return captureFunctions.length == 0 ? new CaptureFunction[] {CaptureFunctions.RAW_VALUES} : captureFunctions;
    }

    public final QueryResult<IdentifiableTimeSeries> findTimeSeries(CaptureCriteria criteria) {
        try {
            getTreeLock().readLock().lock();
            return findTimeSeries_Locked(criteria);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<IdentifiableTimeSeries> findTimeSeries_Locked(CaptureCriteria criteria);

    public final QueryResult<IdentifiableTimeSeries> findTimeSeries(ValueSource source) {
        try {
            getTreeLock().readLock().lock();
            return findTimeSeries_Locked(source);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<IdentifiableTimeSeries> findTimeSeries_Locked(ValueSource source);

    public final QueryResult<IdentifiableTimeSeries> findTimeSeries(String searchPattern) {
        try {
            getTreeLock().readLock().lock();
            return findTimeSeries_Locked(searchPattern);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<IdentifiableTimeSeries> findTimeSeries_Locked(String searchPattern);

    public final QueryResult<IdentifiableTimeSeries> findAllTimeSeries() {
        try {
            getTreeLock().readLock().lock();
            return findAllTimeSeries_Locked();
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<IdentifiableTimeSeries> findAllTimeSeries_Locked();

    public final QueryResult<Capture> findCaptures(String searchPattern) {
        try {
            getTreeLock().readLock().lock();
            return findCaptures_Locked(searchPattern);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Capture> findCaptures_Locked(String searchPattern);

    public final QueryResult<Capture> findCaptures(CaptureCriteria criteria) {
        try {
            getTreeLock().readLock().lock();
            return findCaptures_Locked(criteria);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Capture> findCaptures_Locked(CaptureCriteria criteria);

    public final QueryResult<Capture> findCaptures(ValueSource valueSource) {
        try {
            getTreeLock().readLock().lock();
            return findCaptures_Locked(valueSource);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Capture> findCaptures_Locked(ValueSource valueSource);

    public final QueryResult<Capture> findCaptures(IdentifiableTimeSeries timeSeries) {
        try {
            getTreeLock().readLock().lock();
            return findCaptures_Locked(timeSeries);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Capture> findCaptures_Locked(IdentifiableTimeSeries timeSeries);

    public final QueryResult<Capture> findAllCaptures() {
        try {
            getTreeLock().readLock().lock();
            return findAllCaptures_Locked();
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Capture> findAllCaptures_Locked();

    public final QueryResult<ValueSource> findValueSources(CaptureCriteria criteria) {
        try {
            getTreeLock().readLock().lock();
            return findValueSources_Locked(criteria);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<ValueSource> findValueSources_Locked(CaptureCriteria criteria);

    public final QueryResult<ValueSource> findValueSources(IdentifiableTimeSeries timeSeries) {
        try {
            getTreeLock().readLock().lock();
            return findValueSources_Locked(timeSeries);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<ValueSource> findValueSources_Locked(IdentifiableTimeSeries timeSeries);

    public final QueryResult<ValueSource> findValueSources(String searchPattern) {
        try {
            getTreeLock().readLock().lock();
            return findValueSources_Locked(searchPattern);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<ValueSource> findValueSources_Locked(String searchPattern);

    public final QueryResult<ValueSource> findAllValueSources() {
        try {
            getTreeLock().readLock().lock();
            return findAllValueSources_Locked();
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<ValueSource> findAllValueSources_Locked();

    public final QueryResult<Scheduler> findAllSchedulers() {
        try {
            getTreeLock().readLock().lock();
            return findAllSchedulers_Locked();
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Scheduler> findAllSchedulers_Locked();

    public final QueryResult<Scheduler> findSchedulers(String searchPattern) {
        try {
            getTreeLock().readLock().lock();
            return findSchedulers_Locked(searchPattern);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Scheduler> findSchedulers_Locked(String searchPattern);

    public final QueryResult<Scheduler> findSchedulers(Triggerable triggerable) {
        try {
            getTreeLock().readLock().lock();
            return findSchedulers_Locked(triggerable);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Scheduler> findSchedulers_Locked(Triggerable triggerable);

}
