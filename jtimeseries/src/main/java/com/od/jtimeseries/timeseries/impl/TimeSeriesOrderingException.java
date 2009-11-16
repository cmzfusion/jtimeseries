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

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Mar-2009
 * Time: 18:18:35
 *
 * A RuntimeException generated when an request is made via List interface insert methods
 * which would corrupt the ordering of the items in a TimeSeries
 */
public class TimeSeriesOrderingException extends IllegalArgumentException {

    private int index;
    private long timestamp;

    public TimeSeriesOrderingException(int index, long timestamp) {
        super("Cannot insert an item with timestamp " + timestamp +
              " at index " + index + " without invalidating the TimeSeries ordering");
        this.index = index;
        this.timestamp = timestamp;
    }

    public int getIndex() {
        return index;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
