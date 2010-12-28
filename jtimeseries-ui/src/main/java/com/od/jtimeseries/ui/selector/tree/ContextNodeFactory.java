package com.od.jtimeseries.ui.selector.tree;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.swing.progress.IconComponentAnimator;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 27/12/10
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */
public class ContextNodeFactory<E extends UIPropertiesTimeSeries> {

    private JTree tree;
    private Class seriesClass;

    private IconComponentAnimator serverNodeAnimator = new IconComponentAnimator(
       "/progressAnimation/loading",
       ".gif",
       18, 1, 50, 0, false,
       16,
       16
    );

    public ContextNodeFactory(JTree tree, Class seriesClass) {
        this.tree = tree;
        this.seriesClass = seriesClass;
        serverNodeAnimator.setBackgroundImage("/images/server_client2_16x16.png", 0.65f);
    }

    public AbstractSeriesSelectionTreeNode buildNode(Identifiable identifiable) {
        AbstractSeriesSelectionTreeNode result = null;
        if ( identifiable instanceof TimeSeriesServerContext) {
            result = buildServerNode((TimeSeriesServerContext) identifiable);
        } else if ( identifiable instanceof TimeSeriesContext) {
            result = buildContextNode((TimeSeriesContext)identifiable);
        } else if ( seriesClass.isAssignableFrom(identifiable.getClass())) {
            result = buildSeriesNode((E)identifiable);
        }
        return result;
    }

    private ServerTreeNode buildServerNode(TimeSeriesServerContext serverContext) {
        return new ServerTreeNode(tree, serverContext, serverNodeAnimator);
    }

    private ContextTreeNode buildContextNode(TimeSeriesContext context) {
        return new ContextTreeNode(context);
    }

    private SeriesTreeNode buildSeriesNode(E s) {
        return new SeriesTreeNode<E>(s);
    }
}
