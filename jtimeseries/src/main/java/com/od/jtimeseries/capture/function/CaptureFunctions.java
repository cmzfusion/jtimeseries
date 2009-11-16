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

    public static CaptureFunction COUNT(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.COUNT());
    }

    public static CaptureFunction MEAN_COUNT(TimePeriod timePeriod, TimePeriod timeIntervalToExpressCount) {
        return new MeanCountCaptureFunction(timePeriod, timeIntervalToExpressCount);
    }


}
