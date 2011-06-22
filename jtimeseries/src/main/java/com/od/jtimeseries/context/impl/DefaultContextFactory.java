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

import com.od.jtimeseries.context.ContextFactory;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;
import com.od.jtimeseries.util.identifiable.Identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 17-Feb-2009
 * Time: 14:03:35
 *
 * A factory for contexts, which can be added to a context to handle the creation of child contexts
 */
public class DefaultContextFactory extends IdentifiableBase implements ContextFactory{

    public DefaultContextFactory() {
        super(ID, ID);
        setDescription(getClass().getName());
    }

    public <E extends Identifiable> E createContext(TimeSeriesContext parent, String id, String description, Class<E> classType, Object... parameters) {
        if (classType.isAssignableFrom(DefaultTimeSeriesContext.class)) {
            return (E)new DefaultTimeSeriesContext(id, description, false);
        } else {
            throw new UnsupportedOperationException("Cannot create a context of type " + classType);
        }
    }
}
