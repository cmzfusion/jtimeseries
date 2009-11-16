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
 */
public class TimeSeriesItem {

    private long timestamp;
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

    public int hashCode() {
        int result = 17;
        result = 31 * result + (int)(timestamp ^ (timestamp >>> 32));
        result = 31 * result + value.hashCode();
        return result;
    }

    public boolean equals(Object o) {
        boolean result = false;
        if ( o == this ) {
            result = true;
        } else {
            if ( o instanceof TimeSeriesItem ) {
                TimeSeriesItem i = (TimeSeriesItem)o;
                result = i.getTimestamp() == getTimestamp() &&
                        i.getValue().equals(getValue());
            }
        }
        return result;
    }

    public String toString() {
        return "Item timestamp: " + timestamp + " value:" + value.toString();
    }
}
