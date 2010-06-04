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
package com.od.jtimeseries.component.managedmetric;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 06-Dec-2009
 * Time: 12:04:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractManagedMetric implements ManagedMetric {

    protected static final TimePeriod DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS = Time.minutes(5);

    public final void initializeMetrics(TimeSeriesContext rootContext)  {
        TimeSeriesContext c = rootContext.createContextForPath(getParentContextPath());
        doInitializeMetric(c);
    }

    protected abstract void doInitializeMetric(TimeSeriesContext targetContext);

    /**
     * @return the path to the context in which the metric will be created
     */
    protected abstract String getParentContextPath();

    /**
     * @return the id of the metric which will be created
     */
    protected abstract String getSeriesId();

    public String toString() {
        return getParentContextPath() + "." + getSeriesId();
    }
}
