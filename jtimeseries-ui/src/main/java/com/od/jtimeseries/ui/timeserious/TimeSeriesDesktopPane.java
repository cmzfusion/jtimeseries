package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.visualizer.TimeSeriesVisualizer;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 17:49:07
 */
public class TimeSeriesDesktopPane extends JDesktopPane {

    private TimeSeriesServerDictionary timeSeriesServerDictionary;
    private List<VisualizerInternalFrame> visualizerFrames = new ArrayList<VisualizerInternalFrame>();

    public TimeSeriesDesktopPane(TimeSeriesServerDictionary timeSeriesServerDictionary) {
        this.timeSeriesServerDictionary = timeSeriesServerDictionary;
    }

    public void createAndAddVisualizer() {
        TimeSeriesVisualizer visualizer = createVisualizer();
        VisualizerInternalFrame visualizerFrame = new VisualizerInternalFrame(visualizer);
        visualizerFrames.add(visualizerFrame);
        add(visualizerFrame);
        visualizerFrame.setVisible(true);
    }

    public List<VisualizerInternalFrame> getVisualizerFrames() {
        return visualizerFrames;
    }

    private TimeSeriesVisualizer createVisualizer() {
        return new TimeSeriesVisualizer(
                "Visualizer",
                timeSeriesServerDictionary
        );
    }

}
