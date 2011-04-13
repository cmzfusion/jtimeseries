package com.od.jtimeseries.ui.config;

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
public class TimeSeriousConfig implements ExportableConfig {

    private Map<String, DesktopConfiguration> desktopConfigs = new HashMap<String, DesktopConfiguration>();
    private List<TimeSeriesServerConfig> serverConfigs = new LinkedList<TimeSeriesServerConfig>();
    private Integer splitPaneLocationWhenTableSelected = 400;
    private Integer splitPaneLocationWhenTreeSelected = 300;
    private boolean mainSeriesSelectorTableVisible;

    private List<DisplayNamePattern> displayNamePatterns = new LinkedList<DisplayNamePattern>();

    public DesktopConfiguration getOrCreateDesktopConfiguration(String desktopName) {
        DesktopConfiguration c = this.desktopConfigs.get(desktopName);
        if ( c == null) {
            c = new DesktopConfiguration(desktopName);
            this.desktopConfigs.put(desktopName, c);
        }
        return c;
    }

    public Map<String, DesktopConfiguration> getDesktopConfigs() {
        return desktopConfigs;
    }

    public void setDesktopConfigration(String desktopName, DesktopConfiguration c) {
        this.desktopConfigs.put(desktopName, c);
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
        return this;
    }

}
