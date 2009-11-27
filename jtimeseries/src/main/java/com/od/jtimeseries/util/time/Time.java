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
package com.od.jtimeseries.util.time;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 09-Dec-2008
 * Time: 10:39:25
 */
public class Time {

    /**
     * @return number of milliseconds in the given number of seconds
     */
    public static TimePeriod milliseconds(int millis) {
        return new Millisecond(millis);
    }

    /**
     * @return number of milliseconds in the given number of seconds
     */
    public static TimePeriod seconds(int seconds) {
        return new Second(seconds);
    }

     /**
     * @return number of milliseconds in the given number of minutes
     */
    public static TimePeriod minutes(int minutes) {
        return new Minute(minutes);
    }

     /**
     * @return number of milliseconds in the given number of hours
     */
    public static TimePeriod hours(int hours) {
        return new Hour(hours);
    }

     /**
     * @return number of milliseconds in the given number of days
     */
    public static TimePeriod days(int days) {
        return new Day(days);
    }


}
