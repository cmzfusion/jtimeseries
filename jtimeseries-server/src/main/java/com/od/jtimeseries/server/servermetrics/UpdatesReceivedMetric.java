package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.message.AppendToSeriesMessageListener;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2009
 * Time: 19:47:38
 * To change this template use File | Settings | File Templates.
 */
public class UpdatesReceivedMetric extends ServerMetric {

    private static final TimePeriod countPeriod = Time.minutes(5);

    public String getSeriesId() {
        return "UdpSeriesUpdateCount";
    }

    public void setupSeries(TimeSeriesContext metricContext) {
        Counter counter = metricContext.createCounter(
            "UdpSeriesUpdateCount",
            "A count of the udp series updates received during the last " + countPeriod,
            CaptureFunctions.COUNT(countPeriod)
        );
        AppendToSeriesMessageListener.setUpdateMessagesReceivedCounter(counter);
    }

}
