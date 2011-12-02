package com.od.jtimeseries.net.udp;

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesEvent;
import com.od.jtimeseries.timeseries.TimeSeriesListenerAdapter;
import com.od.jtimeseries.util.NamedExecutors;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Listen to IdentifiableTimeSeries for append events and send a UDP datagram for each appended value
 * Currently works only for single item appends
 *
 * UDP publication may be revised/improved in a future release
 *
 * The maximum publish rate can be set, to limit the possible network overhead
 * Default max is 25 datagrams / second
 * = 25 * 8192 bytes (assuming max datagram size is 8192 bytes)  == 204800 bytes, so a bit less than 256KB/s worst case
 */
public class UdpPublisher extends TimeSeriesListenerAdapter {

    private static final int DEFAULT_MAX_MESSAGES = 25;
    private ScheduledExecutorService rateControllingExecutor = NamedExecutors.newSingleThreadScheduledExecutor("UdpPublisherQueue" + this);
    private UdpClient udpClient;
    private AppendPublishingListener p = new AppendPublishingListener();
    private LinkedBlockingQueue<TimeSeriesValueMessage> messageQueue = new LinkedBlockingQueue<TimeSeriesValueMessage>(10000);
    private AtomicBoolean started = new AtomicBoolean();
    private long delayTimeMicroseconds;

    public UdpPublisher(UdpClient udpClient) {
        this(udpClient, DEFAULT_MAX_MESSAGES);
    }

    public UdpPublisher(UdpClient udpClient, int maxMessagesPerSecond) {
        this.udpClient = udpClient;
        delayTimeMicroseconds = 1000000 / maxMessagesPerSecond;
    }

    public void publishAppends(IdentifiableTimeSeries s) {
        s.addTimeSeriesListener(p);
    }

    public void stopPublishing(IdentifiableTimeSeries s) {
        s.removeTimeSeriesListener(p);
    }

    private class AppendPublishingListener extends TimeSeriesListenerAdapter {
        public void itemsAddedOrInserted(TimeSeriesEvent e) {
            if ( e.getEventType() == TimeSeriesEvent.EventType.APPEND ) {
                IdentifiableTimeSeries i = (IdentifiableTimeSeries)e.getSource();
                if (e.getItems().size() == 1) {
                    TimeSeriesValueMessage m = new TimeSeriesValueMessage(
                        i.getPath(),
                        i.getDescription(),
                        e.getItems().get(0)
                    );
                    messageQueue.add(m);

                    if (! started.getAndSet(true)) {
                        startPublisherQueue();
                    }
                }
            }
        }
    }

    private void startPublisherQueue() {
        rateControllingExecutor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                try {
                    TimeSeriesValueMessage m = messageQueue.take();
                    udpClient.sendMessage(m);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, delayTimeMicroseconds, delayTimeMicroseconds, TimeUnit.MICROSECONDS);
    }
}
