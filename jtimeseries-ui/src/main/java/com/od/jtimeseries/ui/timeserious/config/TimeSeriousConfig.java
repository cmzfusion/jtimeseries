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

    private java.util.List<VisualizerConfiguration> visualizerConfigurations = new LinkedList<VisualizerConfiguration>();

    private Map<String, Rectangle> frameLocations = new HashMap<String, Rectangle>();
    private Map<String, Integer> frameExtendedStates = new HashMap<String, Integer>();

    public Rectangle getFrameLocation(String frameName) {
        return frameLocations.get(frameName);
    }

    public void setFrameLocation(String frameName, Rectangle location) {
        frameLocations.put(frameName, location);
    }

    public List<VisualizerConfiguration> getVisualizerConfigurations() {
        return visualizerConfigurations;
    }

    public void setVisualizerConfigurations(List<VisualizerConfiguration> visualizerConfigurations) {
        this.visualizerConfigurations = visualizerConfigurations;
    }

    public void setFrameExtendedState(String frameName, int extendedState) {
        frameExtendedStates.put(frameName, extendedState);
    }

    public Integer getFrameExtendedState(String frameName) {
        return frameExtendedStates.get(frameName);
    }
}
