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
package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-Dec-2008
 * Time: 12:02:05
 *
 * DefaultTimeSeries is an IndexedTimeSeries based on an array.
 * It should have good performance for append or prepend operations (and for removing items at either end of the series), and such should
 * exhibit very good performance for retrieving items based on their index or position in the series
 *
 * Conversely, operations which insert or remove items from the middle of the series will have poor performance
 **/
public class DefaultTimeSeries extends AbstractIndexedTimeSeries {

    public DefaultTimeSeries() {
    }

    public DefaultTimeSeries(Collection<TimeSeriesItem> items) {
        super(items);
    }

     public DefaultTimeSeries(TimeSeries series) {
        super(series.getSnapshot());
    }
}
