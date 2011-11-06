package com.od.jtimeseries.net.udp;

import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.identifiable.IdentifiableTreeListenerAdapter;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesEvent;
import com.od.jtimeseries.timeseries.TimeSeriesListenerAdapter;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02/11/11
 * Time: 09:04
 */
public class UdpPublishingListener extends IdentifiableTreeListenerAdapter {

    public static final int DEFAULT_MIN_SEND_INTERVAL_MILLIS = 500;

    private Map<Identifiable, UdpPublishingTimeSeriesListener> listenersByIdentifiable = new IdentityHashMap<Identifiable, UdpPublishingTimeSeriesListener>();

    public void descendantAdded(IdentifiableTreeEvent contextTreeEvent) {
        contextTreeEvent.processNodesAndDescendants(new IdentifiableTreeEvent.IdentifiableProcessor<IdentifiableTimeSeries>() {
            public void process(IdentifiableTimeSeries identifiable) {
                addListener(identifiable);
            }
        }, IdentifiableTimeSeries.class);
    }

    private void addListener(IdentifiableTimeSeries s) {

    }

    public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent) {
        contextTreeEvent.processNodesAndDescendants(new IdentifiableTreeEvent.IdentifiableProcessor<IdentifiableTimeSeries>() {
            public void process(IdentifiableTimeSeries identifiable) {
                removeListener(identifiable);
            }
        }, IdentifiableTimeSeries.class);
    }

    private void removeListener(IdentifiableTimeSeries key) {

    }

    /**
     * Listen for insert events which insert one item and send these as a datagram
     * Only send a datagram if it is at least MIN_DATAGRAM_SEND_INTERVAL millis since the last one
     */
    static class UdpPublishingTimeSeriesListener extends TimeSeriesListenerAdapter {

        private UdpClient udpClient;
        private IdentifiableTimeSeries i;
        private volatile long lastSentTime;
        private final int minSendIntervalMillis;

        public UdpPublishingTimeSeriesListener(UdpClient udpClient, IdentifiableTimeSeries i, int minSendIntervalMillis) {
            this.udpClient = udpClient;
            this.i = i;
            this.minSendIntervalMillis = minSendIntervalMillis;
        }

        public void itemsAddedOrInserted(TimeSeriesEvent e) {
            long time = System.currentTimeMillis();
            if (e.getItems().size() == 1 && (time - lastSentTime) > minSendIntervalMillis) {
                udpClient.sendMessage(new TimeSeriesValueMessage(
                        i.getPath(),
                        i.getDescription(),
                        e.getItems().get(0)
                ));
                lastSentTime = time;
            }
        }
    }
}
