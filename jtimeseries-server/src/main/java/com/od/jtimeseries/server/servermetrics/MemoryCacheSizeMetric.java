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
package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.component.managedmetric.AbstractManagedMetric;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.server.timeseries.TimeSeriesCache;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;

import static com.od.jtimeseries.capture.function.CaptureFunctions.LATEST;
import static com.od.jtimeseries.capture.function.CaptureFunctions.MEAN_COUNT_OVER;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2009
 * Time: 19:33:20
 * To change this template use File | Settings | File Templates.
 */
public class MemoryCacheSizeMetric extends AbstractManagedMetric {

    private static final String id = "MemoryCacheSize";
    private String parentContextPath;
    private TimeSeriesCache cache;
    private TimePeriod timePeriod;

    public MemoryCacheSizeMetric(String parentContextPath, TimeSeriesCache cache) {
        this(parentContextPath, cache, DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS);
    }

    public MemoryCacheSizeMetric(String parentContextPath, TimeSeriesCache cache, TimePeriod timePeriod) {
        this.parentContextPath = parentContextPath;
        this.cache = cache;
        this.timePeriod = timePeriod;
    }

   protected String getSeriesPath() {
        return parentContextPath + Identifiable.NAMESPACE_SEPARATOR + id;
    }

    public void doInitializeMetric(TimeSeriesContext rootContext, String path) {
        Counter c = rootContext.createCounterSeries(
            path,
            "Size of timeseries Memory Cache",
            LATEST(timePeriod)
        );
        cache.setCacheSizeCounter(c);
    }
}
