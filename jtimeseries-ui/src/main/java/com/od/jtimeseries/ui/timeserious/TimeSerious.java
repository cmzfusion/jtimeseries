package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.net.httpd.JTimeSeriesHttpd;
import com.od.jtimeseries.net.udp.UdpServer;
import com.od.jtimeseries.ui.config.ConfigAware;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.net.udp.UiTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;
import com.od.jtimeseries.ui.config.ConfigAwareTreeManager;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.config.ConfigInitializer;
import com.od.jtimeseries.ui.util.JideInitialization;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import od.configutil.ConfigManagerException;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 14:15:20
 *
 * Standalone UI for time series exploration
 */
public class TimeSerious implements ConfigAware {

    private static LogMethods logMethods = LogUtils.getLogMethods(TimeSerious.class);

    private ConfigInitializer configInitializer = new ConfigInitializer();
    private ApplicationActionModels applicationActionModels = new ApplicationActionModels();
    private UiTimeSeriesServerDictionary udpPingHttpServerDictionary = new UiTimeSeriesServerDictionary();
    private DisplayNameCalculator displayNameCalculator = new DisplayNameCalculator();
    private TimeSeriousRootContext rootContext = new TimeSeriousRootContext(udpPingHttpServerDictionary, displayNameCalculator);
    private ConfigAwareTreeManager configTree  = new ConfigAwareTreeManager(this);
    private ExitAction exitAction = new ExitAction(configTree, configInitializer);
    private FrameManager frameManager = new FrameManager(
        udpPingHttpServerDictionary,
        applicationActionModels,
        displayNameCalculator,
        rootContext,
        exitAction
    );
    private TimeSeriousConfig config;

    public void start() {
        exitAction.setMainFrame(frameManager.getMainFrame());
        startJmxAndLocalHttpd();
        setupServerDictionary();

        try {
            config = configInitializer.loadConfig();
        } catch (ConfigManagerException e) {
            //todo, add handling
            e.printStackTrace();
        }

        configTree.restoreConfig(config);
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
        config.setDisplayNamePatterns(displayNameCalculator.getDisplayNamePatterns());
    }

    public void restoreConfig(TimeSeriousConfig config) {
        displayNameCalculator.setDisplayNamePatterns(config.getDisplayNamePatterns());
    }

    public java.util.List<ConfigAware> getConfigAwareChildren() {
        return Arrays.asList(rootContext, frameManager);
    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                JideInitialization.applyLicense();
                JideInitialization.setupJide();

                try {
                    UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new TimeSerious().start();
            }
        });

    }

    private void setupServerDictionary() {
        UdpServer server = new UdpServer(17000);
        server.addUdpMessageListener(udpPingHttpServerDictionary);
        server.startReceive();
    }

    private void startJmxAndLocalHttpd() {
        LocalJmxMetrics localJmxMetrics = new LocalJmxMetrics();
        localJmxMetrics.startJmxManagementService(17001);
        localJmxMetrics.startLocalMetricCollection();

        int httpdPort = 17002;
        try {
            JTimeSeriesHttpd httpd = new JTimeSeriesHttpd(httpdPort, localJmxMetrics.getRootContext());
            httpd.start();
        } catch (IOException e) {
            logMethods.logError("Could not start timeserious httpd for local metrics", e);
        }

//        try {
//            udpPingHttpServerDictionary.addServer(
//                new TimeSeriesServer(
//                    InetAddress.getLocalHost(),
//                    httpdPort,
//                    "Timeserious",
//                    System.currentTimeMillis()
//                )
//            );
//        } catch (UnknownHostException e) {
//            logMethods.logError("Could not add local timeseries server", e);
//        }


    }


}
