package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.ExportableConfigHolder;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 10/03/11
 * Time: 08:37
 */
public class DesktopContext extends HideablePeerContext<DesktopConfiguration, PeerDesktop> implements ExportableConfigHolder {

    public DesktopContext(DesktopConfiguration config) {
        super(config.getDesktopName(), config.getDesktopName(), config, config.isShown());

        for ( VisualizerConfiguration v : config.getVisualizerConfigurations()) {
            VisualizerContext n = new VisualizerContext(v);
            addChild(n);
        }
    }

    private List<VisualizerConfiguration> getVisualizerConfigurations() {
        List<VisualizerConfiguration> result = new LinkedList<VisualizerConfiguration>();
        for ( VisualizerContext n : findAll(VisualizerContext.class).getAllMatches()) {
            VisualizerConfiguration c = n.getConfiguration();
            result.add(c);
        }
        return result;
    }

    public DesktopConfiguration createPeerConfig(boolean isShown) {
        PeerDesktop peerFrame = getPeerResource();
        DesktopConfiguration d = new DesktopConfiguration(getId());
        d.setVisualizerConfigurations(getVisualizerConfigurations());
        d.setFrameExtendedState(peerFrame.getExtendedState());
        d.setFrameLocation(peerFrame.getBounds());
        d.setShown(isShown);
        return d;
    }

    public int getFrameExtendedState() {
        return getConfiguration().getFrameExtendedState();
    }

    public Rectangle getFrameLocation() {
        return getConfiguration().getFrameLocation();
    }

    public ExportableConfig getExportableConfig() {
        return getConfiguration();
    }

    public String getDefaultFileName() {
        return "timeSeriousDesktop_" + getId();
    }

    public boolean isMainDesktopContext() {
        return getId().equals(DesktopConfiguration.MAIN_DESKTOP_NAME);
    }

    public ContextNameCheckUtility getNameCheckUtility() {
        return getPeerResource().getNameCheckUtility();
    }
}
