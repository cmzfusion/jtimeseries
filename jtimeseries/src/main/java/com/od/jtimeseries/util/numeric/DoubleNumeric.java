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
package com.od.jtimeseries.util.numeric;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-Dec-2008
 * Time: 12:38:21
 */
public class DoubleNumeric implements Numeric {

    private double d;

    public DoubleNumeric(double d) {
        this.d = d;
    }

    public double doubleValue() {
        return d;
    }

    public long longValue() {
        return Double.valueOf(d).longValue();
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

}
