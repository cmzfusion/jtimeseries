package com.od.jtimeseries.context.impl;

import com.od.jtimeseries.capture.Capture;
import com.od.jtimeseries.capture.CaptureFactory;
import com.od.jtimeseries.capture.TimedCapture;
import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.context.ContextFactory;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.scheduling.Scheduler;
import com.od.jtimeseries.scheduling.Triggerable;
import com.od.jtimeseries.source.*;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;
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

    public LockingTimeSeriesContext(Identifiable parent, String id, String description) {
        super(parent, id, description);
    }

    public LockingTimeSeriesContext(String id, String description) {
        super(id, description);
    }

    public TimeSeriesContext getRoot() {
        try {
            getContextLock().readLock().lock();
            return isRoot() ? this : getParent().getRoot();
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    public TimeSeriesContext getParent() {
        return (TimeSeriesContext) super.getParent();
    }

    public final TimeSeriesContext addChild(Identifiable... identifiables) {
        try {
            getContextLock().writeLock().lock();
            return addChild_Locked(identifiables);
        } finally {
            getContextLock().writeLock().unlock();
        }
    }
    protected abstract TimeSeriesContext addChild_Locked(Identifiable... identifiables);

    public <E extends Identifiable> E create(String path, String description, Class<E> clazz, Object... parameters) {
        try {
            getContextLock().writeLock().lock();
            return create_Locked(path, description, clazz, parameters);
        } finally {
            getContextLock().writeLock().unlock();
        }
    }
    protected abstract <E extends Identifiable> E create_Locked(String path, String description, Class<E> clazz, Object... parameters);

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

    public final Scheduler getScheduler() {
        try {
            getContextLock().readLock().lock();
            return getScheduler_Locked();
        } finally {
            getContextLock().readLock().unlock();
        }
    }
    protected abstract Scheduler getScheduler_Locked();

    public final TimeSeriesContext setScheduler(Scheduler scheduler) {
        try {
            getContextLock().writeLock().lock();
            return setScheduler_Locked(scheduler);
        } finally {
            getContextLock().writeLock().unlock();
        }
    }
    protected abstract TimeSeriesContext setScheduler_Locked(Scheduler scheduler);

    public final boolean isSchedulerStarted() {
        try {
            getContextLock().readLock().lock();
            return isSchedulerStarted_Locked();
        } finally {
            getContextLock().readLock().unlock();
        }
    }
    protected abstract boolean isSchedulerStarted_Locked();

    public final TimeSeriesContext startScheduling() {
        try {
            getContextLock().writeLock().lock();
            return startScheduling_Locked();
        } finally {
            getContextLock().writeLock().unlock();
        }
    }
    protected abstract TimeSeriesContext startScheduling_Locked();

    public final TimeSeriesContext stopScheduling() {
        try {
            getContextLock().writeLock().lock();
            return stopScheduling_Locked();
        } finally {
            getContextLock().writeLock().unlock();
        }
    }
    protected abstract TimeSeriesContext stopScheduling_Locked();

    public final TimeSeriesContext startDataCapture() {
        try {
            getContextLock().writeLock().lock();
            return startDataCapture_Locked();
        } finally {
            getContextLock().writeLock().unlock();
        }
    }
    protected abstract TimeSeriesContext startDataCapture_Locked();

    public final TimeSeriesContext stopDataCapture() {
        try {
            getContextLock().writeLock().lock();
            return stopDataCapture_Locked();
        } finally {
            getContextLock().writeLock().unlock();
        }
    }
    protected abstract TimeSeriesContext stopDataCapture_Locked();

    public final TimeSeriesContext setValueSourceFactory(ValueSourceFactory sourceFactory) {
        try {
            getContextLock().writeLock().lock();
            return setValueSourceFactory_Locked(sourceFactory);
        } finally {
            getContextLock().writeLock().unlock();
        }
    }
    protected abstract TimeSeriesContext setValueSourceFactory_Locked(ValueSourceFactory sourceFactory);

    public final ValueSourceFactory getValueSourceFactory() {
        try {
            getContextLock().readLock().lock();
            return getValueSourceFactory_Locked();
        } finally {
            getContextLock().readLock().unlock();
        }
    }
    protected abstract ValueSourceFactory getValueSourceFactory_Locked();

    public final TimeSeriesContext setTimeSeriesFactory(TimeSeriesFactory seriesFactory) {
        try {
            getContextLock().writeLock().lock();
            return setTimeSeriesFactory_Locked(seriesFactory);
        } finally {
            getContextLock().writeLock().unlock();
        }
    }
    protected abstract TimeSeriesContext setTimeSeriesFactory_Locked(TimeSeriesFactory seriesFactory);

    public final TimeSeriesFactory getTimeSeriesFactory() {
        try {
            getContextLock().readLock().lock();
            return getTimeSeriesFactory_Locked();
        } finally {
            getContextLock().readLock().unlock();
        }
    }
    protected abstract TimeSeriesFactory getTimeSeriesFactory_Locked();

    public final TimeSeriesContext setCaptureFactory(CaptureFactory captureFactory) {
        try {
            getContextLock().writeLock().lock();
            return setCaptureFactory_Locked(captureFactory);
        } finally {
            getContextLock().writeLock().unlock();
        }
    }
    protected abstract TimeSeriesContext setCaptureFactory_Locked(CaptureFactory captureFactory);

    public final CaptureFactory getCaptureFactory() {
        try {
            getContextLock().readLock().lock();
            return getCaptureFactory_Locked();
        } finally {
            getContextLock().readLock().unlock();
        }
    }
    protected abstract CaptureFactory getCaptureFactory_Locked();

    public final TimeSeriesContext setContextFactory(ContextFactory contextFactory) {
        try {
            getContextLock().writeLock().lock();
            return setContextFactory_Locked(contextFactory);
        } finally {
            getContextLock().writeLock().unlock();
        }
    }
    protected abstract TimeSeriesContext setContextFactory_Locked(ContextFactory contextFactory);

    public final ContextFactory getContextFactory() {
        try {
            getContextLock().readLock().lock();
            return getContextFactory_Locked();
        } finally {
            getContextLock().readLock().unlock();
        }
    }
    protected abstract ContextFactory getContextFactory_Locked();

    public final TimeSeriesContext createContext(String path) {
        return createContext(path, new PathParser(path).removeLastNode());
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

    public final QueueTimer createQueueTimer(String id, String description) {
        return create(id, description, QueueTimer.class);
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
        return create(path, description, ValueRecorder.class, getFunctions(captureFunctions));
    }

    public final QueueTimer createQueueTimerSeries(String path, String description, CaptureFunction... captureFunctions) {
        return create(path, description, QueueTimer.class, getFunctions(captureFunctions));
    }

    public final Counter createCounterSeries(String path, String description, CaptureFunction... captureFunctions) {
        return create(path, description, Counter.class, getFunctions(captureFunctions));
    }

    public final EventTimer createEventTimerSeries(String path, String description, CaptureFunction... captureFunctions) {
        return create(path, description, EventTimer.class, getFunctions(captureFunctions));
    }

    public final TimedValueSupplier createTimedValueSupplierSeries(String path, String description, ValueSupplier valueSupplier, TimePeriod timePeriod) {
        return create(path, description, TimedValueSupplier.class, CaptureFunctions.RAW_VALUES, valueSupplier, timePeriod);
    }

    //here we need to make sure we send RawValues as the function if none is specified, so that we end up creating a capture/timesries
    //otherwise the request to create the value source will be interpreted as just that, rather than a request to create a capture/series too
    private CaptureFunction[] getFunctions(CaptureFunction... captureFunctions) {
        return captureFunctions.length == 0 ? new CaptureFunction[] {CaptureFunctions.RAW_VALUES} : captureFunctions;
    }

    public final QueryResult<IdentifiableTimeSeries> findTimeSeries(CaptureCriteria criteria) {
        try {
            getContextLock().readLock().lock();
            return findTimeSeries_Locked(criteria);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<IdentifiableTimeSeries> findTimeSeries_Locked(CaptureCriteria criteria);

    public final QueryResult<IdentifiableTimeSeries> findTimeSeries(ValueSource source) {
        try {
            getContextLock().readLock().lock();
            return findTimeSeries_Locked(source);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<IdentifiableTimeSeries> findTimeSeries_Locked(ValueSource source);

    public final QueryResult<IdentifiableTimeSeries> findTimeSeries(String searchPattern) {
        try {
            getContextLock().readLock().lock();
            return findTimeSeries_Locked(searchPattern);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<IdentifiableTimeSeries> findTimeSeries_Locked(String searchPattern);

    public final QueryResult<IdentifiableTimeSeries> findAllTimeSeries() {
        try {
            getContextLock().readLock().lock();
            return findAllTimeSeries_Locked();
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<IdentifiableTimeSeries> findAllTimeSeries_Locked();

    public final QueryResult<Capture> findCaptures(String searchPattern) {
        try {
            getContextLock().readLock().lock();
            return findCaptures_Locked(searchPattern);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Capture> findCaptures_Locked(String searchPattern);

    public final QueryResult<Capture> findCaptures(CaptureCriteria criteria) {
        try {
            getContextLock().readLock().lock();
            return findCaptures_Locked(criteria);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Capture> findCaptures_Locked(CaptureCriteria criteria);

    public final QueryResult<Capture> findCaptures(ValueSource valueSource) {
        try {
            getContextLock().readLock().lock();
            return findCaptures_Locked(valueSource);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Capture> findCaptures_Locked(ValueSource valueSource);

    public final QueryResult<Capture> findCaptures(IdentifiableTimeSeries timeSeries) {
        try {
            getContextLock().readLock().lock();
            return findCaptures_Locked(timeSeries);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Capture> findCaptures_Locked(IdentifiableTimeSeries timeSeries);

    public final QueryResult<Capture> findAllCaptures() {
        try {
            getContextLock().readLock().lock();
            return findAllCaptures_Locked();
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Capture> findAllCaptures_Locked();

    public final QueryResult<ValueSource> findValueSources(CaptureCriteria criteria) {
        try {
            getContextLock().readLock().lock();
            return findValueSources_Locked(criteria);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<ValueSource> findValueSources_Locked(CaptureCriteria criteria);

    public final QueryResult<ValueSource> findValueSources(IdentifiableTimeSeries timeSeries) {
        try {
            getContextLock().readLock().lock();
            return findValueSources_Locked(timeSeries);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<ValueSource> findValueSources_Locked(IdentifiableTimeSeries timeSeries);

    public final QueryResult<ValueSource> findValueSources(String searchPattern) {
        try {
            getContextLock().readLock().lock();
            return findValueSources_Locked(searchPattern);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<ValueSource> findValueSources_Locked(String searchPattern);

    public final QueryResult<ValueSource> findAllValueSources() {
        try {
            getContextLock().readLock().lock();
            return findAllValueSources_Locked();
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<ValueSource> findAllValueSources_Locked();

    public final QueryResult<Scheduler> findAllSchedulers() {
        try {
            getContextLock().readLock().lock();
            return findAllSchedulers_Locked();
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Scheduler> findAllSchedulers_Locked();

    public final QueryResult<Scheduler> findSchedulers(String searchPattern) {
        try {
            getContextLock().readLock().lock();
            return findSchedulers_Locked(searchPattern);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Scheduler> findSchedulers_Locked(String searchPattern);

    public final QueryResult<Scheduler> findSchedulers(Triggerable triggerable) {
        try {
            getContextLock().readLock().lock();
            return findSchedulers_Locked(triggerable);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract QueryResult<Scheduler> findSchedulers_Locked(Triggerable triggerable);

    public final <E> QueryResult<E> findAll(Class<E> assignableToClass) {
        try {
            getContextLock().readLock().lock();
            return findAllChildren_Locked(assignableToClass);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract <E> QueryResult<E> findAllChildren_Locked(Class<E> assignableToClass);

    public final <E> QueryResult<E> findAll(String searchPattern, Class<E> assignableToClass) {
        try {
            getContextLock().readLock().lock();
            return findAllChildren_Locked(searchPattern, assignableToClass);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract <E> QueryResult<E> findAllChildren_Locked(String searchPattern, Class<E> assignableToClass);
}
