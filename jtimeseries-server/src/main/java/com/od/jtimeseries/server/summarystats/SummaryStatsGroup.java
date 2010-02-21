package com.od.jtimeseries.server.summarystats;

import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2010
 * Time: 10:32:03
 * To change this template use File | Settings | File Templates.
 */
public class SummaryStatsGroup {

    private TimePeriod recalculationFrequency;

    public SummaryStatsGroup(TimePeriod recalculationFrequency) {
        this.recalculationFrequency = recalculationFrequency;
    }

    public TimePeriod getRecalculationFrequency() {
        return recalculationFrequency;
    }
}
