package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.ui.config.DesktopConfiguration;
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
public class DesktopContext extends DefaultTimeSeriesContext {

    private int frameExtendedState;
    private Rectangle frameLocation;

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
        return d;
    }

    public void setDesktopConfiguration(DesktopConfiguration c) {
        Integer state = c.getFrameExtendedState();
        frameExtendedState = state == null ? JFrame.NORMAL : state;
        frameLocation = c.getFrameLocation();
        for ( VisualizerConfiguration v : c.getVisualizerConfigurations()) {
            VisualizerContext n = new VisualizerContext(v.getChartsTitle(), v);
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
}
