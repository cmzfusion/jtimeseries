package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.timeserious.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.net.udp.UdpPingHttpServerDictionary;
import com.od.jtimeseries.ui.visualizer.VisualizerConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 15:14:34
 */
public class TimeSeriousMainFrame extends JFrame {

    public static final String MAIN_FRAME_NAME = "mainFrame";

    private JMenuBar mainMenuBar = new JMenuBar();
    private DesktopPanel desktopPanel = new DesktopPanel();
    private MainSeriesTreePanel seriesTreePanel = new MainSeriesTreePanel();

    public TimeSeriousMainFrame() {
        initializeFrame();
        createMenuBar();

        setJMenuBar(mainMenuBar);
        JSplitPane splitPane = new JSplitPane();
        splitPane.setLeftComponent(seriesTreePanel);
        splitPane.setRightComponent(desktopPanel);
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    private void initializeFrame() {
        setTitle("TimeSerious");
        setIconImage(ImageUtils.SERIES_ICON_16x16.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void createMenuBar() {
        JMenu fileMenu = new JMenu("File");
        mainMenuBar.add(fileMenu);

        JMenuItem exitItem = new JMenuItem(new ExitAction());
        fileMenu.add(exitItem);

        JMenu windowMenu = new JMenu("Window");
        mainMenuBar.add(windowMenu);
    }

    public void restoreConfig(TimeSeriousConfig config) {
        Rectangle frameLocation = config.getFrameLocation(MAIN_FRAME_NAME);
        if ( frameLocation != null) {
            setBounds(frameLocation);
            setExtendedState(config.getFrameExtendedState(MAIN_FRAME_NAME));
        } else {
            setSize(1024, 768);
            setLocationRelativeTo(null);
        }
        desktopPanel.restoreConfig(config);
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
        config.setFrameLocation(MAIN_FRAME_NAME, getBounds());
        config.setFrameExtendedState(MAIN_FRAME_NAME, getExtendedState());
        desktopPanel.prepareConfigForSave(config);
    }

    private class ExitAction extends AbstractAction {

        private ExitAction() {
            super("Exit");
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}
