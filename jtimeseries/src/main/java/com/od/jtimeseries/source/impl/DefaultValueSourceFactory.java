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
package com.od.jtimeseries.source.impl;

import com.od.jtimeseries.source.*;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.time.TimePeriod;

public class DefaultValueSourceFactory extends IdentifiableBase implements ValueSourceFactory {

    public DefaultValueSourceFactory() {
        super(ID, ID);
        setDescription(getClass().getName());
    }

    protected ValueRecorder createValueRecorder(Identifiable parent, String path, String id, String description, Object... parameters) {
        return new DefaultValueRecorder(id, description);
    }

    protected QueueTimer createQueueTimer(Identifiable parent, String path, String id, String description, Object... parameters) {
        return new DefaultQueueTimer(id, description);
    }

    protected Counter createCounter(Identifiable parent, String path, String id, String description, Object... parameters) {
        return new DefaultCounter(id, description);
    }

    protected EventTimer createEventTimer(Identifiable parent, String path, String id, String description, Object... parameters) {
        return new DefaultEventTimer(id, description);
    }

    protected TimedValueSupplier createTimedValueSupplier(Identifiable parent, String path, String id, String description, ValueSupplier valueSupplier, TimePeriod timePeriod, Object... parameters) {
        return new DefaultTimedValueSupplier(id, description, valueSupplier, timePeriod);
    }

    public ValueSource createValueSource(Identifiable parent, String path, String id, String description, Class classType, Object... parameters) {
        if ( classType.isAssignableFrom(ValueRecorder.class)) {
            return createValueRecorder(parent, path, id, description, parameters);
        } else if ( classType.isAssignableFrom(QueueTimer.class)) {
            return createQueueTimer(parent, path, id, description, parameters);
        } else if ( classType.isAssignableFrom(Counter.class)) {
            return createCounter(parent, path, id, description, parameters);
        } else if ( classType.isAssignableFrom(EventTimer.class)) {
            return createEventTimer(parent, path, id, description, parameters);
        } else if ( classType.isAssignableFrom(TimedValueSupplier.class)) {
            return createTimedValueSupplier(parent, path, id, description, (ValueSupplier)parameters[0], (TimePeriod)parameters[1], parameters);
        } else {
            throw new UnsupportedOperationException("Cannot create ValueSource of class type " + classType);
        }
    }
}
