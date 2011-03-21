package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.net.httpd.JTimeSeriesHttpd;
import com.od.jtimeseries.net.udp.UdpServer;
import com.od.jtimeseries.ui.net.udp.UiTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;
import com.od.jtimeseries.ui.timeserious.config.ConfigAwareTreeManager;
import com.od.jtimeseries.ui.timeserious.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.timeserious.config.TimeSeriousConfigManager;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.ui.util.JideInitialization;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import od.configutil.ConfigManagerException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 14:15:20
 *
 * Standalone UI for time series exploration
 */
public class TimeSerious {

    private static LogMethods logMethods = LogUtils.getLogMethods(TimeSerious.class);

    private TimeSeriousConfigManager configManager = new TimeSeriousConfigManager();
    private ApplicationActionModels applicationActionModels = new ApplicationActionModels();
    private UiTimeSeriesServerDictionary udpPingHttpServerDictionary = new UiTimeSeriesServerDictionary();
    private TimeSeriousMainFrame mainFrame = new TimeSeriousMainFrame(udpPingHttpServerDictionary,applicationActionModels, new ExitAction());
    private TimeSeriousConfig config;
    private ConfigAwareTreeManager configTree = new ConfigAwareTreeManager(mainFrame);

    public TimeSerious() {

        startJmxAndLocalHttpd();
        setupServerDictionary();

        try {
            config = configManager.loadConfig();
        } catch (ConfigManagerException e) {
            //todo, add handling
            e.printStackTrace();
        }

        configTree.restoreConfig(config);
        mainFrame.setVisible(true);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if ( ! confirmAndSaveConfig(e.getWindow()) ) {
                    //there's no mechanism to cancel the close which I can find, barring throwing an exception
                    //which is then handled by some dedicated logic in the Component class
                    throw new RuntimeException("User cancelled exit");
                }
            }
        });
    }

    private class ExitAction extends AbstractAction {

        private ExitAction() {
            super("Exit", ImageUtils.EXIT_16x16);
            super.putValue(SHORT_DESCRIPTION, "Exit and save config");
        }

        public void actionPerformed(ActionEvent e) {
            if ( confirmAndSaveConfig(mainFrame) ) {
                System.exit(0);
            }
        }
    }

    private boolean confirmAndSaveConfig(Window w) {
        int option = JOptionPane.showConfirmDialog(
                w,
                "Save Config?",
                "Exit TimeSerious",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if ( option == JOptionPane.YES_OPTION) {
            saveConfigOnShutdown();
        }
        return option != JOptionPane.CANCEL_OPTION;
    }

    private void saveConfigOnShutdown() {
        configTree.prepareConfigForSave(config);
        try {
            configManager.saveConfig(mainFrame, config);
        } catch (ConfigManagerException e1) {
            //todo, add handling
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                JideInitialization.applyLicense();
                JideInitialization.setupJide();
                JideInitialization.setupJideLookAndFeel();

                try {
                    UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new TimeSerious();
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
