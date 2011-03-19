package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.visualizer.VisualizerConfiguration;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 10/03/11
 * Time: 08:34
 */
public class VisualizerNode extends IdentifiableBase {

    private VisualizerInternalFrame internalFrame;              //will be null if visualizer frame is hidden
    private VisualizerConfiguration visualizerConfiguration;    //will be null if visualizer frame is shown

    public VisualizerNode(String visualizerName, VisualizerInternalFrame internalFrame) {
        super(visualizerName, visualizerName);
        this.internalFrame = internalFrame;
    }

    public VisualizerNode(String visualizerName, VisualizerConfiguration visualizerConfiguration) {
        super(visualizerName, visualizerName);
        this.visualizerConfiguration = visualizerConfiguration;
    }

    public void hidden() {
        visualizerConfiguration = VisualizerConfiguration.createVisualizerConfiguration(
            internalFrame.getVisualizer()
        );
        visualizerConfiguration.setFrameBounds(internalFrame.getBounds());
        internalFrame = null;
    }

    public void shown(VisualizerInternalFrame v) {
        visualizerConfiguration = null;
        internalFrame = v;
    }

    public boolean isVisualizerHidden() {
        return internalFrame == null;
    }

    public VisualizerConfiguration getVisualizerConfiguration() {
        return visualizerConfiguration == null ? VisualizerConfiguration.createVisualizerConfiguration(
            internalFrame.getVisualizer()) : visualizerConfiguration;
    }

    public VisualizerInternalFrame getInternalFrame() {
        return internalFrame;
    }
}
