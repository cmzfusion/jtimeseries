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
package com.od.jtimeseries.timeseries;

import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03-Dec-2008
 * Time: 15:37:48
 *
 * TimeSeries are made up of TimeSeriesItem, each TimeSeriesItem has a timestamp with an associated value
 *
 * The timestamp of a TimeSeriesItem is fixed, but it is permitted for the associated value to change
 */
public class TimeSeriesItem {

    private final long timestamp;
    private Numeric value;

    public TimeSeriesItem(long timeStamp, Numeric value) {
        this.timestamp = timeStamp;
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Numeric getValue() {
        return value;
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    public long longValue() {
        return value.longValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeSeriesItem that = (TimeSeriesItem) o;

        if (timestamp != that.timestamp) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (timestamp ^ (timestamp >>> 32));
    }

    public String toString() {
        return "Item timestamp: " + timestamp + " value:" + value.toString();
    }
}
