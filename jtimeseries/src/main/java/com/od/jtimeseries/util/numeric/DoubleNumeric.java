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
 * Time: 12:38:21
 *
 * An immutable Numeric, based on a double primitive
 */
public class DoubleNumeric implements Numeric {

    public static final DoubleNumeric ZERO = new DoubleNumeric(0);

    /**
     * Use this to represent, a value could not be calculated
     */
    public static final DoubleNumeric NaN = new DoubleNumeric(Double.NaN);

    private final double d;

    private DoubleNumeric(double d) {
        this.d = d;
    }

    public double doubleValue() {
        return d;
    }

    public boolean isNaN() {
        return Double.isNaN(d);
    }

    public long longValue() {
        if ( Double.isNaN(d) ) {
            throw new UnsupportedOperationException("Cannot convert a NaN Numeric to primitive long");
        } else {
            return (long)d;
        }
    }

    public String toString() {
        return String.valueOf(d);
    }

    //from java.lang.Double
    public boolean equals(Object obj) {
        return (obj instanceof DoubleNumeric)
               && (Double.doubleToLongBits(((DoubleNumeric)obj).d) ==
                  Double.doubleToLongBits(d));
    }

    //from java.lang.Double
    public int hashCode() {
        long bits = Double.doubleToLongBits(d);
        return (int)(bits ^ (bits >>> 32));
    }

    public static DoubleNumeric valueOf(double d) {
        DoubleNumeric result;
        if ( d == 0) {
            result = ZERO;
        } else if ( Double.isNaN(d) ) {
            result = NaN;
        } else {
            result = new DoubleNumeric(d);
        }
        return result;
    }

}
