package com.od.jtimeseries.server.summarystats;

import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2010
 * Time: 10:37:18
 * To change this template use File | Settings | File Templates.
 */
public class AggregateFunctionSummaryStatistic implements SummaryStatistic {

    private final AggregateFunction function;
    private long startTime;
    private long endTime;

    public AggregateFunctionSummaryStatistic(AggregateFunction function, long startTime, long endTime) {
        this.function = function;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Numeric calculateSummaryStatistic(TimeSeries timeSeries) {
        AggregateFunction f = function.newInstance();  //not supporting chaining functions
        synchronized (timeSeries) {
            for (TimeSeriesItem i : timeSeries) {
                long timestamp = i.getTimestamp();
                if ( timestamp >= startTime && timestamp <= endTime)
                f.addValue(i.getValue());
            }
        }
        return f.calculateAggregateValue();
    }
}
