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

import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.displaypattern.EditDisplayNamePatternsAction;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.DisplayNamesContext;
import com.od.jtimeseries.ui.identifiable.TimeSeriesServerContext;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.ui.selector.shared.SelectorPopupMenuPopulator;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.timeserious.action.*;
import com.od.jtimeseries.ui.timeserious.rootcontext.TimeSeriousRootContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 16/03/11
* Time: 07:03
*/
public class MainSelectorPopupMenuPopulator implements SelectorPopupMenuPopulator {

    private IdentifiableListActionModel selectionModel;
    private Action addSeriesAction;
    private Action refreshServerAction;
    private Action removeServerAction;
    private Action renameServerAction;
    private Action showHiddenVisualizerAction;
    private Action removeVisualizerAction;
    private Action removeDesktopAction;
    private Action showHiddenDesktopAction;
    private Action renameAction;
    private Action newDesktopAction;
    private Action newVisualizerAction;
    private Action editDisplayNamePatternsAction;

    private TimeSeriesServerDictionary timeSeriesServerDictionary;
    private JComponent parentComponent;

    public MainSelectorPopupMenuPopulator(TimeSeriousRootContext rootContext, ApplicationActionModels applicationActionModels, SeriesSelectionPanel<UIPropertiesTimeSeries> selectionPanel, TimeSeriesServerDictionary timeSeriesServerDictionary, DisplayNameCalculator displayNameCalculator, JComponent parentComponent) {
        this.timeSeriesServerDictionary = timeSeriesServerDictionary;
        this.parentComponent = parentComponent;
        this.selectionModel = selectionPanel.getSelectionActionModel();
        addSeriesAction = new AddSeriesToActiveVisualizerAction(applicationActionModels.getVisualizerSelectionActionModel(), selectionModel);
        refreshServerAction = new RefreshServerSeriesAction(rootContext, selectionModel);
        removeServerAction = new RemoveServerAction(parentComponent, timeSeriesServerDictionary, selectionModel);
        renameServerAction = new RenameServerAction(parentComponent, selectionModel);
        showHiddenVisualizerAction = new ShowHiddenVisualizerAction(selectionModel);
        removeVisualizerAction = new RemoveVisualizerAction(selectionModel, parentComponent);
        removeDesktopAction = new RemoveDesktopAction(selectionModel, parentComponent);
        showHiddenDesktopAction = new ShowHiddenDesktopAction(selectionModel);
        renameAction = new RenameAction(parentComponent, selectionModel);
        newDesktopAction = new NewDesktopAction(parentComponent, rootContext, applicationActionModels.getDesktopSelectionActionModel());
        newVisualizerAction = new NewVisualizerAction(parentComponent, applicationActionModels.getDesktopSelectionActionModel(), applicationActionModels.getVisualizerSelectionActionModel());     editDisplayNamePatternsAction = new EditDisplayNamePatternsAction(parentComponent, displayNameCalculator);
    }

    public void addMenuItems(JPopupMenu menu, SelectorComponent s, List<Identifiable> selectedIdentifiable) {
        if (selectionModel.isSelectionLimitedToTypes(UIPropertiesTimeSeries.class)) {
            menu.add(new JMenuItem(addSeriesAction));
        } else if ( selectionModel.isSelectionLimitedToTypes(VisualizerContext.class)) {
            menu.add(showHiddenVisualizerAction);
            menu.add(removeVisualizerAction);
            menu.add(renameAction);
        } else if ( selectionModel.isSelectionLimitedToTypes(TimeSeriesServerContext.class)) {
            menu.add(refreshServerAction);
            menu.add(removeServerAction);
            menu.add(renameServerAction);
        } else if ( selectionModel.isSelectionLimitedToTypes(DesktopContext.class)) {
            menu.add(removeDesktopAction);
            menu.add(showHiddenDesktopAction);
            menu.add(renameAction);
            menu.add(new JMenuBar());
            menu.add(newVisualizerAction);
        } else if ( selectionModel.isSelectionLimitedToTypes(DisplayNamesContext.class)) {
            menu.add(editDisplayNamePatternsAction);
        } else if ( selectionModel.getSelected().size() == 0) {
            JFrame windowAncestor = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
            menu.add(new NewServerAction(
                windowAncestor,
                timeSeriesServerDictionary
            ));
            menu.add(newDesktopAction);
        }
    }

}
