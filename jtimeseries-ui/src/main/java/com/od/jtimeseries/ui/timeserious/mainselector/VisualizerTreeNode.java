package com.od.jtimeseries.ui.timeserious.mainselector;

import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.jtimeseries.ui.selector.tree.AbstractSeriesSelectionTreeNode;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11/03/11
 * Time: 12:18
 */
public class VisualizerTreeNode  extends AbstractSeriesSelectionTreeNode {

    private VisualizerContext identifiable;

    public VisualizerTreeNode(VisualizerContext identifiable) {
        this.identifiable = identifiable;
    }

    public String toString() {
        return identifiable.toString();
    }

    public VisualizerContext getIdentifiable() {
        return identifiable;
    }

    protected Icon getIcon() {
        return ImageUtils.VISUALIZER_16x16;
    }
}

