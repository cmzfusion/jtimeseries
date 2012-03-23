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

import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.LongNumeric;
import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03-Dec-2008
 * Time: 15:37:48
 *
 * The default implementation for TimeSeriesItem
 * A TimeSeriesItem with a fixed/immutable timestamp
 * Equality is defined in terms of both timestamp and value
 */
public class Item implements TimeSeriesItem {

    private final long timestamp;
    private final Numeric value;

    public Item(long timestamp, double value) {
        this(timestamp, DoubleNumeric.valueOf(value));
    }

    public Item(long timestamp, long value) {
        this(timestamp, LongNumeric.valueOf(value));
    }

    public Item(long timeStamp, Numeric value) {
        this.timestamp = timeStamp;
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Numeric getValue() {
        return value;
    }

    public Numeric getValue(int valueIndex) {
        return valueIndex == 0 ? value : null;
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    public long longValue() {
        return value.longValue();
    }

    public int getValueCount() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item that = (Item) o;

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
