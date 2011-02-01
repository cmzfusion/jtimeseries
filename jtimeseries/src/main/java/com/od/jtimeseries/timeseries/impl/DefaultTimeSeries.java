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
package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.TimeSeriesItem;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-Dec-2008
 * Time: 12:02:05
 *
 * A List based series which has an unlimited length
 *
 * This default implementation should have very good performance for appends or prepends
 * (and for removing items at either end), and supports RandomAccess to list elements
 * by list index, since the underlying datastructure is an array-based dequeue
 *
 * The performance for insert operations
 */
public class DefaultTimeSeries extends AbstractListTimeSeries {

    public DefaultTimeSeries() {
    }

    public DefaultTimeSeries(Collection<TimeSeriesItem> items) {
        super(items);
    }
}
