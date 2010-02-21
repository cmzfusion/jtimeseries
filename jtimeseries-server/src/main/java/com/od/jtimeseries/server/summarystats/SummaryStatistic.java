package com.od.jtimeseries.server.summarystats;

import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.timeseries.TimeSeries;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2010
 * Time: 10:34:18
 * To change this template use File | Settings | File Templates.
 */
public interface SummaryStatistic {

    public Numeric calculateSummaryStatistic(TimeSeries timeSeries);

}
