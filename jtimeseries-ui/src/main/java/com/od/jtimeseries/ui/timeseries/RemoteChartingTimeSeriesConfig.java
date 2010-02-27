/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
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
package com.od.jtimeseries.ui.timeseries;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 28-May-2009
* Time: 17:20:00
*/
public class RemoteChartingTimeSeriesConfig {
    private String parentPath;
    private String id;
    private String description;
    private String timeSeriesUrl;
    private int refreshTimeSeconds;
    private int maxDaysHistory;
    private boolean selected;
    private String displayName;
    private Color color;

    //no params constructor required for bean xml persistence
    public RemoteChartingTimeSeriesConfig() {}

    public RemoteChartingTimeSeriesConfig(String parentPath, String id, String description, String timeSeriesUrl, int refreshTimeSeconds, int maxDaysHistory, boolean selected, String displayName, Color color) {
        this.parentPath = parentPath;
        this.id = id;
        this.description = description;
        this.timeSeriesUrl = timeSeriesUrl;
        this.refreshTimeSeconds = refreshTimeSeconds;
        this.maxDaysHistory = maxDaysHistory;
        this.selected = selected;
        this.displayName = displayName;
        this.color = color;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimeSeriesUrl(String timeSeriesUrl) {
        this.timeSeriesUrl = timeSeriesUrl;
    }

    public void setRefreshTimeSeconds(int refreshTimeSeconds) {
        this.refreshTimeSeconds = refreshTimeSeconds;
    }

    public void setMaxDaysHistory(int maxDaysHistory) {
        this.maxDaysHistory = maxDaysHistory;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTimeSeriesUrl() {
        return timeSeriesUrl;
    }

    public int getRefreshTimeSeconds() {
        return refreshTimeSeconds;
    }

    public int getMaxDaysHistory() {
        return maxDaysHistory;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
