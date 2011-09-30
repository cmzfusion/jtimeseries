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
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.swing.progress.IconComponentAnimator;
import com.od.swing.progress.RotatingImageSource;
import com.od.swing.progress.SuffixedImageFileSource;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 27/12/10
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */
public class SelectorTreeNodeFactory<E extends UIPropertiesTimeSeries> {

    private Class seriesClass;

    private RotatingImageSource imageSource = new RotatingImageSource(
       "/progressAnimation/progress.png",
       16,
       16,
       16,
       1f
    );

    private IconComponentAnimator serverNodeAnimator = new IconComponentAnimator(
       new IconComponentAnimator.IconComponentAdapter(),
       imageSource,
       50,
       0,
       false
    );

    public SelectorTreeNodeFactory(Class seriesClass) {
        this.seriesClass = seriesClass;
        serverNodeAnimator.setBackgroundImage(ImageUtils.PROGRESS_SERVER_IMAGE, 0.9f);
    }

    public AbstractSeriesSelectionTreeNode buildNode(Identifiable identifiable, JTree tree) {
        AbstractSeriesSelectionTreeNode result = null;
        if ( identifiable instanceof TimeSeriesServerContext) {
            result = buildServerNode((TimeSeriesServerContext) identifiable, tree);
        } else if ( identifiable instanceof TimeSeriesContext) {
            result = buildContextNode((TimeSeriesContext)identifiable);
        } else if ( seriesClass.isAssignableFrom(identifiable.getClass())) {
            result = buildSeriesNode((E)identifiable);
        }
        return result;
    }

    private ServerTreeNode buildServerNode(TimeSeriesServerContext serverContext, JTree tree) {
        return new ServerTreeNode(tree, serverContext, serverNodeAnimator);
    }

    private ContextTreeNode buildContextNode(TimeSeriesContext context) {
        return new ContextTreeNode(context);
    }

    private SeriesTreeNode buildSeriesNode(E s) {
        return new SeriesTreeNode<E>(s);
    }
}
