package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.ExportableConfigHolder;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.visualizer.TimeSeriesVisualizer;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 10/03/11
 * Time: 08:34
 */
public class VisualizerContext extends HideablePeerContext<VisualizerConfiguration> implements ExportableConfigHolder {

    private VInternalFrame peerFrame;

    public VisualizerContext(VisualizerConfiguration visualizerConfiguration) {
        super(visualizerConfiguration.getChartsTitle(), visualizerConfiguration.getChartsTitle(), visualizerConfiguration, visualizerConfiguration.isShown());
    }

    protected VisualizerConfiguration createVisualizerConfig(boolean isShown) {
        VisualizerConfiguration c = TimeSeriesVisualizer.createVisualizerConfiguration(
                peerFrame.getVisualizer()
        );
        c.setFrameBounds(peerFrame.getBounds());
        c.setZPosition(peerFrame.getZPosition());
        c.setIsIcon(peerFrame.isIcon());
        c.setShown(isShown);
        return c;
    }

    protected boolean isPeerCreated() {
        return peerFrame != null;
    }

    protected void disposePeerResource() {
        peerFrame = null;
    }

    public void setPeerResource(VInternalFrame frame) {
        this.peerFrame = frame;
    }

    public int getZPosition() {
        return isPeerCreatedAndShown() ?
            peerFrame.getZPosition() :
            getConfiguration().getZPosition();
    }

    public ExportableConfig getExportableConfig() {
        return getConfiguration();
    }

    public String getDefaultFileName() {
        return "timeSeriousVisualizer_" + getId();
    }
}
