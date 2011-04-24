package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.ExportableConfigHolder;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 10/03/11
 * Time: 08:37
 */
public class DesktopContext extends DefaultTimeSeriesContext implements ExportableConfigHolder {

    private int frameExtendedState;
    private Rectangle frameLocation;
    private boolean shown = true;

    public DesktopContext(String name) {
        super(name, name);
    }

    public List<VisualizerConfiguration> getVisualizerConfigurations() {
        List<VisualizerConfiguration> result = new LinkedList<VisualizerConfiguration>();
        for ( VisualizerContext n : findAll(VisualizerContext.class).getAllMatches()) {
            VisualizerConfiguration c = n.getVisualizerConfiguration();
            result.add(c);
        }
        return result;
    }

    public DesktopConfiguration getDesktopConfiguration() {
        DesktopConfiguration d = new DesktopConfiguration(getId());
        d.setVisualizerConfigurations(getVisualizerConfigurations());
        d.setFrameExtendedState(frameExtendedState);
        d.setFrameLocation(frameLocation);
        d.setShown(shown);
        return d;
    }

    public void createDesktopConfiguration(DesktopConfiguration c) {
        Integer state = c.getFrameExtendedState();
        frameExtendedState = state == null ? JFrame.NORMAL : state;
        frameLocation = c.getFrameLocation();
        shown = c.isShown();
        for ( VisualizerConfiguration v : c.getVisualizerConfigurations()) {
            VisualizerContext n = new VisualizerContext(v);
            addChild(n);
        }
    }

    public int getFrameExtendedState() {
        return frameExtendedState;
    }

    public void setFrameExtendedState(int frameExtendedState) {
        this.frameExtendedState = frameExtendedState;
    }

    public Rectangle getFrameLocation() {
        return frameLocation;
    }

    public void setFrameLocation(Rectangle frameLocation) {
        this.frameLocation = frameLocation;
    }

    public ExportableConfig getExportableConfig() {
        return getDesktopConfiguration();
    }

    public String getDefaultFileName() {
        return "timeSeriousDesktop_" + getId();
    }

    public boolean isShown() {
        return shown;
    }

    public boolean isMainDesktopContext() {
        return getId().equals(DesktopConfiguration.MAIN_DESKTOP_NAME);
    }
}
