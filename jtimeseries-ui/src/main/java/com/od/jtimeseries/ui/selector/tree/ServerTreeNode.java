package com.od.jtimeseries.ui.selector.tree;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.progress.AnimatedIconTree;
import com.od.swing.progress.IconComponentAnimator;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 27/12/10
 * Time: 21:14
 * To change this template use File | Settings | File Templates.
 */
class ServerTreeNode extends ContextTreeNode implements AnimatedIconTree.ProgressTreeNode {

    private JTree tree;
    private TimeSeriesServerContext context;

    private IconComponentAnimator animator;
    private Icon animatedIcon = ImageUtils.TIMESERIES_SERVER_ICON_16x16;

    public ServerTreeNode(JTree tree, TimeSeriesServerContext context, IconComponentAnimator animator) {
        super(context);
        this.tree = tree;
        this.context = context;
        this.animator = animator;
    }

    public String toString() {
        return context.toString();
    }

    public TimeSeriesContext getIdentifiable() {
        return context;
    }

    protected Icon getIcon() {
        return isAnimationEnabled() ? animatedIcon :
            context.isConnectionFailed() ?
                ImageUtils.TIMESERIES_SERVER_OFFLINE_ICON_16x16 :
                ImageUtils.TIMESERIES_SERVER_ICON_16x16;
    }

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
        return context.isLoading();
    }

    public void setAnimatedIcon(Icon i) {
        animatedIcon = i;
    }
}
