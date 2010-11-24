package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.net.udp.UdpPingHttpServerDictionary;
import com.od.jtimeseries.ui.visualizer.VisualizerConfiguration;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 24-Nov-2010
 * Time: 09:29:26
 * To change this template use File | Settings | File Templates.
 */
public class DesktopPanel extends JPanel implements TimeSeriousDesktop {

    private TimeSeriesServerDictionary timeSeriesServerDictionary = new UdpPingHttpServerDictionary();
    private TimeSeriesDesktopPane desktopPane = new TimeSeriesDesktopPane(timeSeriesServerDictionary);
    private JToolBar mainToolBar = new JToolBar();

    public DesktopPanel() {
        super(new BorderLayout());
        createToolBar();
        add(mainToolBar, BorderLayout.NORTH);

        add(desktopPane, BorderLayout.CENTER);
    }

    private void createToolBar() {
        NewVisualizerAction newVisualizerAction = new NewVisualizerAction(this);
        mainToolBar.add(newVisualizerAction);
    }

    public void createAndAddVisualizer() {
        desktopPane.createAndAddVisualizer();
    }

    public void createAndAddVisualizer(VisualizerConfiguration c) {
        desktopPane.createAndAddVisualizer(c);
    }

    public List<VisualizerConfiguration> getVisualizerConfigurations() {
        return desktopPane.getVisualizerConfigurations();
    }

    public void addVisualizers(List<VisualizerConfiguration> visualizerConfigurations) {
        desktopPane.addVisualizers(visualizerConfigurations);
    }
}
