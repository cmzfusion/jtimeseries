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
package com.od.jtimeseries.capture.function;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.numeric.Numeric;

public class CaptureFunctions {

    public static CaptureFunction MAX(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.MAX());
    }

    public static CaptureFunction MIN(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.MIN());
    }

    public static CaptureFunction MEAN(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.MEAN());
    }

    public static CaptureFunction SUM(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.SUM());
    }

    /**
     * @return a Delta function which measures change starting from zero
     */
    public static CaptureFunction DELTA(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.DELTA());
    }

    /**
     * (initialValue may be Double.NaN if you want to take the initial value from the first recorded value.
     * This sometimes makes sense if you will, for example, get a very large initial value, but are only interested in small
     * changes thereafter)
     * @return A Delta function which measures change starting from initialValue.
     */
    public static CaptureFunction DELTA(TimePeriod timePeriod, Numeric initialValue) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.DELTA(initialValue));
    }

    public static CaptureFunction MEAN_DELTA(TimePeriod timePeriod, TimePeriod timeIntervalToExpressCount) {
        return new MeanDeltaCaptureFunction(timePeriod, timeIntervalToExpressCount);
    }


}
