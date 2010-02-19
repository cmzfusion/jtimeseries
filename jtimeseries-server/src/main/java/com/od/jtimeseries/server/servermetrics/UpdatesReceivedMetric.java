package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.message.AppendToSeriesMessageListener;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2009
 * Time: 19:47:38
 * To change this template use File | Settings | File Templates.
 */
public class UpdatesReceivedMetric extends AbstractServerMetric {

    private static final String SERIES_ID = "UdpSeriesUpdates";
    private TimePeriod countPeriod;
    private String parentContextPath;

    public UpdatesReceivedMetric(String parentContextPath) {
        this(parentContextPath, DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS);
    }

    public UpdatesReceivedMetric(String parentContextPath, TimePeriod countPeriod) {
        this.parentContextPath = parentContextPath;
        this.countPeriod = countPeriod;
    }

    public String getSeriesId() {
        return SERIES_ID;
    }

    public String getParentContextPath() {
        return parentContextPath;
    }

    public void doInitializeMetric(TimeSeriesContext metricContext) {
        Counter counter = metricContext.createCounterSeries(
            SERIES_ID,
            "A count of series data update UDP datagram messages received",
            CaptureFunctions.COUNT(countPeriod)
        );
        AppendToSeriesMessageListener.setUpdateMessagesReceivedCounter(counter);
    }

}
