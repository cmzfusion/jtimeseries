package com.od.jtimeseries.ui.timeserious;

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

    private JMenuBar mainMenuBar = new JMenuBar();
    private JToolBar mainToolBar = new JToolBar();
    private TimeSeriesServerDictionary timeSeriesServerDictionary = new UdpPingHttpServerDictionary();
    private TimeSeriesDesktopPane desktopPane = new TimeSeriesDesktopPane(timeSeriesServerDictionary);

    public TimeSeriousMainFrame() {
        initializeFrame();
        createMenuBar();
        createToolBar();

        setJMenuBar(mainMenuBar);
        add(mainToolBar, BorderLayout.NORTH);
        getContentPane().add(desktopPane, BorderLayout.CENTER);
    }

    private void createToolBar() {
        NewVisualizerAction newVisualizerAction = new NewVisualizerAction(desktopPane);
        mainToolBar.add(newVisualizerAction);
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

    public List<VisualizerConfiguration> getVisualizerConfigurations() {
        return desktopPane.getVisualizerConfigurations();
    }

    public void addVisualizers(List<VisualizerConfiguration> visualizerConfigurations) {
        desktopPane.addVisualizers(visualizerConfigurations);
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
