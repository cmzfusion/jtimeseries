package com.od.jtimeseries.net.udp;

import com.od.jtimeseries.net.udp.message.SeriesDescriptionMessage;
import com.od.jtimeseries.net.udp.message.TimeSeriesValueMessage;
import com.od.jtimeseries.net.udp.message.UdpMessage;
import com.od.jtimeseries.net.udp.message.UdpMessageFactory;
import com.od.jtimeseries.net.udp.message.javaio.JavaIOMessageFactory;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesEvent;
import com.od.jtimeseries.timeseries.TimeSeriesListenerAdapter;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Listen to IdentifiableTimeSeries for append events and send a UDP datagram for each appended value
 * Currently works only for single item appends
 *
 * The maximum publish rate can be set, to limit the possible network overhead
 * Default max is 25 datagrams / second
 * = 25 * 8192 bytes (assuming max datagram size is 8192 bytes)  == 204800 bytes, so a bit less than 256KB/s worst case
 */
public class UdpPublisher extends TimeSeriesListenerAdapter {

    private static final LogMethods logMethods = LogUtils.getLogMethods(UdpPublisher.class);
    private static final int DEFAULT_MAX_MESSAGES = 25;

    private ScheduledExecutorService rateControllingExecutor = NamedExecutors.newSingleThreadScheduledExecutor("UdpPublisherQueue" + this);
    private UdpClient udpClient;
    private AppendPublishingListener appendPublishingListener = new AppendPublishingListener();
    private LinkedBlockingQueue<UdpMessage> messageQueue = new LinkedBlockingQueue<UdpMessage>(10000);
    private AtomicBoolean started = new AtomicBoolean();
    private long delayTimeMicroseconds;
    private UdpMessageFactory udpMessageFactory = new JavaIOMessageFactory();

    public UdpPublisher(UdpClient udpClient) {
        this(udpClient, DEFAULT_MAX_MESSAGES);
    }

    public UdpPublisher(UdpClient udpClient, int maxDatagramsPerSecond) {
        this.udpClient = udpClient;
        delayTimeMicroseconds = 1000000 / maxDatagramsPerSecond;
    }

    public void setUdpMessageFactory(UdpMessageFactory udpMessageFactory) {
        this.udpMessageFactory = udpMessageFactory;
    }

    public void publishAppends(IdentifiableTimeSeries s) {
        s.addTimeSeriesListener(appendPublishingListener);
    }

    public void stopPublishing(IdentifiableTimeSeries s) {
        s.removeTimeSeriesListener(appendPublishingListener);
    }

    public void publishDescription(IdentifiableTimeSeries identifiable) {
        SeriesDescriptionMessage d = udpMessageFactory.createTimeSeriesDescriptionMessage(
            identifiable.getPath(), identifiable.getDescription()
        );
        messageQueue.add(d);
    }

    private class AppendPublishingListener extends TimeSeriesListenerAdapter {
        public void itemsAddedOrInserted(TimeSeriesEvent e) {
            if ( e.isAppend() ) {
                IdentifiableTimeSeries i = (IdentifiableTimeSeries)e.getSource();
                if (e.getItems().size() == 1) {
                    TimeSeriesValueMessage m = udpMessageFactory.createTimeSeriesValueMessage(
                        i.getPath(),
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
                    udpClient.sendMessages(messageQueue);
                } catch (Throwable t) {
                    logMethods.error("Failed to send UDP message(s)", t);
                }
            }
        }, delayTimeMicroseconds, delayTimeMicroseconds, TimeUnit.MICROSECONDS);
    }
}
