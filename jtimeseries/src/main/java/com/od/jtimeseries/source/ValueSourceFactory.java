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
package com.od.jtimeseries.source;

import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.time.TimePeriod;

public interface ValueSourceFactory extends Identifiable {

    /**
     * @param path, full context path including id
     */
    ValueRecorder createValueRecorder(String path, String id, String description);

    /**
     * @param path, full context path including id
     */
    QueueTimer createQueueTimer(String path, String id, String description);

    /**
     * @param path, full context path including id
     */
    Counter createCounter(String path, String id, String description);

    /**
     * @param path, full context path including id
     */
    EventTimer createEventTimer(String path, String id, String description);

    /**
     * @param path, full context path including id
     */
    TimedValueSource createTimedValueSource(String path, String s, String description, ValueSupplier valueSupplier, TimePeriod timePeriod);
}
