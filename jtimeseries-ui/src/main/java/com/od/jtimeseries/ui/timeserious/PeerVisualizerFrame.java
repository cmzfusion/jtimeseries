package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.visualizer.TimeSeriesVisualizer;

import java.awt.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 30/03/11
 * Time: 06:48
 */
public interface PeerVisualizerFrame {

    TimeSeriesVisualizer getVisualizer();

    Rectangle getBounds();

    int getZPosition();

    boolean isIcon();

    void addTimeSeries(java.util.List<UIPropertiesTimeSeries> selectedSeries);
}
