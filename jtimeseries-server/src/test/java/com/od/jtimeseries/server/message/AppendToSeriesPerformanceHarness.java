package com.od.jtimeseries.server.message;

import com.od.jtimeseries.net.udp.message.TimeSeriesValueMessage;
import com.od.jtimeseries.net.udp.UdpClient;
import com.od.jtimeseries.net.udp.UdpClientConfig;
import com.od.jtimeseries.net.udp.message.properties.PropertiesMessageFactory;
import com.od.jtimeseries.timeseries.Item;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.numeric.DoubleNumeric;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 02-Mar-2010
 * Time: 19:52:23
 * To change this template use File | Settings | File Templates.
 */
public class AppendToSeriesPerformanceHarness {

    private static long messagesSent = 0;
    private static final int NUMBER_OF_SERIES = 10000;
    private static final int MESSAGES_PER_SECOND = 1000;

    private List<String> seriesPaths = new ArrayList<String>();
    public UdpClient udpClient = new UdpClient(new UdpClientConfig(InetAddress.getByName("localhost"), 18081));

    public AppendToSeriesPerformanceHarness() throws UnknownHostException {
        generateSeriesPaths();
        ScheduledExecutorService s = NamedExecutors.newSingleThreadScheduledExecutor("AppendToSeriesPerformanceHarness");
        s.scheduleAtFixedRate(new Runnable() {
            public void run() {
                fireMessage();
            }
        }, 0, (int)Math.ceil((float)1000/MESSAGES_PER_SECOND), TimeUnit.MILLISECONDS);
    }

    private void fireMessage() {
        int series = (int)(Math.random() * seriesPaths.size());
        String path = seriesPaths.get(series);

        TimeSeriesValueMessage timeSeriesValueMessage = new PropertiesMessageFactory().createTimeSeriesValueMessage(
                path,
                new Item(System.currentTimeMillis(), Math.random())
        );

        udpClient.sendMessage(timeSeriesValueMessage);

        messagesSent++;
        if ( messagesSent % 10000 == 0) {
            System.out.println("Sent " + messagesSent / 1000 + " thousand messages");
        }
    }

    private void generateSeriesPaths() {
        for (int loop=0; loop < NUMBER_OF_SERIES; loop++) {
            String newPath = "test.series.path.with.several.nodes." + loop;
            seriesPaths.add(newPath);
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        new AppendToSeriesPerformanceHarness();
    }
}
