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

import com.od.jtimeseries.capture.impl.DefaultCaptureFactory;
import com.od.jtimeseries.scheduling.DefaultScheduler;
import com.od.jtimeseries.context.ContextFactory;
import com.od.jtimeseries.context.ContextProperties;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.source.impl.DefaultValueSourceFactory;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeriesFactory;
import com.od.jtimeseries.util.JTimeSeriesConstants;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 17-Feb-2009
 * Time: 14:03:35
 *
 * A factory for contexts, which can be added to a context to handle the creation of child contexts
 *
 * A ContextFactory can also be used to create a root context, in which case it should usually register itself
 * as the context factory for the root. This may be desirable to ensure the root context is of the same class
 * type as all its child contexts, in cases were a custom context type is required.
 *
 * The default implementation creates contexts of class DefaultTimeSeriesContext
 * Subclasses may override this to create contexts of a custom class, and optionally override the methods
 * which create the various resources to register with the root context (e.g. schedulers, factories for
 * timeseries etc).
 */
public class DefaultContextFactory extends IdentifiableBase implements ContextFactory{

    public DefaultContextFactory() {
        this("DefaultContextFactory", "DefaultContextFactory");
    }

    public DefaultContextFactory(String id, String description) {
        super(id, description);
    }

    public TimeSeriesContext createContext(TimeSeriesContext parent, String id, String description) {
        return new DefaultTimeSeriesContext(parent, id, description);
    }

    /**
     *  The root context creation differs because we need to set up factories and properties which
     *  child contexts will inherit
     */
    public TimeSeriesContext createRootContext(String id, String description) {
        TimeSeriesContext c = createContext(null, id, description);
        c.setContextFactory(createRootContextFactory());
        c.setScheduler(createRootScheduler());
        c.setTimeSeriesFactory(createRootTimeSeriesFactory());
        c.setValueSourceFactory(createRootValueSourceFactory());
        c.setCaptureFactory(createRootCaptureFactory());
        setRootContextProperties(c);
        return c;
    }

    protected void setRootContextProperties(TimeSeriesContext c) {
        c.setProperty(ContextProperties.START_CAPTURES_IMMEDIATELY_PROPERTY, "true");
    }

    protected DefaultCaptureFactory createRootCaptureFactory() {
        return new DefaultCaptureFactory();
    }

    protected DefaultValueSourceFactory createRootValueSourceFactory() {
        return new DefaultValueSourceFactory();
    }

    protected DefaultTimeSeriesFactory createRootTimeSeriesFactory() {
        return new DefaultTimeSeriesFactory();
    }

    protected DefaultScheduler createRootScheduler() {
        return new DefaultScheduler(JTimeSeriesConstants.DEFAULT_ROOT_CONTEXT_ID + " Scheduler", "Root Context Scheduler");
    }

    protected DefaultContextFactory createRootContextFactory() {
        //usually this factory will be used for the root and all the child contexts created.
        return this;
    }

}
