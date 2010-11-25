package com.od.jtimeseries.ui.timeseries;

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2010
 * Time: 16:53:05
 * To change this template use File | Settings | File Templates.
 */
public interface ChartSeriesListener {

    void chartSeriesChanged(ChartSeriesEvent chartSeriesEvent);
}
