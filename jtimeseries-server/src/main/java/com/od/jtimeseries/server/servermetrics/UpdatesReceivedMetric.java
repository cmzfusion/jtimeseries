package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.capture.impl.DefaultTimedCapture;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.AppendToSeriesMessageListener;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.impl.DefaultCounter;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
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

    public static final String COUNT_OF_UDP_SERIES_UPDATES_ID = "UdpSeriesUpdateCount";
    private static final String COUNT_OF_UDP_SERIES_UPDATES_DESC =
            "A count of the udp series updates received during the last " + countPeriod;

    public TimePeriod getSchedulingPeriod() {
        return null;
    }

    public String getSeriesId() {
        return COUNT_OF_UDP_SERIES_UPDATES_ID;
    }

    public String getMetricDescription() {
        return COUNT_OF_UDP_SERIES_UPDATES_DESC;
    }

    public void setupSeries(TimeSeriesContext metricContext, IdentifiableTimeSeries timeSeries) {
        //n.b. usually we could use metricContext.createCounter() to create the counter, capture and series in one method call,
        //but here the series is already created (may have been reloaded) so create and add the Counter and timed capture the hard way.
        Counter counter = new DefaultCounter("Source_" + getSeriesId(), "Counter for " + getSeriesId());
        DefaultTimedCapture t = new DefaultTimedCapture("Capture " + getSeriesId(), counter, timeSeries, CaptureFunctions.COUNT(countPeriod));
        metricContext.addChild(counter, t);

        AppendToSeriesMessageListener.setUpdateMessagesReceivedCounter(counter);
    }

    public void run() {
    }
}
