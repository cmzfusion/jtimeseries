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
 * Time: 12:37:08
 */
public class LongNumeric implements Numeric {

    private long l;

    public LongNumeric(long l) {
        this.l = l;
    }

    public LongNumeric(int l) {
        this.l = l;
    }

    public double doubleValue() {
        return l;
    }

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
	    if (obj instanceof LongNumeric) {
	        return l == ((LongNumeric)obj).l;
        }
        return false;
    }

}
