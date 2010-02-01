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
package com.od.jtimeseries.context.impl;

import com.od.jtimeseries.capture.Capture;
import com.od.jtimeseries.capture.CaptureFactory;
import com.od.jtimeseries.capture.TimedCapture;
import com.od.jtimeseries.capture.impl.DefaultCaptureFactory;
import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.context.*;
import com.od.jtimeseries.scheduling.Scheduler;
import com.od.jtimeseries.scheduling.Triggerable;
import com.od.jtimeseries.scheduling.DefaultScheduler;
import com.od.jtimeseries.source.*;
import com.od.jtimeseries.source.impl.DefaultValueSourceFactory;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeriesFactory;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.JTimeSeriesConstants;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-Dec-2008
 * Time: 10:17:44
 */
public class DefaultTimeSeriesContext extends IdentifiableBase implements TimeSeriesContext {

    private final List<Identifiable> children = Collections.synchronizedList(new ArrayList<Identifiable>());
    private final Map<String, Identifiable> childrenById = Collections.synchronizedMap(new HashMap<String, Identifiable>());
    private ContextQueries contextQueries = new DefaultContextQueries(this);
    private DefaultMetricCreator defaultMetricCreator = new DefaultMetricCreator(this);

    /**
     * Create a context without a parent (i.e. a root context), using a default id and description, which has its own
     * scheduler and factories for series, value sources, captures and child contexts, and sets default context properties.
     * Child contexts generally inherit these from the root context, although any set up at a child level override the parent.
     */
    public DefaultTimeSeriesContext() {
        this(null, JTimeSeriesConstants.DEFAULT_ROOT_CONTEXT_ID, JTimeSeriesConstants.DEFAULT_ROOT_CONTEXT_ID);
    }

    /**
     * Create a context without a parent (i.e. a root context), which has its own scheduler and factories for
     * series, value sources, captures and child contexts, and sets default context properties.
     * Child contexts generally inherit these from the root context, although any set up at a child level override the parent.
     */
    public DefaultTimeSeriesContext(String id, String description) {
        this(null, id, description);
    }

    /**
     * Create a context which is a child of the parentContext supplied
     * @param parentContext parentContext or null, if null then a root context is created
     */
    public DefaultTimeSeriesContext(TimeSeriesContext parentContext, String id, String description) {
        super(parentContext, id, description);
        checkId(id);
        if ( parentContext == null) {
            createRootContextResources(id);
        }
    }

    private void createRootContextResources(String id) {
        setScheduler(new DefaultScheduler(id + " Scheduler", id + "Scheduler"));
        setTimeSeriesFactory(new DefaultTimeSeriesFactory());
        setValueSourceFactory(new DefaultValueSourceFactory());
        setCaptureFactory(new DefaultCaptureFactory());
        setContextFactory(new DefaultContextFactory());
        setDefaultContextProperties();
    }

    private void setDefaultContextProperties() {
        setProperty(ContextProperties.START_CAPTURES_IMMEDIATELY_PROPERTY, "true");
    }


    public TimeSeriesContext getRoot() {
        synchronized (getTreeLock()) {
            return isRoot() ? this : getParent().getRoot();
        }
    }

    public List<ValueSource> getSources() {
        return getChildren(ValueSource.class);
    }

    public List<Capture> getCaptures() {
        return getChildren(Capture.class);
    }

    public List<TimeSeriesContext> getChildContexts() {
        return getChildren(TimeSeriesContext.class);
    }

    public List<IdentifiableTimeSeries> getTimeSeries() {
        return getChildren(IdentifiableTimeSeries.class);
    }

    public <E extends Identifiable> List<E> getChildren(Class<E> classType) {
        synchronized (getTreeLock()) {
            List<E> list = new ArrayList<E>();
            for ( Identifiable i : children) {
                if ( classType.isAssignableFrom(i.getClass())) {
                    list.add((E)i);
                }
            }
            return list;
        }
    }

    public TimeSeriesContext addChild(Identifiable... identifiables) {
        synchronized (getTreeLock()) {
            checkIdCharacters(identifiables);
            for ( Identifiable i : identifiables) {
                if ( i instanceof Scheduler) {
                    doSetScheduler((Scheduler)i);
                }
                if ( i instanceof ValueSourceFactory) {
                    removeUniqueChildInstance(ValueSourceFactory.class);
                }
                if ( i instanceof ContextFactory) {
                    removeUniqueChildInstance(ContextFactory.class);
                }
                if ( i instanceof CaptureFactory) {
                    removeUniqueChildInstance(CaptureFactory.class);
                }
                if ( i instanceof TimeSeriesFactory) {
                    removeUniqueChildInstance(TimeSeriesFactory.class);
                }
                if ( i instanceof Triggerable ) {
                    getScheduler().addTriggerable((Triggerable)i);
                }
                if ( i instanceof Capture ) {
                    if (Boolean.parseBoolean(findProperty(ContextProperties.START_CAPTURES_IMMEDIATELY_PROPERTY))) {
                        ((Capture)i).start();
                    }
                }
                checkUniqueIdAndAdd(i);
            }
            return this;
        }
    }

    public boolean removeChild(Identifiable e) {
        synchronized (getTreeLock()) {
            boolean removed = children.remove(e);
            if ( removed) {
                e.setParent(null);
                childrenById.remove(e.getId());
            }
            return removed;
        }
    }

    private void doSetScheduler(Scheduler scheduler) {
        synchronized (getTreeLock()) {
            if ( isSchedulerStarted() ) {
                throw new UnsupportedOperationException("Cannot setScheduler while the existing scheduler is running");
            } else {
                Scheduler oldSchedulerForThisContext = removeUniqueChildInstance(Scheduler.class);
                if (oldSchedulerForThisContext == null) {
                    oldSchedulerForThisContext = getScheduler();  //this context was using the parent scheduler
                }
                moveTriggerablesFromOldToNewScheduler(oldSchedulerForThisContext, scheduler);
            }
        }
    }

    //If any capture from this context in the hierarchy downwards is assoicated with the old scheduler, remove it
    //and add it to the new scheduler
    private void moveTriggerablesFromOldToNewScheduler(Scheduler oldScheduler, Scheduler newScheduler) {
        for ( Triggerable t : findAllChildren(Triggerable.class).getAllMatches()) {
            if ( oldScheduler != null && oldScheduler.containsTriggerable(t)) {
                oldScheduler.removeTriggerable(t);
                newScheduler.addTriggerable(t);
            }
        }
    }

    private <E extends Identifiable> E getUniqueChild(Class<E> classType) {
        List<E> child = getChildren(classType);
        if ( child.size() == 1 ) {
            return child.get(0);
        }
        return null;
    }

    private <E extends Identifiable> E removeUniqueChildInstance(Class<E> uniqueChildClass) {
        E result = getUniqueChild(uniqueChildClass);
        if ( result != null ) {
            removeChild(result);
        }
        return result;
    }

    public IdentifiableTimeSeries getTimeSeries(String id) {
        return get(id, IdentifiableTimeSeries.class);
    }

    public ValueSource getSource(String id) {
        return get(id, ValueSource.class);
    }

    public TimeSeriesContext getChildContext(String id) {
        return get(id, TimeSeriesContext.class);
    }

    public Capture getCapture(String id) {
        return get(id, Capture.class);
    }

    public Identifiable get(String id) {
        return childrenById.get(id);
    }

    public <E extends Identifiable> E get(String id, Class<E> classType) {
        synchronized (getTreeLock()) {
            Identifiable i = childrenById.get(id);
            if ( i != null && classType.isAssignableFrom(i.getClass())) {
                return (E)i;
            }
            return null;
        }
    }

    public boolean containsChildWithId(String id) {
        synchronized (getTreeLock()) {
            return childrenById.containsKey(id);
        }
    }

    public boolean containsChild(Identifiable child) {
        synchronized (getTreeLock()) {
            return children.contains(child);
        }
    }

    public Scheduler getScheduler() {
        synchronized (getTreeLock()) {
            Scheduler result = getUniqueChild(Scheduler.class);
            if ( result == null && ! isRoot() ) {
                result = getParent().getScheduler();
            }
            return result;
        }
    }

    public TimeSeriesContext setScheduler(Scheduler scheduler) {
        addChild(scheduler);
        return this;
    }

    public boolean isSchedulerStarted() {
        synchronized (getTreeLock()) {
            //can be null only if the root context during construction
            return getScheduler() != null && getScheduler().isStarted();
        }
    }

    public TimeSeriesContext startScheduling() {
        synchronized (getTreeLock()) {
            for (Scheduler s : findAllSchedulers().getAllMatches()) {
                s.start();
            }
            return this;
        }
    }

    public TimeSeriesContext stopScheduling() {
        synchronized (getTreeLock()) {
            for (Scheduler s : findAllSchedulers().getAllMatches()) {
                s.stop();
            }
            return this;
        }
    }

    public TimeSeriesContext startDataCapture() {
        synchronized (getTreeLock()) {
            List<Capture> allCaptures = findAllCaptures().getAllMatches();
            for ( Capture c : allCaptures) {
                c.start();
            }
            return this;
        }
    }

    public TimeSeriesContext stopDataCapture() {
        synchronized (getTreeLock()) {
            List<Capture> allCaptures = findAllCaptures().getAllMatches();
            for ( Capture c : allCaptures) {
                c.stop();
            }
            return this;
        }
    }

    public TimeSeriesContext setTimeSeriesFactory(TimeSeriesFactory seriesFactory) {
        addChild(seriesFactory);
        return this;
    }

    public TimeSeriesFactory getTimeSeriesFactory() {
        synchronized (getTreeLock()) {
            TimeSeriesFactory result = getUniqueChild(TimeSeriesFactory.class);
            if ( result == null && ! isRoot() ) {
                result = getParent().getTimeSeriesFactory();
            }
            return result;
        }
    }

    public TimeSeriesContext setCaptureFactory(CaptureFactory captureFactory) {
        addChild(captureFactory);
        return this;
    }

    public CaptureFactory getCaptureFactory() {
        synchronized (getTreeLock()) {
            CaptureFactory result = getUniqueChild(CaptureFactory.class);
            if ( result == null && ! isRoot() ) {
                result = getParent().getCaptureFactory();
            }
            return result;
        }
    }

    public TimeSeriesContext setValueSourceFactory(ValueSourceFactory sourceFactory) {
        addChild(sourceFactory);
        return this;
    }

    public ValueSourceFactory getValueSourceFactory() {
        synchronized (getTreeLock()) {
            ValueSourceFactory result = getUniqueChild(ValueSourceFactory.class);
            if ( result == null && ! isRoot() ) {
                result = getParent().getValueSourceFactory();
            }
            return result;
        }
    }

     public TimeSeriesContext setContextFactory(ContextFactory contextFactory) {
        addChild(contextFactory);
        return this;
    }

    public ContextFactory getContextFactory() {
        synchronized (getTreeLock()) {
            ContextFactory result = getUniqueChild(ContextFactory.class);
            if ( result == null && ! isRoot() ) {
                result = getParent().getContextFactory();
            }
            return result;
        }
    }

    public IdentifiableTimeSeries createTimeSeriesForPath(String path, String description) {
        synchronized (getTreeLock()) {
            List<String> contextIds = splitPath(path);
            TimeSeriesContext parentContext = ( contextIds.size() > 1) ? createContextRecursive(this, contextIds.subList(0, contextIds.size() -1)) : this;
            String id = contextIds.get(contextIds.size()-1);
            IdentifiableTimeSeries s = parentContext.getTimeSeries(id);
            if ( s == null) {
                s = parentContext.createTimeSeries(id, description);
            }
            return s;
        }
    }

    public IdentifiableTimeSeries createTimeSeries(String id, String description) {
        synchronized (getTreeLock()) {
            IdentifiableTimeSeries i = getTimeSeriesFactory().createTimeSeries(getPath() + NAMESPACE_SEPARATOR + id, id, description);
            addChild(i);
            return i;
        }
    }

    public Capture createCapture(String id, ValueSource source, IdentifiableTimeSeries series) {
        synchronized (getTreeLock()) {
            Capture c = getCaptureFactory().createCapture(getPath() + NAMESPACE_SEPARATOR + id, id, source, series);
            addChild(c);
            return c;
        }
    }

    public TimedCapture createTimedCapture(String id, ValueSource source, IdentifiableTimeSeries series, CaptureFunction captureFunction) {
        synchronized (getTreeLock()) {
            TimedCapture c = getCaptureFactory().createTimedCapture(getPath() + NAMESPACE_SEPARATOR + id, id, source, series, captureFunction);
            addChild(c);
            return c;
        }
    }

    public ValueRecorder createValueRecorder(String id, String description) {
        synchronized (getTreeLock()) {
            ValueRecorder v = getValueSourceFactory().createValueRecorder(getPath() + NAMESPACE_SEPARATOR + id, id, description);
            addChild(v);
            return v;
        }
    }

    public QueueTimer createQueueTimer(String id, String description) {
        synchronized (getTreeLock()) {
            QueueTimer q = getValueSourceFactory().createQueueTimer(getPath() + NAMESPACE_SEPARATOR + id, id, description);
            addChild(q);
            return q;
        }
    }

    public Counter createCounter(String id, String description) {
        synchronized (getTreeLock()) {
            Counter c = getValueSourceFactory().createCounter(getPath() + NAMESPACE_SEPARATOR + id, id, description);
            addChild(c);
            return c;
        }
    }

    public EventTimer createEventTimer(String id, String description) {
        synchronized (getTreeLock()) {
            EventTimer e = getValueSourceFactory().createEventTimer(getPath() + NAMESPACE_SEPARATOR + id, id, description);
            addChild(e);
            return e;
        }
    }

    public TimedValueSource createTimedValueSource(String id, String description, ValueSupplier valueSupplier, TimePeriod timePeriod) {
        synchronized (getTreeLock()) {
            TimedValueSource t = getValueSourceFactory().createTimedValueSource(getPath() + NAMESPACE_SEPARATOR + id, id, description, valueSupplier, timePeriod);
            addChild(t);
            return t;
        }
    }

    public TimeSeriesContext createContextForPath(String path) {
        synchronized (getTreeLock()) {
            if ( path.equals("")) {
                return this;
            } else {
                List<String> contextIds = splitPath(path);
                return createContextRecursive(this, contextIds);
            }
        }
    }

    private TimeSeriesContext createContextRecursive(TimeSeriesContext context, List<String> pathContextIds) {
        String nextId = pathContextIds.remove(0);
        TimeSeriesContext child = getOrCreateChildContext(context, nextId);
        if ( pathContextIds.size() > 0) {
            child = createContextRecursive(child, pathContextIds);
        }
        return child;
    }

    private TimeSeriesContext getOrCreateChildContext(TimeSeriesContext context, String nextId) {
        TimeSeriesContext child = context.getChildContext(nextId);
        if ( child == null) {
            child = context.createChildContext(nextId);
        }
        return child;
    }

    public TimeSeriesContext createChildContext(String id) {
        return createChildContext(id, id);
    }

    public TimeSeriesContext createChildContext(String id, String description) {
        synchronized (getTreeLock()) {
            TimeSeriesContext timeSeriesContext = getContextFactory().createContext(this, id, description);
            synchronized (getTreeLock()) {
                checkUniqueIdAndAdd(timeSeriesContext);
            }
            return timeSeriesContext;
        }
    }

    public ValueRecorder newValueRecorder(String id, String description, CaptureFunction... captureFunctions) {
        synchronized (getTreeLock()) {
            return defaultMetricCreator.newValueRecorder(id, description, captureFunctions);
        }
    }

    public QueueTimer newQueueTimer(String id, String description, CaptureFunction... captureFunctions) {
        synchronized (getTreeLock()) {
            return defaultMetricCreator.newQueueTimer(id, description, captureFunctions);
        }
    }

    public Counter newCounter(String id, String description, CaptureFunction... captureFunctions) {
        synchronized (getTreeLock()) {
            return defaultMetricCreator.newCounter(id, description, captureFunctions);
        }
    }

    public EventTimer newEventTimer(String id, String description, CaptureFunction... captureFunctions) {
        synchronized (getTreeLock()) {
            return defaultMetricCreator.newEventTimer(id, description, captureFunctions);
        }
    }

    public TimedValueSource newTimedValueSource(String id, String description, ValueSupplier valueSupplier, TimePeriod timePeriod) {
        return defaultMetricCreator.newTimedValueSource(id, description, valueSupplier, timePeriod);
    }

    public QueryResult<IdentifiableTimeSeries> findTimeSeries(CaptureCriteria criteria) {
        synchronized (getTreeLock()) {
            return contextQueries.findTimeSeries(criteria);
        }
    }

    public QueryResult<IdentifiableTimeSeries> findTimeSeries(ValueSource source) {
        synchronized (getTreeLock()) {
            return contextQueries.findTimeSeries(source);
        }
    }

    public QueryResult<IdentifiableTimeSeries> findTimeSeries(String searchPattern) {
        synchronized (getTreeLock()) {
            return contextQueries.findTimeSeries(searchPattern);
        }
    }

    public QueryResult<IdentifiableTimeSeries> findAllTimeSeries() {
        synchronized (getTreeLock()) {
            return contextQueries.findAllTimeSeries();
        }
    }

    public QueryResult<Capture> findCaptures(String searchPattern) {
        synchronized (getTreeLock()) {
            return contextQueries.findCaptures(searchPattern);
        }
    }

    public QueryResult<Capture> findCaptures(CaptureCriteria criteria) {
        synchronized (getTreeLock()) {
            return contextQueries.findCaptures(criteria);
        }
    }

    public QueryResult<Capture> findCaptures(ValueSource valueSource) {
        synchronized (getTreeLock()) {
            return contextQueries.findCaptures(valueSource);
        }
    }

    public QueryResult<Capture> findCaptures(IdentifiableTimeSeries timeSeries) {
        synchronized (getTreeLock()) {
            return contextQueries.findCaptures(timeSeries);
        }
    }

    public QueryResult<Capture> findAllCaptures() {
        synchronized (getTreeLock()) {
            return contextQueries.findAllCaptures();
        }
    }

    public QueryResult<ValueSource> findValueSources(CaptureCriteria criteria) {
        synchronized (getTreeLock()) {
            return contextQueries.findValueSources(criteria);
        }
    }

    public QueryResult<ValueSource> findValueSources(IdentifiableTimeSeries timeSeries) {
        synchronized (getTreeLock()) {
            return contextQueries.findValueSources(timeSeries);
        }
    }

    public QueryResult<ValueSource> findValueSources(String searchPattern) {
        synchronized (getTreeLock()) {
            return contextQueries.findValueSources(searchPattern);
        }
    }

    public QueryResult<ValueSource> findAllValueSources() {
        synchronized (getTreeLock()) {
            return contextQueries.findAllValueSources();
        }
    }

    public QueryResult<Scheduler> findAllSchedulers() {
        synchronized (getTreeLock()) {
            return contextQueries.findAllSchedulers();
        }
    }

    public QueryResult<Scheduler> findSchedulers(String searchPattern) {
        synchronized (getTreeLock()) {
            return contextQueries.findSchedulers(searchPattern);
        }
    }

    public QueryResult<Scheduler> findSchedulers(Triggerable triggerable) {
        synchronized (getTreeLock()) {
            return contextQueries.findSchedulers(triggerable);
        }
    }

    public <E> QueryResult<E> findAllChildren(Class<E> assignableToClass) {
        synchronized (getTreeLock()) {
            return contextQueries.findAllChildren(assignableToClass);
        }
    }

    public <E> QueryResult<E> findAllChildren(String searchPattern, Class<E> assignableToClass) {
        synchronized (getTreeLock()) {
            return contextQueries.findAllChildren(searchPattern, assignableToClass);
        }
    }

    public String toString() {
        return "Context " + getId();
    }

    public List<Identifiable> getChildren() {
        synchronized (getTreeLock()) {
            List<Identifiable> children = new ArrayList<Identifiable>();
            children.addAll(this.children);
            return children;
        }
    }

    public TimeSeriesContext getParent() {
        return (TimeSeriesContext)super.getParent();
    }

    private <E extends Identifiable> void checkUniqueIdAndAdd(E identifiable) {
        synchronized (getTreeLock()) {
            synchronized (children) {
                if ( childrenById.containsKey(identifiable.getId())) {
                    throw new AlreadyExistsException("id " + identifiable.getId() + " already exists in this context");
                } else {
                    children.add(identifiable);
                    childrenById.put(identifiable.getId(), identifiable);
                }
                identifiable.setParent(this);
            }
        }
    }


}
