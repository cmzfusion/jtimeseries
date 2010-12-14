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
package com.od.jtimeseries.server.message;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesValueMessage;
import com.od.jtimeseries.net.udp.UdpMessage;
import com.od.jtimeseries.net.udp.UdpServer;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 20-May-2009
* Time: 22:11:48
* To change this template use File | Settings | File Templates.
*
* Appends a value to a series when a message is received, if necessary creating the series
* according to the context path and id received in the message.
*
* Keeps a running tally of the number of series for which data is being received, and the overall update count.
*/
public class AppendToSeriesMessageListener implements UdpServer.UdpMessageListener {

    private static LogMethods logMethod = LogUtils.getLogMethods(AppendToSeriesMessageListener.class);

    public static final TimePeriod STALE_SERIES_DELAY = Time.hours(1);

    private static volatile Counter updateMessagesReceivedCounter;
    private static volatile ValueRecorder liveSeriesTotalValueRecorder;

    private final Map<String, Long> liveSeriesLastUpdateMap = new HashMap<String, Long>();

    private TimeSeriesContext rootContext;

    public AppendToSeriesMessageListener(TimeSeriesContext rootContext) {
        this.rootContext = rootContext;

        scheduleReportingAndCleanup(NamedExecutors.newSingleThreadScheduledExecutor("AppendToSeriesMessageListener"));
    }

    private void scheduleReportingAndCleanup(ScheduledExecutorService staleSeriesExecutor) {
        staleSeriesExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                long currentTime = System.currentTimeMillis();
                synchronized (liveSeriesLastUpdateMap) {
                    Iterator<Map.Entry<String, Long>> i = liveSeriesLastUpdateMap.entrySet().iterator();
                    while(i.hasNext()) {
                        Map.Entry<String, Long> e = i.next();
                        if ( currentTime - e.getValue() > STALE_SERIES_DELAY.getLengthInMillis() ) {
                            logMethod.logInfo("Series " + e.getKey() + " has received no updates for one hour, " +
                                    "it is likely this series is no longer being published");
                            i.remove();
                        }
                    }
                    if ( liveSeriesTotalValueRecorder != null) {
                        liveSeriesTotalValueRecorder.newValue(liveSeriesLastUpdateMap.size());
                    }
                }
            }
        }, 300, 300, TimeUnit.SECONDS);
    }

    public void udpMessageReceived(UdpMessage m) {
        if ( m instanceof TimeSeriesValueMessage) {
            TimeSeriesValueMessage v = (TimeSeriesValueMessage)m;
            processNewTimeSeriesValue(v);
        }
    }

    private void processNewTimeSeriesValue(TimeSeriesValueMessage v) {
        try {
            TimeSeries s = rootContext.createTimeSeries(v.getContextPath(), v.getDescription());
            s.append(v.getTimeSeriesItem());

            if ( updateMessagesReceivedCounter != null) {
                updateMessagesReceivedCounter.incrementCount();
            }

            synchronized (liveSeriesLastUpdateMap) {
                Long lastTimestamp = liveSeriesLastUpdateMap.get(v.getContextPath());
                if ( lastTimestamp == null) {
                    liveSeriesLastUpdateMap.put(v.getContextPath(), System.currentTimeMillis());
                    logMethod.logInfo("Started to receive UDP updates for series " + v.getContextPath() + " from host " + v.getHostname() + " with address " + v.getInetAddress());
                }
            }
        } catch ( Exception e) {
            logMethod.logError("Error when trying to create timeseries for UDP series "  + v.getContextPath() + " from host " + v.getInetAddress() + " with address " + v.getInetAddress());
            logMethod.logDebug("Error when trying to create timeseries", e);
        }
    }

    public static void setUpdateMessagesReceivedCounter(Counter updateMessagesReceivedCounter) {
        AppendToSeriesMessageListener.updateMessagesReceivedCounter = updateMessagesReceivedCounter;
    }

    public static void setLiveSeriesTotalValueRecorder(ValueRecorder liveSeriesTotalValueRecorder) {
        AppendToSeriesMessageListener.liveSeriesTotalValueRecorder = liveSeriesTotalValueRecorder;
    }
}
