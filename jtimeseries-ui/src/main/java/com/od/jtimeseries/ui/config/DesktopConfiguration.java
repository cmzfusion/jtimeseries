package com.od.jtimeseries.ui.config;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 25/03/11
 * Time: 06:56
 */
public class DesktopConfiguration implements ExportableConfig {

    public static final String MAIN_DESKTOP_NAME = "Main Desktop";

    private String desktopName;
    private Rectangle frameLocation;
    private Integer frameExtendedState;
    private java.util.List<VisualizerConfiguration> visualizerConfigurations = new LinkedList<VisualizerConfiguration>();

    public DesktopConfiguration(String desktopName) {
        this.desktopName = desktopName;
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

    public String getDesktopName() {
        return desktopName;
    }

    public void setDesktopName(String desktopName) {
        this.desktopName = desktopName;
    }
}
