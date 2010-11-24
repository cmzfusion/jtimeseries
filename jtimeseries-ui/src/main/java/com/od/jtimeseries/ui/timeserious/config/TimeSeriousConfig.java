package com.od.jtimeseries.ui.timeserious.config;

import com.od.jtimeseries.ui.visualizer.VisualizerConfiguration;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 29-Apr-2010
 * Time: 08:13:11
 */
public class TimeSeriousConfig {

    private Rectangle mainFrameLocation;
    private java.util.List<VisualizerConfiguration> visualizerConfigurations = new LinkedList<VisualizerConfiguration>();

    public Rectangle getMainFrameLocation() {
        return mainFrameLocation;
    }

    public void setMainFrameLocation(Rectangle mainFrameLocation) {
        this.mainFrameLocation = mainFrameLocation;
    }

    public List<VisualizerConfiguration> getVisualizerConfigurations() {
        return visualizerConfigurations;
    }

    public void setVisualizerConfigurations(List<VisualizerConfiguration> visualizerConfigurations) {
        this.visualizerConfigurations = visualizerConfigurations;
    }

}
