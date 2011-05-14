package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.ExportableConfigHolder;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.visualizer.TimeSeriesVisualizer;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 10/03/11
 * Time: 08:34
 */
public class VisualizerContext extends HidablePeerContext<VisualizerConfiguration, PeerVisualizerFrame> implements ExportableConfigHolder {

    public VisualizerContext(VisualizerConfiguration visualizerConfiguration) {
        super(visualizerConfiguration.getTitle(), visualizerConfiguration.getTitle(), visualizerConfiguration, visualizerConfiguration.isShown());
    }

    protected VisualizerConfiguration createPeerConfig(boolean isShown) {
        PeerVisualizerFrame peerFrame = getPeerResource();
        VisualizerConfiguration c = TimeSeriesVisualizer.createVisualizerConfiguration(
                peerFrame.getVisualizer()
        );
        c.setFrameLocation(peerFrame.getBounds());
        c.setZPosition(peerFrame.getZPosition());
        c.setIsIcon(peerFrame.isIcon());
        c.setShown(isShown);
        return c;
    }

    public int getZPosition() {
        return isPeerCreatedAndShown() ?
            getPeerResource().getZPosition() :
            getConfiguration().getZPosition();
    }

    public ExportableConfig getExportableConfig() {
        return getConfiguration();
    }

    public String getDefaultFileName() {
        return "timeSeriousVisualizer_" + getId();
    }

    public void addTimeSeries(List<UIPropertiesTimeSeries> selectedSeries) {
        getPeerResource().addTimeSeries(selectedSeries);
    }

    public HidablePeerContext<VisualizerConfiguration, PeerVisualizerFrame> newInstance(TimeSeriesContext parent, VisualizerConfiguration config) {
        return new VisualizerContext(config);
    }
}
