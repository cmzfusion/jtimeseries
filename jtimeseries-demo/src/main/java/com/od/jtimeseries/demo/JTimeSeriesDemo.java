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
package com.od.jtimeseries.demo;

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.httpd.JTimeSeriesHttpd;
import com.od.jtimeseries.net.udp.*;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.timeseries.aggregation.AggregatedIdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.aggregation.DefaultAggregatedIdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.util.BenchmarkingRepaintManager;
import com.od.jtimeseries.ui.util.JideInitialization;
import com.od.jtimeseries.ui.visualizer.TimeSeriesVisualizer;
import com.od.jtimeseries.util.time.Time;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 30-Dec-2008
 * Time: 16:49:20
 */
public class JTimeSeriesDemo {
    private static final int UDP_SERVER_PORT = 1045;
    private static final int HTTPD_PORT = 1026;
    public static TimeSeriesVisualizer visualizer;

    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        try {
            SwingUtilities.invokeAndWait(
                new Runnable() {
                    public void run() {
                        try {
                            new JTimeSeriesDemo();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JTimeSeriesDemo() throws IOException {
        start();
    }

    private void start() throws IOException {

        JideInitialization.applyLicense();
        DemoMainFrame frame = new DemoMainFrame();

        TimeSeriesContext rootContext = JTimeSeries.createRootContext();

        //create some diagnostics time series for the Swing repainting which will appear at the top level/root context
        RepaintManager.setCurrentManager(new BenchmarkingRepaintManager(rootContext, Time.seconds(10)));

        //we'll create a demo sub-context, one step down from the context tree root and add a value recorder time series
        //to show the max of some random values
        TimeSeriesContext demoContext = rootContext.createContext("demo", "A context node to contain demo values");
        final ValueRecorder maxOfRandomLongValueRecorder = demoContext.createValueRecorderSeries(
                "ValueRecorder",
                "Max of the values recorded every second",
                CaptureFunctions.MAX(Time.seconds(1))
        );

        //add also an aggregated series which sums the values from the repaint metrics
        AggregatedIdentifiableTimeSeries a = new DefaultAggregatedIdentifiableTimeSeries(
                "Test Aggregated",
                "Test Aggregated Time Series",
                AggregateFunctions.SUM()
        );
        a.addTimeSeries(
            rootContext.findTimeSeries(BenchmarkingRepaintManager.REPAINT_EVENT_DURATION_METRIC_ID).getFirstMatch(),
            rootContext.findTimeSeries(BenchmarkingRepaintManager.NUMBER_OF_REPAINT_EVENTS_METRIC_ID).getFirstMatch()
        );
        rootContext.addChild(a);

        //start everything up!
        //no values will be captured until startDataCapture()
        //for metrics where we apply a function to the values recorded over a time period (e.g. max/mean)
        //nothing will capture until we start the scheduling
        rootContext.startScheduling().startDataCapture();

        //create an http server to make our time series data available over http/in a browser
        JTimeSeriesHttpd httpd = new JTimeSeriesHttpd(HTTPD_PORT, rootContext);
        httpd.start();

        //A server dictionary which stores the list of known http servers from which we can download data in the ui
        UdpPingTimeSeriesServerDictionary serverDictionary = new UdpPingTimeSeriesServerDictionary();
        UdpServer udpServer = new UdpServer(UDP_SERVER_PORT);
        udpServer.addUdpMessageListener(serverDictionary);

        //ping ourselves, to advertise our own server to appear in the local server dictionary
        UdpClient udpClient = new UdpClient(new UdpClientConfig(
                InetAddress.getLocalHost(),
                UDP_SERVER_PORT
        ));
        AnnouncementMessage h = new HttpServerAnnouncementMessage(HTTPD_PORT, "Test Server");
        udpClient.sendRepeatedMessage(h, Time.seconds(10));

        //If we wanted to send a ping to a stand alone timeseries-server, to ask the server to ping us
        //back if it is running, and thereby appear in our local server dictionary, we could do it like this
        //        UdpClient serverPingClient = new UdpClient();
        //        serverPing.addClientConfig(new UdpClient.ClientConfig(
        //                InetAddress.getByName("my.host.runningtheserver"),
        //                18081
        //        ));
        //        AnnouncementMessage m = new ClientAnnouncementMessage(UDP_SERVER_PORT, "JTimeseriesDemoClient");
        //        serverPing.sendRepeatedMessage(m, Time.seconds(5));

        createVisualizer(serverDictionary);

        frame.getContentPane().add(visualizer);
        frame.setSize(1200,900);
        frame.setVisible(true);

        new Thread(new Runnable() {
            public void run() {
                //10 times a second record a long value
                while(true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    long l = (long)(Math.random() * 1000);
                    maxOfRandomLongValueRecorder.newValue(l);
                }
            }
        }).start();
    }

    private void createVisualizer(UdpPingTimeSeriesServerDictionary serverDictionary) throws FileNotFoundException {
        visualizer = new TimeSeriesVisualizer("Test Chart", serverDictionary);
        VisualizerConfiguration config = loadVisualizerConfig();
        if ( config != null) {
            configureVisualizer(config);
        }
    }

    private VisualizerConfiguration loadVisualizerConfig() throws FileNotFoundException {
        VisualizerConfiguration v = null;
        File configFile = getTimeSeriesDemoConfigFile();
        if ( configFile.exists() ) {
            XMLDecoder d = new XMLDecoder(new FileInputStream(configFile));
            v = (VisualizerConfiguration)d.readObject();
        }
        return v;
    }


    private void saveVisualizerConfig() {
        VisualizerConfiguration v = getVisualizerConfiguration();
        File f = getTimeSeriesDemoConfigFile();
        XMLEncoder encoder = null;
        try {
            encoder = new XMLEncoder(new FileOutputStream(f));
            encoder.writeObject(v);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } finally {
            if ( encoder != null ) {
                encoder.close();
            }
        }
    }

    private File getTimeSeriesDemoConfigFile() {
        return new File(System.getProperty("user.home"), "timeSeriesDemoConfig.xml");
    }

    public VisualizerConfiguration getVisualizerConfiguration() {
        return TimeSeriesVisualizer.createVisualizerConfiguration(visualizer);
    }

    private void configureVisualizer(VisualizerConfiguration config) {
        TimeSeriesVisualizer.setVisualizerConfiguration(visualizer, config);
    }

    public class DemoMainFrame extends JFrame {

        public DemoMainFrame() {
            super("JTimeseries Demo");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    saveVisualizerConfig();
                }
            });
        }

    }
}
