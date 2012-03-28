/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
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
import com.od.jtimeseries.net.udp.message.SeriesDescriptionMessage;
import com.od.jtimeseries.net.udp.message.TimeSeriesValueMessage;
import com.od.jtimeseries.net.udp.message.UdpMessage;
import com.od.jtimeseries.net.udp.UdpServer;
import com.od.jtimeseries.component.util.path.PathMapper;
import com.od.jtimeseries.component.util.path.PathMappingResult;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.source.impl.DefaultCounter;
import com.od.jtimeseries.source.impl.DefaultValueRecorder;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;

import java.util.*;
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
public class ServerUdpMessageListener implements UdpServer.UdpMessageListener {

    private static LogMethods logMethod = LogUtils.getLogMethods(ServerUdpMessageListener.class);

    public static final TimePeriod STALE_SERIES_DELAY = Time.hours(1);

    private static volatile Counter updateMessagesReceivedCounter = DefaultCounter.NULL_COUNTER;
    private static volatile ValueRecorder liveSeriesTotalValueRecorder = DefaultValueRecorder.NULL_VALUE_RECORDER;

    private final Map<String, Long> liveSeriesLastUpdateMap = new HashMap<String, Long>();

    private TimeSeriesContext rootContext;

    private PathMapper pathMapper;
    private Set<String> loggedDeniedPaths = Collections.synchronizedSet(new HashSet<String>());
    private Set<String> loggedMigratedPaths = Collections.synchronizedSet(new HashSet<String>());


    public ServerUdpMessageListener(TimeSeriesContext rootContext, PathMapper pathMapper) {
        this.rootContext = rootContext;
        this.pathMapper = pathMapper;

        scheduleReportingAndCleanup(NamedExecutors.newSingleThreadScheduledExecutor(ServerUdpMessageListener.class.getSimpleName()));
    }

    public void udpMessageReceived(UdpMessage m) {
        updateMessagesReceivedCounter.incrementCount();
        if ( m instanceof SeriesDescriptionMessage) {
            //create series, or just update the description
            SeriesDescriptionMessage d = (SeriesDescriptionMessage)m;
            String path = getMappedPath(d.getSeriesPath());
            if ( path != null) {
                IdentifiableTimeSeries s = findOrCreateSeries(path, d.getSeriesDescription(), d.getSourceHostname(), d.getSourceInetAddress());
                s.setDescription(d.getSeriesDescription());
            }
        } else if ( m instanceof TimeSeriesValueMessage) {
            TimeSeriesValueMessage v = (TimeSeriesValueMessage)m;
            String path = getMappedPath(v.getSeriesPath());
            if (path != null) {
                addItem(v, path);
            }
        }
    }

    /**
     * @return new path, or null if the series is denied
     */
    private String getMappedPath(String path) {
        String result = null;
        PathMappingResult mappingRule = pathMapper.getPathMapping(path);
        if ( mappingRule.getType() == PathMappingResult.ResultType.DENY) {
            if ( loggedDeniedPaths.add(path)) {
                logMethod.info("Not creating a series for path " + path + ", this is an invalid series path which is denied by path mapping rules configuration");
            }
        } else if ( mappingRule.getType() == PathMappingResult.ResultType.MIGRATE) {
            if ( loggedMigratedPaths.add(path)) {
                logMethod.info("Series with path " + path + " will be migrated to path " + mappingRule.getNewPath() + " due to path mapping rules configuration");
            }
            result = mappingRule.getNewPath();
        } else if ( mappingRule.getType() == PathMappingResult.ResultType.PERMIT) {
            result = path;
        }
        return result;
    }

    private void addItem(TimeSeriesValueMessage v, String path) {
        TimeSeries s = findOrCreateSeries(path, v.getDescription(), v.getSourceHostname(), v.getSourceInetAddress());
        updateLiveSeriesMap(v, path);
        if ( v != null ) { //series created successfully
            s.addItem(v.getTimeSeriesItem());
        }
    }

    private IdentifiableTimeSeries findOrCreateSeries(String path, String description, String source, String sourceInetAddress) {
        IdentifiableTimeSeries result = null;
        try {
            result = rootContext.getOrCreateTimeSeries(path, description);
        } catch ( Throwable t) {
            logMethod.error("Error when trying to create timeseries for UDP series " + path + " from host " + source + " with address " + sourceInetAddress, t);
            logMethod.debug("Error when trying to create timeseries", t);
        }
        return result;
    }

    private void updateLiveSeriesMap(TimeSeriesValueMessage v, String path) {
        synchronized (liveSeriesLastUpdateMap) {
            Long lastTimestamp = liveSeriesLastUpdateMap.get(path);
            if ( lastTimestamp == null) {
                liveSeriesLastUpdateMap.put(path, System.currentTimeMillis());
                logMethod.info("Started to receive UDP updates for series " + path + " from host " + v.getSourceHostname() + " with address " + v.getSourceInetAddress());
            }
        }
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
                            logMethod.info("Series " + e.getKey() + " has received no updates for one hour, " +
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


    public static void setUpdateMessagesReceivedCounter(Counter updateMessagesReceivedCounter) {
        ServerUdpMessageListener.updateMessagesReceivedCounter = updateMessagesReceivedCounter;
    }

    public static void setLiveSeriesTotalValueRecorder(ValueRecorder liveSeriesTotalValueRecorder) {
        ServerUdpMessageListener.liveSeriesTotalValueRecorder = liveSeriesTotalValueRecorder;
    }
}
