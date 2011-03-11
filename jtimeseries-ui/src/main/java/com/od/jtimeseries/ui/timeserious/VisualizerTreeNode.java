package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.selector.tree.AbstractSeriesSelectionTreeNode;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11/03/11
 * Time: 12:18
 */
public class VisualizerTreeNode  extends AbstractSeriesSelectionTreeNode {

    private VisualizerNode identifiable;

    public VisualizerTreeNode(VisualizerNode identifiable) {
        this.identifiable = identifiable;
    }

    public String toString() {
        return identifiable.toString();
    }

    @Override
    protected VisualizerNode getIdentifiable() {
        return identifiable;
    }

    protected Icon getIcon() {
        return ImageUtils.ADD_TO_VISUALIZER_16x16;
    }

    @Override
    public boolean isSelected() {
        return false;
    }
}

