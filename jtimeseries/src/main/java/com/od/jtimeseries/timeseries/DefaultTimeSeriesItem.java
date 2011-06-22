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
package com.od.jtimeseries.timeseries;

import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03-Dec-2008
 * Time: 15:37:48
 *
 * A TimeSeriesItem with a fixed/immutable timestamp
 *
 * Equality is defined in terms of both timestamp and value
 *
 * Numeric value is also usually immutable - but if the Numeric value does change, be aware this could invalidate any
 * equality based collection (e.g. Set) in which the TimeSeriesItem is currently stored.
 */
public class DefaultTimeSeriesItem implements TimeSeriesItem {

    private final long timestamp;
    private final Numeric value;

    public DefaultTimeSeriesItem(long timeStamp, Numeric value) {
        this.timestamp = timeStamp;
        this.value = value;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public Numeric getValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value.doubleValue();
    }

    @Override
    public long longValue() {
        return value.longValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultTimeSeriesItem that = (DefaultTimeSeriesItem) o;

        if (timestamp != that.timestamp) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Item timestamp: " + timestamp + " value:" + value.toString();
    }
}
