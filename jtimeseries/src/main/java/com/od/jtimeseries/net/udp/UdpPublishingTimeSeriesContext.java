package com.od.jtimeseries.net.udp;

import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultContextFactory;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesListenerAdapter;
import com.od.jtimeseries.timeseries.TimeSeriesEvent;

import java.util.Map;
import java.util.IdentityHashMap;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 19-Feb-2010
* Time: 23:14:35
* To change this template use File | Settings | File Templates.
*
* A context which adds listener to child timeseries which sends a UDP datagram when values are added to the series
* By default, child contexts created by this context will also be UdpPublishingTimeSeriesContext
*/
public class UdpPublishingTimeSeriesContext extends DefaultTimeSeriesContext {

    private static int DEFAULT_MIN_SEND_INTERVAL_MILLIS = 5000;
    private Map<Identifiable, PublishingTimeSeriesListener> listenersByIdentifiable = new IdentityHashMap<Identifiable, PublishingTimeSeriesListener>();
    private UdpClient udpClient;
    private int minSendIntervalMillis;

    public UdpPublishingTimeSeriesContext(UdpClient udpClient, TimeSeriesContext parent, String id, String description) {
        this(udpClient, DEFAULT_MIN_SEND_INTERVAL_MILLIS, parent, id, description);
    }

    public UdpPublishingTimeSeriesContext(UdpClient udpClient, int minSendIntervalMillis, TimeSeriesContext parent, String id, String description) {
        super(parent, id, description);
        this.udpClient = udpClient;
        this.minSendIntervalMillis = minSendIntervalMillis;
        setContextFactory(new UdpPublishingContextFactory());
    }

    public TimeSeriesContext addChild(Identifiable... identifiables) {
        synchronized (getTreeLock()) {
            TimeSeriesContext t = super.addChild(identifiables);
            for ( Identifiable i : identifiables) {
                if ( i instanceof IdentifiableTimeSeries) {
                    PublishingTimeSeriesListener listener = new PublishingTimeSeriesListener(udpClient, (IdentifiableTimeSeries) i, minSendIntervalMillis);
                    listenersByIdentifiable.put(i, listener);
                    ((IdentifiableTimeSeries)i).addTimeSeriesListener(listener);
                }
            }
            return t;
        }
    }

    public boolean removeChild(Identifiable identifiable) {
        synchronized (getTreeLock()) {
            boolean result = super.removeChild(identifiable);
            if (result) {
                PublishingTimeSeriesListener l = listenersByIdentifiable.get(identifiable);
                if ( l != null) {
                    ((IdentifiableTimeSeries)identifiable).removeTimeSeriesListener(l);
                }
            }
            return result;
        }
    }

    /**
     * Listen for insert events which insert one item and send these as a datagram
     * Only send a datagram if it is at least MIN_DATAGRAM_SEND_INTERVAL millis since the last one
     */
    private static class PublishingTimeSeriesListener extends TimeSeriesListenerAdapter {

        private UdpClient udpClient;
        private IdentifiableTimeSeries i;
        private volatile long lastSentTime;
        private final int minSendIntervalMillis;

        public PublishingTimeSeriesListener(UdpClient udpClient, IdentifiableTimeSeries i, int minSendIntervalMillis) {
            this.udpClient = udpClient;
            this.i = i;
            this.minSendIntervalMillis = minSendIntervalMillis;
        }

        public void itemsAdded(TimeSeriesEvent e) {
            long time = System.currentTimeMillis();
            if ( e.getItems().size() == 1 && (time - lastSentTime) > minSendIntervalMillis) {
                udpClient.sendMessage(new TimeSeriesValueMessage(
                        i.getPath(),
                        i.getDescription(),
                        e.getItems().get(0)
                ));
                lastSentTime = time;
            }
        }
    }

    public class UdpPublishingContextFactory extends DefaultContextFactory {

        public TimeSeriesContext createContext(TimeSeriesContext parent, String id, String description) {
            return new UdpPublishingTimeSeriesContext(udpClient, minSendIntervalMillis, parent, id, description);
        }

    }
}
