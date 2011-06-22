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
package com.od.jtimeseries.util.time;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 08-Jan-2009
 * Time: 11:29:17
 */
class Day extends AbstractTimePeriod {

    private static final int millis = 24 * 60 * 60 * 1000;

    public Day(long days) {
        super(millis * days);
    }

    public String toString() {
        long days = getLengthInMillis() / millis;
        return days + "day";
    }
}
