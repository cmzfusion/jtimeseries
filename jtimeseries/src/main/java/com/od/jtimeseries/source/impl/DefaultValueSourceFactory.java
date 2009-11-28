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
package com.od.jtimeseries.source.impl;

import com.od.jtimeseries.source.*;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;
import com.od.jtimeseries.util.time.TimePeriod;

public class DefaultValueSourceFactory extends IdentifiableBase implements ValueSourceFactory {

    public DefaultValueSourceFactory() {
        this("DefaultValueSourceFactory", "DefaultValueSourceFactory");
    }

    public DefaultValueSourceFactory(String id, String description) {
        super(id, description);
    }

    public ValueRecorder createValueRecorder(String id, String description) {
        return new DefaultValueRecorder(id, description);
    }

    public QueueTimer createQueueTimer(String id, String description) {
        return new DefaultQueueTimer(id, description);
    }

    public Counter createCounter(String id, String description) {
        return new DefaultCounter(id, description);
    }

    public EventTimer createEventTimer(String id, String description) {
        return new DefaultEventTimer(id, description);
    }

    public TimedValueSource createTimedValueSource(String id, String description, ValueSupplier valueSupplier, TimePeriod timePeriod) {
        return new DefaultTimedValueSource(id, description, valueSupplier, timePeriod);
    }

}
