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
import com.od.jtimeseries.capture.function.AbstractCaptureFunction;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.numeric.DoubleNumeric;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 08-Jan-2009
 * Time: 10:46:56
 *
 * Similar to Change, a measures the net change over a period, but expresses the value as a mean
 * change over a shorted time period. For example, we may wish to make one measurement per hour
 * (a count of events), but express that as a mean rate per minute.
 */
class MeanChangeFunction extends AbstractCaptureFunction {

    private TimePeriod timeIntervalToExpressAverage;
    private AggregateFunction prototype;

    public MeanChangeFunction(TimePeriod capturePeriod, TimePeriod periodInWhichToExpressValue) {
        super(capturePeriod);
        double divisor = calculateDivisor(periodInWhichToExpressValue);
        prototype = new MeanChangeAggregateFunction(divisor);
    }

    public MeanChangeFunction(TimePeriod capturePeriod, TimePeriod periodInWhichToExpressValue, Numeric initialValue) {
        super(capturePeriod);
        double divisor = calculateDivisor(periodInWhichToExpressValue);
        prototype = new MeanChangeAggregateFunction(divisor, initialValue);
    }

    private double calculateDivisor(TimePeriod periodInWhichToExpressValue) {
        this.timeIntervalToExpressAverage = periodInWhichToExpressValue;
        return calculateDivisor();
    }

    public AggregateFunction nextFunctionInstance() {
        prototype = prototype.nextInstance();
        return prototype;
    }

    private double calculateDivisor() {
        return ((double)getCapturePeriodInMillis())/timeIntervalToExpressAverage.getLengthInMillis();
    }

    protected String doGetDescription() {
        return prototype.getDescription();
    }

    /**
    * Created by IntelliJ IDEA.
    * User: nick
    * Date: 10-Dec-2009
    * Time: 23:20:52
    *
    * A wrapper around ChangeFunction, to implement the division
    */
    static class MeanChangeAggregateFunction implements AggregateFunction {

        private double divisor;
        private ChangeFunction c;

        /**
         * Change function where initial value is specified up front
         */
        public MeanChangeAggregateFunction(double divisor, Numeric initialValue) {
            this.divisor = divisor;
            c = new ChangeFunction(initialValue);
        }

        /**
         * Change function where initial value is defined by the first value received from the value source
         */
        public MeanChangeAggregateFunction(double divisor) {
            this.divisor = divisor;
            c = new ChangeFunction();
        }

        public void addValue(Numeric value) {
            c.addValue(value);
        }

        public void addValue(double value) {
            c.addValue(value);
        }

        public void addValue(long value) {
            c.addValue(value);
        }

        public Numeric calculateAggregateValue() {
            double result = c.calculateAggregateValue().doubleValue();
            return new DoubleNumeric(result / divisor);
        }

        public String getDescription() {
            return "MeanChange";
        }

        public void clear() {
            c.clear();
        }

        public AggregateFunction nextInstance() {
            return new MeanChangeAggregateFunction(divisor, c.getLastValue());
        }
    }
}
