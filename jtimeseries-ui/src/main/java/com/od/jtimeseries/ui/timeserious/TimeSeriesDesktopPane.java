package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.visualizer.TimeSeriesVisualizer;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.visualizer.VisualizerConfiguration;

import javax.swing.*;
import java.beans.PropertyVetoException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 17:49:07
 */
public class TimeSeriesDesktopPane extends JDesktopPane {

    private TimeSeriesServerDictionary timeSeriesServerDictionary;

    public TimeSeriesDesktopPane(TimeSeriesServerDictionary timeSeriesServerDictionary) {
        this.timeSeriesServerDictionary = timeSeriesServerDictionary;
    }

    public void createAndAddVisualizer() {
        createAndAddVisualizer(null);
    }

    public void createAndAddVisualizer(VisualizerConfiguration c) {
        TimeSeriesVisualizer visualizer = createVisualizer();
        VisualizerInternalFrame visualizerFrame = new VisualizerInternalFrame(visualizer);
        if ( c != null) {
            VisualizerConfiguration.setVisualizerConfiguration(visualizer, c);
            visualizerFrame.setBounds(c.getFrameBounds());

        }
        add(visualizerFrame);
        visualizerFrame.setVisible(true);
        try {
            visualizerFrame.setIcon(c.isIcon());
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public List<VisualizerConfiguration> getVisualizerConfigurations() {
        List<VisualizerConfiguration> l = new LinkedList<VisualizerConfiguration>();
        for ( JInternalFrame v : getVisualizerFramesByPosition()) {
            VisualizerInternalFrame vf = (VisualizerInternalFrame) v;
            VisualizerConfiguration c = VisualizerConfiguration.createVisualizerConfiguration(vf.getVisualizer());
            c.setIsIcon(v.isIcon());
            c.setFrameBounds(v.getBounds());
            l.add(c);
        }
        return l;
    }

    public void addVisualizers(List<VisualizerConfiguration> visualizerConfigurations) {
        for (VisualizerConfiguration c : visualizerConfigurations) {
            createAndAddVisualizer(c);
        }
    }

    //a list of frames by z position, so that when we load the config and add them they reappear with the
    //same z order
    private JInternalFrame[] getVisualizerFramesByPosition() {
        JInternalFrame[] jInternalFrames = getAllFrames();
        Arrays.sort(jInternalFrames, new Comparator<JInternalFrame>() {
                public int compare(JInternalFrame o1, JInternalFrame o2) {
                    return Integer.valueOf(getPosition(o2)).compareTo(getPosition(o1));
                }
            }
        );
        return jInternalFrames;
    }

    private TimeSeriesVisualizer createVisualizer() {
        return new TimeSeriesVisualizer(
            "Visualizer",
            timeSeriesServerDictionary
        );
    }

}
