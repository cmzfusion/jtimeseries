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

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 25/03/11
 * Time: 06:56
 */
public class DesktopConfiguration implements ExportableConfig {

    public static final String MAIN_DESKTOP_NAME = "Main Desktop";

    private String title;
    private Rectangle frameLocation;
    private Integer frameExtendedState;
    private java.util.List<VisualizerConfiguration> visualizerConfigurations = new LinkedList<VisualizerConfiguration>();
    private boolean isShown = true;

    //no params constructor required for bean xml persistence
    public DesktopConfiguration() {}

    public DesktopConfiguration(String desktopTitle) {
        this.title = desktopTitle;
    }

    public Rectangle getFrameLocation() {
        return frameLocation;
    }

    public void setFrameLocation(Rectangle frameLocation) {
        this.frameLocation = frameLocation;
    }

    public Integer getFrameExtendedState() {
        return frameExtendedState;
    }

    public void setFrameExtendedState(Integer frameExtendedState) {
        this.frameExtendedState = frameExtendedState;
    }

    public List<VisualizerConfiguration> getVisualizerConfigurations() {
        return visualizerConfigurations;
    }

    public void setVisualizerConfigurations(List<VisualizerConfiguration> visualizerConfigurations) {
        this.visualizerConfigurations = visualizerConfigurations;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShown() {
        return isShown;
    }

    public void setShown(boolean shown) {
        isShown = shown;
    }
}
