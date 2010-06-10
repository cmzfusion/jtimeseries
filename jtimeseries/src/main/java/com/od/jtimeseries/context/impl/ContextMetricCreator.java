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

import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.source.*;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.identifiable.Identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 28-Nov-2009
 * Time: 21:06:54
 *
 */
public interface ContextMetricCreator {

    ValueRecorder createValueRecorderSeries(Identifiable parent, String path, String id, String description, CaptureFunction... captureFunctions);

    QueueTimer createQueueTimerSeries(Identifiable parent, String path, String id, String description, CaptureFunction... captureFunctions);

    Counter createCounterSeries(Identifiable parent, String path, String id, String description, CaptureFunction... captureFunctions);

    EventTimer createEventTimerSeries(Identifiable parent, String path, String id, String description, CaptureFunction... captureFunctions);

    TimedValueSupplier createValueSupplierSeries(Identifiable parent, String path,String id, String description, ValueSupplier valueSupplier, TimePeriod timePeriod);

}
