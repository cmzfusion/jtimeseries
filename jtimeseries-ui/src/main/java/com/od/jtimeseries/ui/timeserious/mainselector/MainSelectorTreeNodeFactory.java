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
package com.od.jtimeseries.ui.timeserious.mainselector;

import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.DisplayNamesContext;
import com.od.jtimeseries.ui.identifiable.SettingsContext;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.jtimeseries.ui.selector.tree.AbstractSeriesSelectionTreeNode;
import com.od.jtimeseries.ui.selector.tree.SelectorTreeNodeFactory;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;

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
        } else if ( identifiable instanceof DisplayNamesContext) {
            result = new DisplayNamesTreeNode((DisplayNamesContext)identifiable);
        } else {
            result = super.buildNode(identifiable, tree);
        }
        return result;
    }
}
