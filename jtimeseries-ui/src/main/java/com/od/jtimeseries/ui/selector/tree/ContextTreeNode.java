package com.od.jtimeseries.ui.selector.tree;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.swing.progress.AnimatedIconTree;
import com.od.swing.progress.IconComponentAnimator;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 20/12/10
* <p/>
* To change this template use File | Settings | File Templates.
*/
class ContextTreeNode extends AbstractSeriesSelectionTreeNode implements AnimatedIconTree.ProgressTreeNode {

    private JTree tree;
    private TimeSeriesContext context;
    private Icon icon;

    private IconComponentAnimator animator;

    public ContextTreeNode(JTree tree, TimeSeriesContext context, IconComponentAnimator animator) {
        this.tree = tree;
        this.context = context;
        this.animator = animator;
        icon = context instanceof TimeSeriesServerContext ? ImageUtils.TIMESERIES_SERVER_ICON_16x16 : ImageUtils.CONTEXT_ICON_16x16;
    }

    public TimeSeriesContext getContext() {
        return context;
    }

    public String toString() {
        return context.toString();
    }

    @Override
    protected Identifiable getIdentifiable() {
        return context;
    }

    protected Icon getIcon() {
        return icon;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    public IconComponentAnimator getIconComponentAnimator() {
        return animator;
    }

    public Rectangle getBounds() {
        return tree.getPathBounds(
            new TreePath(getPath())
        );
    }

    public boolean isAnimationEnabled() {
        return context instanceof TimeSeriesServerContext;
    }

    public void setAnimatedIcon(Icon i) {
        icon = i;
    }
}
