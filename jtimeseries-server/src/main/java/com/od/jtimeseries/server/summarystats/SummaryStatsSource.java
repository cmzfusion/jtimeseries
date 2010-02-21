package com.od.jtimeseries.server.summarystats;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2010
 * Time: 10:31:06
 * To change this template use File | Settings | File Templates.
 */
public interface SummaryStatsSource {

    List<SummaryStatsGroup> getSummaryStatsGroups();

}
