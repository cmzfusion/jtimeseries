package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.selector.tree.AbstractSeriesSelectionTreeNode;
import com.od.jtimeseries.ui.selector.tree.SelectorTreeNodeFactory;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 16/03/11
* Time: 07:05
*/
public class MainSelectorTreeNodeFactory extends SelectorTreeNodeFactory<UIPropertiesTimeSeries> {

    public MainSelectorTreeNodeFactory(Class seriesClass) {
        super(seriesClass);
    }

    public AbstractSeriesSelectionTreeNode buildNode(Identifiable identifiable, JTree tree) {
        AbstractSeriesSelectionTreeNode result;
        if ( identifiable instanceof DesktopContext) {
            result = new DesktopContextTreeNode((DesktopContext)identifiable);
        } else if ( identifiable instanceof  VisualizerNode ) {
            result = new VisualizerTreeNode((VisualizerNode)identifiable);
        } else {
            result = super.buildNode(identifiable, tree);
        }
        return result;
    }
}
