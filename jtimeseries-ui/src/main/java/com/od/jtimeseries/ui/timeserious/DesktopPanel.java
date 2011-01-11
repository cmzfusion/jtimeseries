package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.net.udp.UdpPingTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.timeserious.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.timeserious.config.ConfigAware;
import com.od.jtimeseries.ui.visualizer.VisualizerConfiguration;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 24-Nov-2010
 * Time: 09:29:26
 * To change this template use File | Settings | File Templates.
 */
public class DesktopPanel extends JPanel implements TimeSeriousDesktop, ConfigAware {

    private TimeSeriesServerDictionary timeSeriesServerDictionary = new UdpPingTimeSeriesServerDictionary();
    private TimeSeriesDesktopPane desktopPane = new TimeSeriesDesktopPane(timeSeriesServerDictionary);

    public DesktopPanel() {
        super(new BorderLayout());
        add(desktopPane, BorderLayout.CENTER);
    }

    public void createAndAddVisualizer(String title) {
        desktopPane.createAndAddVisualizer(title);
    }

    public List<VisualizerConfiguration> getVisualizerConfigurations() {
        return desktopPane.getVisualizerConfigurations();
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
        config.setVisualizerConfigurations(getVisualizerConfigurations());
    }

    public void restoreConfig(TimeSeriousConfig config) {
        desktopPane.addVisualizers(config.getVisualizerConfigurations());
    }

    public List<ConfigAware> getConfigAwareChildren() {
        return Collections.emptyList();
    }
}
