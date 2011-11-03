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
        for ( Map.Entry<Identifiable,Collection<Identifiable>> e :  contextTreeEvent.getNodesWithDescendants().entrySet()) {
            //first add top level node
            if ( e.getKey() instanceof IdentifiableTimeSeries) {
                addListener((IdentifiableTimeSeries)e.getKey());
            }

            //now any children
            for ( Identifiable s : e.getValue()) {
                if ( s instanceof IdentifiableTimeSeries) {
                    addListener((IdentifiableTimeSeries)s);
                }
            }
        }
    }

    private void addListener(IdentifiableTimeSeries s) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent) {
        for ( Map.Entry<Identifiable,Collection<Identifiable>> e :  contextTreeEvent.getNodesWithDescendants().entrySet()) {
            //first add top level node
            if ( e.getKey() instanceof IdentifiableTimeSeries) {
                removeListener((IdentifiableTimeSeries) e.getKey());
            }

            //now any children
            for ( Identifiable s : e.getValue()) {
                if ( s instanceof IdentifiableTimeSeries) {
                    removeListener((IdentifiableTimeSeries) s);
                }
            }
        }
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
