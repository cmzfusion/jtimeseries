package com.od.jtimeseries.net.udp;

import com.od.jtimeseries.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.identifiable.IdentifiableTreeListenerAdapter;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02/11/11
 * Time: 09:04
 *
 * Listen to a TimeSeriesContext or Identifiable node for IdentifiableTimeSeries added as descendants
 * Publish UDP series append messages via a UdpClient for all the series added
 *
 * When descendant IdentifiableTimeSeries are removed from their parent context/identifiable, then
 * publication will stop
 */
public class UdpPublishingTreeListener extends IdentifiableTreeListenerAdapter {

    private UdpPublisher udpPublisher;

    public UdpPublishingTreeListener(UdpClient udpClient) {
        this.udpPublisher = new UdpPublisher(udpClient);
    }

    public UdpPublishingTreeListener(UdpClient udpClient, int maxDatagramsPerSecond) {
        this.udpPublisher = new UdpPublisher(udpClient, maxDatagramsPerSecond);
    }

    public void descendantAdded(IdentifiableTreeEvent contextTreeEvent) {
        contextTreeEvent.processNodesAndDescendants(new IdentifiableTreeEvent.IdentifiableProcessor<IdentifiableTimeSeries>() {
            public void process(IdentifiableTimeSeries identifiable) {
                addListener(identifiable);
            }
        }, IdentifiableTimeSeries.class);
    }

    protected void addListener(IdentifiableTimeSeries s) {
        udpPublisher.publishAppends(s);
    }

    public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent) {
        contextTreeEvent.processNodesAndDescendants(new IdentifiableTreeEvent.IdentifiableProcessor<IdentifiableTimeSeries>() {
            public void process(IdentifiableTimeSeries identifiable) {
                removeListener(identifiable);
            }
        }, IdentifiableTimeSeries.class);
    }

    protected void removeListener(IdentifiableTimeSeries s) {
        udpPublisher.stopPublishing(s);
    }

}
