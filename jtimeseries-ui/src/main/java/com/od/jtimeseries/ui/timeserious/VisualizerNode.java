package com.od.jtimeseries.ui.timeserious;

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

    public VisualizerNode(String id, String description, VisualizerInternalFrame internalFrame) {
        super(id, description);
        this.internalFrame = internalFrame;
    }


}
