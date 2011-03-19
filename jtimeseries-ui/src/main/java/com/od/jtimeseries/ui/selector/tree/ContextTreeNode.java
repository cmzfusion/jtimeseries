package com.od.jtimeseries.ui.selector.tree;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 20/12/10
* <p/>
* To change this template use File | Settings | File Templates.
*/
class ContextTreeNode extends AbstractSeriesSelectionTreeNode {

    private TimeSeriesContext context;

    public ContextTreeNode(TimeSeriesContext context) {
        this.context = context;
    }

    public String toString() {
        return context.toString();
    }

    @Override
    protected TimeSeriesContext getIdentifiable() {
        return context;
    }

    protected Icon getIcon() {
        return context instanceof TimeSeriesServerContext ? ImageUtils.TIMESERIES_SERVER_ICON_16x16 : ImageUtils.CONTEXT_ICON_16x16;
    }

    @Override
    public boolean isSelected() {
        return false;
    }
}
