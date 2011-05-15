package com.od.jtimeseries.ui.identifiable;

import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 30/03/11
 * Time: 06:48
 */
public interface PeerVisualizerFrame {

    VisualizerConfiguration getVisualizerConfiguration();

    Rectangle getBounds();

    int getZPosition();

    boolean isIcon();

    void addTimeSeries(java.util.List<UIPropertiesTimeSeries> selectedSeries);
}
