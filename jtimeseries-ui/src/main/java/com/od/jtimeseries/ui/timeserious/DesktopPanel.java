package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.net.udp.UiTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
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

    private TimeSeriesDesktopPane desktopPane;

    public DesktopPanel(UiTimeSeriesServerDictionary dictionary, DisplayNameCalculator displayNameCalculator, SeriesSelectionPanel seriesSelectionPanel) {
        super(new BorderLayout());
        desktopPane = new TimeSeriesDesktopPane(dictionary, displayNameCalculator, seriesSelectionPanel);
        add(desktopPane, BorderLayout.CENTER);
    }

    public void createAndAddVisualizer(String title) {
        desktopPane.createAndAddVisualizer(title);
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
        config.setVisualizerConfigurations(desktopPane.getVisualizerConfigurations());
    }

    public void restoreConfig(TimeSeriousConfig config) {
        desktopPane.addVisualizers(config.getVisualizerConfigurations());
    }

    public List<ConfigAware> getConfigAwareChildren() {
        return Collections.emptyList();
    }
}
