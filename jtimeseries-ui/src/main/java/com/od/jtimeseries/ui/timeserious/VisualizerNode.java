package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.visualizer.VisualizerConfiguration;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 10/03/11
 * Time: 08:34
 */
public class VisualizerNode extends IdentifiableBase {

    private VisualizerInternalFrame internalFrame;
    private VisualizerConfiguration visualizerConfiguration;

    public VisualizerNode(String id, String description, VisualizerInternalFrame internalFrame) {
        super(id, description);
        this.internalFrame = internalFrame;
    }

    public void hidden() {
        visualizerConfiguration = VisualizerConfiguration.createVisualizerConfiguration(
            internalFrame.getVisualizer()
        );
        internalFrame = null;
    }

    public void shown(VisualizerInternalFrame v) {
        visualizerConfiguration = null;
        internalFrame = v;
    }
}
