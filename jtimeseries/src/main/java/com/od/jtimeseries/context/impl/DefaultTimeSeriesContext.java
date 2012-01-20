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
import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.capture.impl.DefaultCaptureFactory;
import com.od.jtimeseries.context.ContextFactory;
import com.od.jtimeseries.context.ContextProperties;
import com.od.jtimeseries.context.ContextQueries;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.identifiable.IdentifiablePathUtils;
import com.od.jtimeseries.identifiable.QueryResult;
import com.od.jtimeseries.scheduling.GroupByPeriodScheduler;
import com.od.jtimeseries.scheduling.Scheduler;
import com.od.jtimeseries.scheduling.Triggerable;
import com.od.jtimeseries.source.ValueSource;
import com.od.jtimeseries.source.ValueSourceFactory;
import com.od.jtimeseries.source.impl.DefaultValueSourceFactory;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeriesFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-Dec-2008
 * Time: 10:17:44
 */
public class DefaultTimeSeriesContext extends LockingTimeSeriesContext {

    private ContextQueries contextQueries = new DefaultContextQueries(this);
    private DefaultMetricCreator defaultMetricCreator = new DefaultMetricCreator(this);

    /**
     * Create a context without a parent (i.e. a root context), using a default id and description, which has its own
     * scheduler and factories for series, value sources, captures and child contexts, and sets default context properties.
     * Child contexts generally inherit these from the root context, although any set up at a child level override the parent.
     */
    public DefaultTimeSeriesContext() {
        this(IdentifiablePathUtils.DEFAULT_ROOT_CONTEXT_ID, IdentifiablePathUtils.DEFAULT_ROOT_CONTEXT_ID);
    }

    /**
     * Create a (root) context which has its own scheduler and factories for
     * series, value sources, captures and child contexts, and sets default context properties, and perform initialization
     * This is generally useful for creating a 'root' context - child contexts generally reuse factories from the root context,
     * although any set up at a child level override the parent.
     */
    public DefaultTimeSeriesContext(String id, String description) {
        this(id, description, true);
    }

    public DefaultTimeSeriesContext(String id, String description, boolean createRootContextResources) {
        this(id, description, createRootContextResources, createRootContextResources);
    }

    /**
     * Create a context, specifiying whether this is to be a root context
     * @param createRootContextResources, whether to create the factories etc. generally required by a root context
     * @param initialize, whether to perform subsequent initialization (e.g. starting scheduler)
     */
    public DefaultTimeSeriesContext(String id, String description, boolean createRootContextResources, boolean initialize) {
        super(id, description);
        checkId(id);
        if (createRootContextResources) {
            createRootContextResources();
        }
        if (initialize) {
            initializeRootContext();
        }
    }

    protected void initializeRootContext() {
        getScheduler().start(); //should not create any threads until a Triggerable is added
    }

    protected void createRootContextResources() {
        setScheduler(new GroupByPeriodScheduler());
        setTimeSeriesFactory(new DefaultTimeSeriesFactory());
        setValueSourceFactory(new DefaultValueSourceFactory());
        setCaptureFactory(new DefaultCaptureFactory());
        setContextFactory(new DefaultContextFactory());
        setDefaultContextProperties();
    }

    private void setDefaultContextProperties() {
        setProperty(ContextProperties.START_CAPTURES_IMMEDIATELY_PROPERTY, "true");
    }

    protected TimeSeriesContext addChild_Locked(Identifiable... identifiables) {
        checkIdCharacters(identifiables);
        for ( Identifiable i : identifiables) {
            if ( i instanceof Scheduler) {
                doSetScheduler((Scheduler)i);
            }
            if ( i instanceof ValueSourceFactory) {
                remove(ValueSourceFactory.ID);
            }
            if ( i instanceof ContextFactory) {
                remove(ContextFactory.ID);
            }
            if ( i instanceof CaptureFactory) {
                remove(CaptureFactory.ID);
            }
            if ( i instanceof TimeSeriesFactory) {
                remove(TimeSeriesFactory.ID);
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

    private void doSetScheduler(Scheduler scheduler) {
        if ( isSchedulerStarted() ) {
            throw new UnsupportedOperationException("Cannot setScheduler while the existing scheduler is running");
        } else {
            Scheduler oldSchedulerForThisContext = remove(Scheduler.ID, Scheduler.class);
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

    protected <E extends Identifiable> E doCreate(String id, String description, Class<E> classType, Object... parameters) {
        E result;
        if ( IdentifiableTimeSeries.class.isAssignableFrom(classType)) {
            result = (E)getTimeSeriesFactory().createTimeSeries(this, getPathForChild(id), id, description, classType, parameters);
        }
        else if ( ValueSource.class.isAssignableFrom(classType) ) {
            result = createValueSource(id, description, classType, parameters);
        }
        else if ( Capture.class.isAssignableFrom(classType)) {
            result = (E)getCaptureFactory().createCapture(this, getPathForChild(id), id, (ValueSource)parameters[0], (IdentifiableTimeSeries)parameters[1], (CaptureFunction)parameters[2], classType, parameters);
        }
        else if ( Identifiable.class.isAssignableFrom(classType)) { //default to creating a child context so recursive creation works
            result = (E)getContextFactory().createContext(this, id, description, classType, parameters);
        }
        else {
            throw new UnsupportedOperationException("Cannot create identifiable of class " + classType);
        }
        return result;
    }

    //here we have to cater for just creating the individual source, or creating a capture and series as well using MetricCreator,
    //depending on whether there are CaptureFunctions as parameters
    private <E extends Identifiable> E createValueSource(String id, String description, Class<E> classType, Object... parameters) {
        E result;
        if ( parameters.length == 0 || ! (parameters[0] instanceof CaptureFunction)) {
            result = (E)getValueSourceFactory().createValueSource(this, getPathForChild(id), id, description, classType, parameters);
        } else {
            List<CaptureFunction> functions = new ArrayList<CaptureFunction>();
            List<Object> params = new ArrayList<Object>();
            for ( Object o : parameters ) {
                if ( o instanceof CaptureFunction ) {
                    functions.add((CaptureFunction)o);
                } else {
                    params.add(o);
                }
            }
            result = (E)defaultMetricCreator.createSourceCaptureAndSeries(this, getPathForChild(id), id, description, classType, functions, params.toArray(new Object[params.size()]));
        }
        return result;
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

    public String toString() {
        return "Context " + getId();
    }

}
