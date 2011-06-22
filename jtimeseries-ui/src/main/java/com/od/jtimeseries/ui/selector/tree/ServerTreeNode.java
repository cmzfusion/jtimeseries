/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.ui.selector.tree;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.identifiable.TimeSeriesServerContext;
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
