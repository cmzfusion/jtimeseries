package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.selector.tree.AbstractSeriesSelectionTreeNode;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11/03/11
 * Time: 12:15
 */
public class VisualizerContextTreeNode extends AbstractSeriesSelectionTreeNode {

    private VisualizerContext context;

    public VisualizerContextTreeNode(VisualizerContext context) {
        this.context = context;
    }

    public String toString() {
        return context.toString();
    }

    protected VisualizerContext getIdentifiable() {
        return context;
    }

    protected Icon getIcon() {
        return ImageUtils.ADD_TO_VISUALIZER_16x16;
    }

    @Override
    public boolean isSelected() {
        return false;
    }
}

