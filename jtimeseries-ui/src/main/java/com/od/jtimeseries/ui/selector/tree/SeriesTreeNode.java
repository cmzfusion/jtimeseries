package com.od.jtimeseries.ui.selector.tree;

import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 20/12/10
* <p/>
* To change this template use File | Settings | File Templates.
*/
class SeriesTreeNode<E extends UIPropertiesTimeSeries> extends AbstractSeriesSelectionTreeNode {

    private E series;

    public SeriesTreeNode(E series) {
        this.series = series;
    }

    public String toString() {
        return series.toString();
    }

    public E getTimeSeries() {
        return series;
    }

    @Override
    protected Identifiable getIdentifiable() {
        return series;
    }

    protected Icon getIcon() {
        return ImageUtils.SERIES_ICON_16x16;
    }

    @Override
    public boolean isSelected() {
        return series.isSelected();
    }
}
