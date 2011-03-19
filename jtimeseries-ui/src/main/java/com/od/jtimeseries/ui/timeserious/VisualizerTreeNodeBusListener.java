package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.event.TimeSeriousBusListenerAdapter;
import com.od.jtimeseries.util.identifiable.Identifiable;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 15/03/11
* Time: 19:38
*
* Create and manage visualizer selector nodes in the main selector tree
*/
class VisualizerTreeNodeBusListener extends TimeSeriousBusListenerAdapter {

    private Identifiable visualizerContext;

    public VisualizerTreeNodeBusListener(Identifiable visualizerContext) {
        this.visualizerContext = visualizerContext;
    }

    public void visualizerFrameDisplayed(VisualizerInternalFrame visualizerFrame) {
        VisualizerNode v = visualizerContext.get(visualizerFrame.getTitle(), VisualizerNode.class);
        if ( v == null ) {
            v = new VisualizerNode(visualizerFrame.getTitle(), visualizerFrame);
            v.shown(visualizerFrame);
            visualizerContext.addChild(v);
        } else {
            v.shown(visualizerFrame);
        }
    }

    public void visualizerFrameDisposed(VisualizerInternalFrame visualizerFrame) {
        VisualizerNode v = visualizerContext.get(visualizerFrame.getTitle(), VisualizerNode.class);
        if ( v != null) {
            v.hidden();
        }
    }

}
