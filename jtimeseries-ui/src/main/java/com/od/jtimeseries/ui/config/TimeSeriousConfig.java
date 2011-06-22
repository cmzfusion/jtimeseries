/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    private String title = "TimeSeriousConfig";
    private List<ColumnSettings> mainSelectorColumnSettings;
    private DisplayNamePatternConfig displayNamePatterns = new DisplayNamePatternConfig();

    public TimeSeriousConfig() {
        //main desktop config should exist when we create an initial config
        desktopConfigs.put(DesktopConfiguration.MAIN_DESKTOP_NAME, new DesktopConfiguration(DesktopConfiguration.MAIN_DESKTOP_NAME));
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

    public void setDisplayNamePatterns(DisplayNamePatternConfig displayNamePatternConfig) {
        this.displayNamePatterns = displayNamePatternConfig;
    }

    public DisplayNamePatternConfig getDisplayNamePatterns() {
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

    public List<ColumnSettings> getMainSelectorColumnSettings() {
        return mainSelectorColumnSettings;
    }

    public void setMainSelectorColumnSettings(List<ColumnSettings> mainSelectorColumnSettings) {
        this.mainSelectorColumnSettings = mainSelectorColumnSettings;
    }

    //the readResolve method allows us to handle migrations where we add fields which need to
    //be initialised - xstream sets the fields null even if a default is
    //assigned when the field is defined
    private Object readResolve() {
        if (serverConfigs == null) {
            serverConfigs = new LinkedList<TimeSeriesServerConfig>();
        }
        if (displayNamePatterns == null) {
            displayNamePatterns = new DisplayNamePatternConfig();
        }
        if ( splitPaneLocationWhenTableSelected == null ) {
            splitPaneLocationWhenTableSelected = 400;
        }
        if ( splitPaneLocationWhenTreeSelected == null ) {
            splitPaneLocationWhenTreeSelected = 300;
        }
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
