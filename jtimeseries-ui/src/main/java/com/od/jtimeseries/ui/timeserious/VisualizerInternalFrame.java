package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.visualizer.TimeSeriesVisualizer;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 26-Mar-2010
* Time: 17:59:22
*/
public class VisualizerInternalFrame extends JInternalFrame {

    private TimeSeriesVisualizer visualizer;

    public VisualizerInternalFrame(TimeSeriesVisualizer visualizer) {
        super(visualizer.getChartsTitle(), true, true, true, true);
        this.visualizer = visualizer;
        setFrameIcon(ImageUtils.SERIES_ICON_16x16);
        getContentPane().add(visualizer);
        setSize(800,600);
    }

    public TimeSeriesVisualizer getVisualizer() {
        return visualizer;
    }
}
