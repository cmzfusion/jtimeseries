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
package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.source.ValueSupplier;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.numeric.LongNumeric;
import com.od.jtimeseries.component.managedmetric.AbstractManagedMetric;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2009
 * Time: 19:53:14
 * To change this template use File | Settings | File Templates.
 */
public class TotalSeriesMetric extends AbstractManagedMetric {

    private static final String id = "TotalSeries";
    private String parentContextPath;
    private TimeSeriesContext rootContext;
    private TimePeriod countPeriod;

    public TotalSeriesMetric(String parentContextPath, TimeSeriesContext rootContext) {
        this(parentContextPath, rootContext, DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS);
    }

    public TotalSeriesMetric(String parentContextPath, TimeSeriesContext rootContext, TimePeriod countPeriod) {
        this.parentContextPath = parentContextPath;
        this.rootContext = rootContext;
        this.countPeriod = countPeriod;
    }


    public String getSeriesId() {
        return id;
    }

    public String getParentContextPath() {
        return parentContextPath;
    }

    public void doInitializeMetric(TimeSeriesContext metricContext) {
        metricContext.createValueSupplierSeries(
            id,
            "Total number of series managed by the server",
            new TotalSeriesCountValueSupplier(),
            countPeriod
        );
    }

    private class TotalSeriesCountValueSupplier implements ValueSupplier {

        public Numeric getValue() {
            return LongNumeric.valueOf(rootContext.findAllTimeSeries().getNumberOfMatches());
        }
    }
}
