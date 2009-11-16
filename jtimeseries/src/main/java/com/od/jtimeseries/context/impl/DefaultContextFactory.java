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
import com.od.jtimeseries.capture.impl.DefaultCaptureScheduler;
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
     *  other child contexts will inherit, unless they provide a local factory which overrides the root's default
     */
    public TimeSeriesContext createRootContext(String description) {
        TimeSeriesContext c = createContext(null, JTimeSeriesConstants.DEFAULT_ROOT_CONTEXT_ID, description);
        c.setContextFactory(createRootContextFactory());
        c.setScheduler(createRootScheduler());
        c.setTimeSeriesFactory(createRootTimeSeriesFactory());
        c.setValueSourceFactory(createRootValueSourceFactory());
        c.setCaptureFactory(createRootCaptureFactory());
        setRootStartCapturesImmediatelyProperty(c);
        return c;
    }

    protected void setRootStartCapturesImmediatelyProperty(TimeSeriesContext c) {
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

    protected DefaultCaptureScheduler createRootScheduler() {
        return new DefaultCaptureScheduler(JTimeSeriesConstants.DEFAULT_ROOT_CONTEXT_ID + " Scheduler", "Root Context Scheduler");
    }

    protected DefaultContextFactory createRootContextFactory() {
        return this;
    }

}
