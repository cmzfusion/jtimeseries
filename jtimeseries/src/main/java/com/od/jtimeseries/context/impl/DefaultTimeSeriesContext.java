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
import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.capture.impl.DefaultCaptureFactory;
import com.od.jtimeseries.context.*;
import com.od.jtimeseries.scheduling.DefaultScheduler;
import com.od.jtimeseries.scheduling.Scheduler;
import com.od.jtimeseries.scheduling.Triggerable;
import com.od.jtimeseries.source.*;
import com.od.jtimeseries.source.impl.DefaultValueSourceFactory;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeriesFactory;
import com.od.jtimeseries.util.JTimeSeriesConstants;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.time.TimePeriod;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-Dec-2008
 * Time: 10:17:44
 */
public class DefaultTimeSeriesContext extends LockingTimeSeriesContext {

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

    protected List<ValueSource> getSources_Locked() {
        return getChildren(ValueSource.class);
    }

    protected List<Capture> getCaptures_Locked() {
        return getChildren(Capture.class);
    }

    protected List<TimeSeriesContext> getChildContexts_Locked() {
        return getChildren(TimeSeriesContext.class);
    }

    protected List<IdentifiableTimeSeries> getTimeSeries_Locked() {
        return getChildren(IdentifiableTimeSeries.class);
    }

    protected <E extends Identifiable> List<E> getChildren_Locked(Class<E> classType) {
        List<E> list = new ArrayList<E>();
        for ( Identifiable i : children) {
            if ( classType.isAssignableFrom(i.getClass())) {
                list.add((E)i);
            }
        }
        return list;
    }

    protected TimeSeriesContext addChild_Locked(Identifiable... identifiables) {
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

    protected boolean removeChild_Locked(Identifiable e) {
        boolean removed = children.remove(e);
        if ( removed) {
            e.setParent(null);
            childrenById.remove(e.getId());
        }
        return removed;
    }

    private void doSetScheduler(Scheduler scheduler) {
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

    //If any capture from this context in the hierarchy downwards is assoicated with the old scheduler, remove it
    //and add it to the new scheduler
    private void moveTriggerablesFromOldToNewScheduler(Scheduler oldScheduler, Scheduler newScheduler) {
        for ( Triggerable t : findAll(Triggerable.class).getAllMatches()) {
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

    protected IdentifiableTimeSeries getTimeSeries_Locked(String path) {
        return get(path, IdentifiableTimeSeries.class);
    }

    protected ValueSource getSource_Locked(String path) {
        return get(path, ValueSource.class);
    }

    protected TimeSeriesContext getChildContext_Locked(String path) {
        return get(path, TimeSeriesContext.class);
    }

    protected Capture getCapture_Locked(String path) {
        return get(path, Capture.class);
    }

    protected Identifiable get_Locked(String path) {
        Identifiable result;
        PathParser p = new PathParser(path);
        if (p.isSingleNode()) {
            result = childrenById.get(path);
        } else {
            String childContext = p.removeFirstNode();
            TimeSeriesContext c = getChildContext(childContext);
            result = c == null ? null : c.get(p.getRemainingPath());
        }
        return result;
    }

    protected <E extends Identifiable> E get_Locked(String path, Class<E> classType) {
        Identifiable result = get(path);
        if ( result != null && ! classType.isAssignableFrom(result.getClass())) {
            result = null;
        }
        return (E)result;
    }

    protected boolean containsChildWithId_Locked(String id) {
        return childrenById.containsKey(id);
    }

    protected boolean containsChild_Locked(Identifiable child) {
        return children.contains(child);
    }

    protected Scheduler getScheduler_Locked() {
        Scheduler result = getUniqueChild(Scheduler.class);
        if ( result == null && ! isRoot() ) {
            result = getParent().getScheduler();
        }
        return result;
    }

    protected TimeSeriesContext setScheduler_Locked(Scheduler scheduler) {
        addChild(scheduler);
        return this;
    }

    protected boolean isSchedulerStarted_Locked() {
        //can be null only if the root context during construction
        return getScheduler() != null && getScheduler().isStarted();
    }

    protected TimeSeriesContext startScheduling_Locked() {
        for (Scheduler s : findAllSchedulers().getAllMatches()) {
            s.start();
        }
        return this;
    }

    protected TimeSeriesContext stopScheduling_Locked() {
        for (Scheduler s : findAllSchedulers().getAllMatches()) {
            s.stop();
        }
        return this;
    }

    protected TimeSeriesContext startDataCapture_Locked() {
        List<Capture> allCaptures = findAllCaptures().getAllMatches();
        for ( Capture c : allCaptures) {
            c.start();
        }
        return this;
    }

    protected TimeSeriesContext stopDataCapture_Locked() {
        List<Capture> allCaptures = findAllCaptures().getAllMatches();
        for ( Capture c : allCaptures) {
            c.stop();
        }
        return this;
    }

    protected TimeSeriesContext setTimeSeriesFactory_Locked(TimeSeriesFactory seriesFactory) {
        addChild(seriesFactory);
        return this;
    }

    protected TimeSeriesFactory getTimeSeriesFactory_Locked() {
        TimeSeriesFactory result = getUniqueChild(TimeSeriesFactory.class);
        if ( result == null && ! isRoot() ) {
            result = getParent().getTimeSeriesFactory();
        }
        return result;
    }

    protected TimeSeriesContext setCaptureFactory_Locked(CaptureFactory captureFactory) {
        addChild(captureFactory);
        return this;
    }

    protected CaptureFactory getCaptureFactory_Locked() {
        CaptureFactory result = getUniqueChild(CaptureFactory.class);
        if ( result == null && ! isRoot() ) {
            result = getParent().getCaptureFactory();
        }
        return result;
    }

    protected TimeSeriesContext setValueSourceFactory_Locked(ValueSourceFactory sourceFactory) {
        addChild(sourceFactory);
        return this;
    }

    protected ValueSourceFactory getValueSourceFactory_Locked() {
        ValueSourceFactory result = getUniqueChild(ValueSourceFactory.class);
        if ( result == null && ! isRoot() ) {
            result = getParent().getValueSourceFactory();
        }
        return result;
    }

    protected TimeSeriesContext setContextFactory_Locked(ContextFactory contextFactory) {
        addChild(contextFactory);
        return this;
    }

    protected ContextFactory getContextFactory_Locked() {
        ContextFactory result = getUniqueChild(ContextFactory.class);
        if ( result == null && ! isRoot() ) {
            result = getParent().getContextFactory();
        }
        return result;
    }

    protected IdentifiableTimeSeries createTimeSeries_Locked(String path, String description) {
       return create(path, description, IdentifiableTimeSeries.class);
    }

    protected <E extends Identifiable> E create_Locked(String path, String description, Class<E> clazz) {
        PathParser p = new PathParser(path);
        if ( p.isSingleNode() ) {
            E s = get(path, clazz);
            if ( s == null) {
                s = doCreate(path, description, clazz);
                addChild(s);
            }
            return s;
        } else {
            String id = p.removeLastNode();
            TimeSeriesContext c = createContext(p.getRemainingPath());
            return c.create(id, description, clazz);
        }
    }

    private <E extends Identifiable> E doCreate(String id, String description, Class<E> clazz) {
        E result;
        if ( IdentifiableTimeSeries.class.isAssignableFrom(clazz)) {
            result = getTimeSeriesFactory().createTimeSeries(this, getPathForChild(id), id, description, clazz);
        } else {
            throw new UnsupportedOperationException("Cannot create identifiable of class " + clazz);
        }
        return result;
    }


    protected Capture createCapture_Locked(String id, ValueSource source, IdentifiableTimeSeries series) {
        Capture c = getCaptureFactory().createCapture(this, getPathForChild(id), id, source, series);
        addChild(c);
        return c;
    }

    protected TimedCapture createTimedCapture_Locked(String id, ValueSource source, IdentifiableTimeSeries series, CaptureFunction captureFunction) {
        TimedCapture c = getCaptureFactory().createTimedCapture(this, getPathForChild(id), id, source, series, captureFunction);
        addChild(c);
        return c;
    }

    protected ValueRecorder createValueRecorder_Locked(String id, String description) {
        ValueRecorder v = getValueSourceFactory().createValueRecorder(this, getPathForChild(id), id, description);
        addChild(v);
        return v;
    }

    protected QueueTimer createQueueTimer_Locked(String id, String description) {
        QueueTimer q = getValueSourceFactory().createQueueTimer(this, getPathForChild(id), id, description);
        addChild(q);
        return q;
    }

    protected Counter createCounter_Locked(String id, String description) {
        Counter c = getValueSourceFactory().createCounter(this, getPathForChild(id), id, description);
        addChild(c);
        return c;
    }

    protected EventTimer createEventTimer_Locked(String id, String description) {
        EventTimer e = getValueSourceFactory().createEventTimer(this, getPathForChild(id), id, description);
        addChild(e);
        return e;
    }

    protected TimedValueSource createTimedValueSource_Locked(String id, String description, ValueSupplier valueSupplier, TimePeriod timePeriod) {
        TimedValueSource t = getValueSourceFactory().createTimedValueSource(this, getPathForChild(id), id, description, valueSupplier, timePeriod);
        addChild(t);
        return t;
    }

    protected TimeSeriesContext createContext_Locked(String path, String description) {
        TimeSeriesContext result;
        PathParser pathParser = new PathParser(path);
        if ( pathParser.isEmpty() ) {
            result = this;
        } else {
            String firstChild = pathParser.removeFirstNode();
            //only want to apply the description to the last node in the path, otherwise use the id
            //as the description for each intermediate node
            String desc = pathParser.isEmpty() ? description : firstChild;
            TimeSeriesContext c = getOrCreateContext(firstChild, desc);
            result = c.createContext(pathParser.getRemainingPath(), description);
        }
        return result;
    }

    private TimeSeriesContext getOrCreateContext(String id, String description) {
        TimeSeriesContext c = getChildContext(id);
        if ( c == null ) {
            c = getContextFactory().createContext(this, id, description);
            checkUniqueIdAndAdd(c);
        }
        return c;
    }

    protected ValueRecorder createValueRecorderSeries_Locked(String id, String description, CaptureFunction... captureFunctions) {
        return defaultMetricCreator.createValueRecorderSeries(this, getPathForChild(id), id, description, captureFunctions);
    }

    protected QueueTimer createQueueTimerSeries_Locked(String id, String description, CaptureFunction... captureFunctions) {
        return defaultMetricCreator.createQueueTimerSeries(this, getPathForChild(id), id, description, captureFunctions);
    }

    protected Counter createCounterSeries_Locked(String id, String description, CaptureFunction... captureFunctions) {
        return defaultMetricCreator.createCounterSeries(this, getPathForChild(id), id, description, captureFunctions);
    }

    protected EventTimer createEventTimerSeries_Locked(String id, String description, CaptureFunction... captureFunctions) {
        return defaultMetricCreator.createEventTimerSeries(this, getPathForChild(id), id, description, captureFunctions);
    }

    protected TimedValueSource createValueSupplierSeries_Locked(String id, String description, ValueSupplier valueSupplier, TimePeriod timePeriod) {
        return defaultMetricCreator.createValueSupplierSeries(this, getPathForChild(id), id, description, valueSupplier, timePeriod);
    }

    protected QueryResult<IdentifiableTimeSeries> findTimeSeries_Locked(CaptureCriteria criteria) {
        return contextQueries.findTimeSeries(criteria);
    }

    protected QueryResult<IdentifiableTimeSeries> findTimeSeries_Locked(ValueSource source) {
        return contextQueries.findTimeSeries(source);
    }

    protected QueryResult<IdentifiableTimeSeries> findTimeSeries_Locked(String searchPattern) {
        return contextQueries.findTimeSeries(searchPattern);
    }

    protected QueryResult<IdentifiableTimeSeries> findAllTimeSeries_Locked() {
        return contextQueries.findAllTimeSeries();
    }

    protected QueryResult<Capture> findCaptures_Locked(String searchPattern) {
        return contextQueries.findCaptures(searchPattern);
    }

    protected QueryResult<Capture> findCaptures_Locked(CaptureCriteria criteria) {
        return contextQueries.findCaptures(criteria);
    }

    protected QueryResult<Capture> findCaptures_Locked(ValueSource valueSource) {
        return contextQueries.findCaptures(valueSource);
    }

    protected QueryResult<Capture> findCaptures_Locked(IdentifiableTimeSeries timeSeries) {
        return contextQueries.findCaptures(timeSeries);
    }

    protected QueryResult<Capture> findAllCaptures_Locked() {
        return contextQueries.findAllCaptures();
    }

    protected QueryResult<ValueSource> findValueSources_Locked(CaptureCriteria criteria) {
        return contextQueries.findValueSources(criteria);
    }

    protected QueryResult<ValueSource> findValueSources_Locked(IdentifiableTimeSeries timeSeries) {
        return contextQueries.findValueSources(timeSeries);
    }

    protected QueryResult<ValueSource> findValueSources_Locked(String searchPattern) {
        return contextQueries.findValueSources(searchPattern);
    }

    protected QueryResult<ValueSource> findAllValueSources_Locked() {
        return contextQueries.findAllValueSources();
    }

    protected QueryResult<Scheduler> findAllSchedulers_Locked() {
        return contextQueries.findAllSchedulers();
    }

    protected QueryResult<Scheduler> findSchedulers_Locked(String searchPattern) {
        return contextQueries.findSchedulers(searchPattern);
    }

    protected QueryResult<Scheduler> findSchedulers_Locked(Triggerable triggerable) {
        return contextQueries.findSchedulers(triggerable);
    }

    protected <E> QueryResult<E> findAllChildren_Locked(Class<E> assignableToClass) {
        return contextQueries.findAll(assignableToClass);
    }

    protected <E> QueryResult<E> findAllChildren_Locked(String searchPattern, Class<E> assignableToClass) {
        return contextQueries.findAll(searchPattern, assignableToClass);
    }

    public String toString() {
        return "Context " + getId();
    }

    protected List<Identifiable> getChildren_Locked() {
        List<Identifiable> children = new ArrayList<Identifiable>();
        children.addAll(this.children);
        return children;
    }

    private <E extends Identifiable> void checkUniqueIdAndAdd(E identifiable) {
        if (childrenById.containsKey(identifiable.getId())) {
            throw new DuplicateIdException("id " + identifiable.getId() + " already exists in this context");
        } else {
            children.add(identifiable);
            childrenById.put(identifiable.getId(), identifiable);
        }
        identifiable.setParent(this);
    }

}
