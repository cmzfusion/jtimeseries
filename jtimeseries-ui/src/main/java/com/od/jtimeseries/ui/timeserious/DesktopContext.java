package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
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
public class DesktopContext extends HidablePeerContext<DesktopConfiguration, PeerDesktop> implements ExportableConfigHolder {

    public DesktopContext(DesktopConfiguration config) {
        super(config.getTitle(), config.getTitle(), config, config.isShown());
        setContextFactory(new MainSelectorTreeContextFactory());
        createChildVisualizers(config);
    }

    private void createChildVisualizers(DesktopConfiguration config) {
        for ( VisualizerConfiguration v : config.getVisualizerConfigurations()) {
            create(v.getTitle(), v.getTitle(), VisualizerContext.class, v);
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

    public HidablePeerContext<DesktopConfiguration, PeerDesktop> newInstance(TimeSeriesContext parent, DesktopConfiguration config) {
        return new DesktopContext(config);
    }

}
