package com.od.jtimeseries.ui.config;

import com.od.jtimeseries.ui.displaypattern.DisplayNamePattern;
import com.od.jtimeseries.ui.util.JTimeSeriesUIConfig;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 29-Apr-2010
 * Time: 08:13:11
 */
public class TimeSeriousConfig implements JTimeSeriesUIConfig {

    private java.util.List<VisualizerConfiguration> visualizerConfigurations = new LinkedList<VisualizerConfiguration>();
    private java.util.List<VisualizerConfiguration> hiddenVisualizerConfigurations = new LinkedList<VisualizerConfiguration>();

    private Map<String, Rectangle> frameLocations = new HashMap<String, Rectangle>();
    private Map<String, Integer> frameExtendedStates = new HashMap<String, Integer>();
    private List<TimeSeriesServerConfig> serverConfigs = new LinkedList<TimeSeriesServerConfig>();
    private Integer splitPaneLocationWhenTableSelected = 400;
    private Integer splitPaneLocationWhenTreeSelected = 300;
    private boolean mainSeriesSelectorTableVisible;

    private List<DisplayNamePattern> displayNamePatterns = new LinkedList<DisplayNamePattern>();

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

    public List<VisualizerConfiguration> getHiddenVisualizerConfigurations() {
        return hiddenVisualizerConfigurations;
    }

    public void setHiddenVisualizerConfigurations(List<VisualizerConfiguration> hiddenVisualizerConfigurations) {
        this.hiddenVisualizerConfigurations = hiddenVisualizerConfigurations;
    }

    public void setFrameExtendedState(String frameName, int extendedState) {
        frameExtendedStates.put(frameName, extendedState);
    }

    public Integer getFrameExtendedState(String frameName) {
        return frameExtendedStates.get(frameName);
    }

    public List<TimeSeriesServerConfig> getServerConfigs() {
        return serverConfigs;
    }

    public void setTimeSeriesServerConfigs(List<TimeSeriesServerConfig> serverConfigs) {
        this.serverConfigs = serverConfigs;
    }

    public void setDisplayNamePatterns(List<DisplayNamePattern> displayNamePatterns) {
        this.displayNamePatterns = displayNamePatterns;
    }

    public List<DisplayNamePattern> getDisplayNamePatterns() {
        return displayNamePatterns;
    }

    public int getSplitPaneLocationWhenTableSelected() {
        return splitPaneLocationWhenTableSelected;
    }

    public void setSplitPaneLocationWhenTableSelected(int splitPaneLocationWhenTableSelected) {
        this.splitPaneLocationWhenTableSelected = splitPaneLocationWhenTableSelected;
    }

    public int getSplitPaneLocationWhenTreeSelected() {
        return splitPaneLocationWhenTreeSelected;
    }

    public void setSplitPaneLocationWhenTreeSelected(int splitPaneLocationWhenTreeSelected) {
        this.splitPaneLocationWhenTreeSelected = splitPaneLocationWhenTreeSelected;
    }

    public boolean isMainSeriesSelectorTableVisible() {
        return mainSeriesSelectorTableVisible;
    }

    public void setMainSeriesSelectorTableVisible(boolean mainSeriesSelectorTableVisible) {
        this.mainSeriesSelectorTableVisible = mainSeriesSelectorTableVisible;
    }

    //the readResolve method allows us to handle migrations where we add fields which need to
    //be initialised - xstream sets the fields null even if a default is
    //assigned when the field is defined
    private Object readResolve() {
        if (serverConfigs == null) {
            serverConfigs = new LinkedList<TimeSeriesServerConfig>();
        }
        if (displayNamePatterns == null) {
            displayNamePatterns = new LinkedList<DisplayNamePattern>();
        }
        if ( splitPaneLocationWhenTableSelected == null ) {
            splitPaneLocationWhenTableSelected = 400;
        }
        if ( splitPaneLocationWhenTreeSelected == null ) {
            splitPaneLocationWhenTreeSelected = 300;
        }
        if ( hiddenVisualizerConfigurations == null ) {
            hiddenVisualizerConfigurations = new LinkedList<VisualizerConfiguration>();
        }
        return this;
    }
}
