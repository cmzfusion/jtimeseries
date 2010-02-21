package com.od.jtimeseries.server.summarystats;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2010
 * Time: 12:53:14
 * To change this template use File | Settings | File Templates.
 */
public class TodayOnlySummaryStatistic extends DefaultSummaryStatistic {

    private static final int MILLIS_IN_DAY = 24 * 60 * 60 * 1000;

    public TodayOnlySummaryStatistic(String name, AggregateFunction function) {
        super(name, function);
    }

    protected long getEndTime() {
        Calendar c = getStartOfToday();
        return c.getTimeInMillis() + MILLIS_IN_DAY;
    }


    protected long getStartTime() {
        Calendar c = getStartOfToday();
        return c.getTimeInMillis();
    }

    private Calendar getStartOfToday() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        return c;
    }

}
