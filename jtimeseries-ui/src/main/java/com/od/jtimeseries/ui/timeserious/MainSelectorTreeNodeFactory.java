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
            result = new DesktopTreeNode((DesktopContext)identifiable);
        } else if ( identifiable instanceof VisualizerContext) {
            result = new VisualizerTreeNode((VisualizerContext)identifiable);
        } else if ( identifiable instanceof SettingsContext) {
            result = new SettingsTreeNode((SettingsContext)identifiable);
        } else {
            result = super.buildNode(identifiable, tree);
        }
        return result;
    }
}
