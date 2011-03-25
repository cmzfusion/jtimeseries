package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.selector.tree.AbstractSeriesSelectionTreeNode;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11/03/11
 * Time: 12:15
 */
public class DesktopContextTreeNode extends AbstractSeriesSelectionTreeNode {

    private DesktopContext context;

    public DesktopContextTreeNode(DesktopContext context) {
        this.context = context;
    }

    public String toString() {
        return context.toString();
    }

    protected DesktopContext getIdentifiable() {
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

