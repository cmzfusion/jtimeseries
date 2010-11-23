package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.timeserious.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.timeserious.config.TimeSeriousConfigManager;
import com.od.jtimeseries.ui.util.JideInitialization;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import od.configutil.ConfigManagerException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 14:15:20
 *
 * Standalone UI for time series exploration
 */
public class TimeSerious {

    private TimeSeriousConfigManager configManager = new TimeSeriousConfigManager();
    private TimeSeriousMainFrame mainFrame = new TimeSeriousMainFrame();
    private TimeSeriousConfig config;

    public TimeSerious() {
        try {
            config = configManager.loadConfig();
        } catch (ConfigManagerException e) {
            //todo, add handling
            e.printStackTrace();
        }

        Rectangle frameLocation = config.getMainFrameLocation();
        if ( frameLocation != null) {
            mainFrame.setBounds(frameLocation);
        } else {
            mainFrame.setSize(1024, 768);
            mainFrame.setLocationRelativeTo(null);
        }
        mainFrame.addVisualizers(config.getVisualizerConfigurations());
        mainFrame.setVisible(true);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                prepareConfigForSave();
                try {
                    configManager.saveConfig(config);
                } catch (ConfigManagerException e1) {
                    //todo, add handling
                    e1.printStackTrace();
                }
            }
        });

        configManager.checkInitialized(mainFrame);
    }

    private void prepareConfigForSave() {
        config.setMainFrameLocation(mainFrame.getBounds());
        config.setVisualizerConfigurations(mainFrame.getVisualizerConfigurations());
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                try {
                    UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JideInitialization.applyLicense();                
                JideInitialization.setupJide();
                JideInitialization.setupJideLookAndFeel();

                new TimeSerious();
            }
        });
    }



}
