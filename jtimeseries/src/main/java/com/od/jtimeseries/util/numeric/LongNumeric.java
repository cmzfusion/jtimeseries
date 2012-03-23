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
package com.od.jtimeseries.util.numeric;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-Dec-2008
 * Time: 12:37:08
 *
 *  An immutable Numeric, which uses a long internally
 */
public class LongNumeric implements Numeric {

    public static final LongNumeric ZERO = new LongNumeric(0);

    //there is no sensible representation of NaN as a primitive long
    public static final LongNumeric NaN = new LongNumeric(Long.MIN_VALUE) {
        public long longValue() {
            throw new UnsupportedOperationException("Cannot return a long value for NaN");
        }
    };

    private long l;

    private LongNumeric(long l) {
        this.l = l;
    }

    public double doubleValue() {
        return isNaN() ? Double.NaN : (double)l;
    }

    public boolean isNaN() {
        return l == Long.MIN_VALUE;
    }

    /**
     * @return long value as a primitive long
     * @throws UnsupportedOperationException if you call this method when this value is either the LongNumeric.NULL or LongNumeric.NaN value
     */
    public long longValue() {
        return l;
    }

    public String toString() {
        return String.valueOf(l);
    }

    public int hashCode() {
	    return (int)(l ^ (l >>> 32));
    }

    public boolean equals(Object obj) {
        return obj instanceof LongNumeric && l == ((LongNumeric) obj).l;
    }

    public static LongNumeric valueOf(long l) {
        LongNumeric result;
        if ( l == 0) {
            result = ZERO;
        } else {
            result = new LongNumeric(l);
        }
        return result;
    }

}
