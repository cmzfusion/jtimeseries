package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.ExportableConfigHolder;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.visualizer.TimeSeriesVisualizer;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 10/03/11
 * Time: 08:34
 */
public class VisualizerContext extends DefaultTimeSeriesContext implements ExportableConfigHolder {

    private VisualizerConfiguration visualizerConfiguration;
    private VInternalFrame visualizerInternalFrame;
    private boolean shown;

    public VisualizerContext(VisualizerConfiguration visualizerConfiguration) {
        super(visualizerConfiguration.getChartsTitle(), visualizerConfiguration.getChartsTitle());
        this.visualizerConfiguration = visualizerConfiguration;
        shown = visualizerConfiguration.isShown();
    }

    public VisualizerConfiguration getVisualizerConfiguration() {
        return isShown() && visualizerInternalFrame != null ?
            createVisualizerConfig() :
            visualizerConfiguration;
    }

    private VisualizerConfiguration createVisualizerConfig() {
        VisualizerConfiguration c =  TimeSeriesVisualizer.createVisualizerConfiguration(
            visualizerInternalFrame.getVisualizer()
        );
        c.setFrameBounds(visualizerInternalFrame.getBounds());
        c.setZPosition(visualizerInternalFrame.getZPosition());
        c.setIsIcon(visualizerInternalFrame.isIcon());
        c.setShown(true);
        return c;
    }

    public boolean isShown() {
        return shown;
    }

    public boolean isHidden() {
        return ! isShown();
    }

    public void setShown(boolean shown) {
        if ( this.shown != shown) {
            this.shown = shown;
            if ( ! shown) {
                visualizerConfiguration = createVisualizerConfig();
                visualizerConfiguration.setShown(false);
                visualizerInternalFrame = null;
            }
            fireNodeChanged("shown");
        }
    }

    public void setVisualizerFrame(VInternalFrame frame) {
        this.visualizerInternalFrame = frame;
    }

    public int getZPosition() {
        return visualizerInternalFrame != null ?
            visualizerInternalFrame.getZPosition() :
            visualizerConfiguration.getZPosition();
    }

    public ExportableConfig getExportableConfig() {
        return getVisualizerConfiguration();
    }

    public String getDefaultFileName() {
        return "timeSeriousVisualizer_" + getId();
    }
}
