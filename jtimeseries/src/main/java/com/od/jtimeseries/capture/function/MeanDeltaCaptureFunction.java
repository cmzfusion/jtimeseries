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

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 08-Jan-2009
 * Time: 10:46:56
 *
 * Capture, for example, the mean count per second, or the mean count per minute
 *
 * This is useful when you want to only measure an attribute infrequently, but express the value in
 * different terms (e.g. measure once every 30 mins, but express the result in minutes)
 */
public class MeanDeltaCaptureFunction extends AbstractCaptureFunction {

    private TimePeriod timeIntervalToExpressAverage;
    private double divisor;
    private AggregateFunction prototype;

    public MeanDeltaCaptureFunction(TimePeriod capturePeriod, TimePeriod timePeriodInWhichToExpressCount) {
        super(capturePeriod);
        this.timeIntervalToExpressAverage = timePeriodInWhichToExpressCount;
        this.divisor = calculateDivisor();
        prototype = new MeanDeltaFunctionImpl(divisor);
    }

    public AggregateFunction nextFunctionInstance() {
        prototype = prototype.nextInstance();
        return prototype;
    }

    private double calculateDivisor() {
        return ((double)getCapturePeriodInMillis())/timeIntervalToExpressAverage.getLengthInMillis();
    }

    public String doGetDescription() {
        return "Delta per " + timeIntervalToExpressAverage;
    }

    private class MeanDeltaFunctionImpl implements AggregateFunction {

        private double divisor;
        private AggregateFunction d = AggregateFunctions.DELTA();

        public MeanDeltaFunctionImpl(double divisor) {
            this.divisor = divisor;
        }

        public void addValue(Numeric value) {
            d.addValue(value);
        }

        public void addValue(double value) {
            d.addValue(value);
        }

        public void addValue(long value) {
            d.addValue(value);
        }

        public Numeric calculateAggregateValue() {
            double result = d.calculateAggregateValue().doubleValue();
            return new DoubleNumeric(result / divisor);
        }

        public String getDescription() {
            return doGetDescription();
        }

        public void clear() {
            d.clear();
        }

        public AggregateFunction nextInstance() {
            return new MeanDeltaFunctionImpl(divisor);
        }
    }
}
