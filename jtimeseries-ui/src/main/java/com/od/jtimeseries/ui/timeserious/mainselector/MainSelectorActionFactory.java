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
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.displaypattern.EditDisplayNamePatternsAction;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.DisplayNamesContext;
import com.od.jtimeseries.ui.identifiable.TimeSeriesServerContext;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.selector.shared.SelectorActionFactory;
import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.timeserious.action.*;
import com.od.jtimeseries.ui.timeserious.rootcontext.TimeSeriousRootContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;

import javax.swing.*;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 16/03/11
* Time: 07:03
*/
public class MainSelectorActionFactory implements SelectorActionFactory {

    private IdentifiableListActionModel selectionModel;
    private Action addSeriesAction;
    private Action showSeriesInNewVisualizerAction;
    private Action refreshServerAction;
    private Action removeServerAction;
    private Action renameServerAction;
    private Action showVisualizerAction;
    private Action removeVisualizerAction;
    private Action removeDesktopAction;
    private Action showDesktopAction;
    private Action hideDesktopAction;
    private Action renameAction;
    private Action newDesktopAction;
    private Action newVisualizerAction;
    private Action editDisplayNamePatternsAction;

    private TimeSeriesServerDictionary timeSeriesServerDictionary;
    private JComponent parentComponent;

    public MainSelectorActionFactory(TimeSeriousRootContext rootContext, ApplicationActionModels applicationActionModels, SeriesSelectionPanel<UIPropertiesTimeSeries> selectionPanel, TimeSeriesServerDictionary timeSeriesServerDictionary, DisplayNameCalculator displayNameCalculator, JComponent parentComponent) {
        this.timeSeriesServerDictionary = timeSeriesServerDictionary;
        this.parentComponent = parentComponent;
        this.selectionModel = selectionPanel.getSelectionActionModel();
        addSeriesAction = new AddSeriesToActiveVisualizerAction(applicationActionModels.getVisualizerSelectionActionModel(), selectionModel);
        showSeriesInNewVisualizerAction = new ShowSeriesInNewVisualizerAction(
            parentComponent,
            applicationActionModels.getDesktopSelectionActionModel(),
            applicationActionModels.getVisualizerSelectionActionModel(),
            selectionModel
        );
        refreshServerAction = new RefreshServerSeriesAction(rootContext, selectionModel);
        removeServerAction = new RemoveServerAction(parentComponent, timeSeriesServerDictionary, selectionModel);
        renameServerAction = new RenameServerAction(parentComponent, selectionModel);
        showVisualizerAction = new ShowHidableVisualizerAction(selectionModel);
        removeVisualizerAction = new RemoveVisualizerAction(selectionModel, parentComponent);
        removeDesktopAction = new RemoveDesktopAction(selectionModel, parentComponent);
        hideDesktopAction = new HideDesktopAction(selectionModel);
        showDesktopAction = new ShowHidableDesktopAction(selectionModel);
        renameAction = new RenameAction(parentComponent, selectionModel);
        newDesktopAction = new NewDesktopAction(parentComponent, rootContext, applicationActionModels.getDesktopSelectionActionModel());
        newVisualizerAction = new NewVisualizerInSelectedDesktopNode(parentComponent, selectionModel,
            applicationActionModels.getVisualizerSelectionActionModel()
        );
        editDisplayNamePatternsAction = new EditDisplayNamePatternsAction(parentComponent, displayNameCalculator);
    }

    public void addMenuItems(JPopupMenu menu, SelectorComponent s, List<Identifiable> selectedIdentifiable) {
        if (selectionModel.isSelectionLimitedToTypes(UIPropertiesTimeSeries.class)) {
            menu.add(addSeriesAction);
            menu.add(showSeriesInNewVisualizerAction);
        } else if ( selectionModel.isSelectionLimitedToTypes(VisualizerContext.class)) {
            menu.add(showVisualizerAction);
            menu.add(removeVisualizerAction);
            menu.add(renameAction);
        } else if ( selectionModel.isSelectionLimitedToTypes(TimeSeriesServerContext.class)) {
            menu.add(refreshServerAction);
            menu.add(removeServerAction);
            menu.add(renameServerAction);
        } else if ( selectionModel.isSelectionLimitedToTypes(DesktopContext.class)) {
            menu.add(showDesktopAction);
            menu.add(hideDesktopAction);
            menu.add(removeDesktopAction);
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

    public Action getDefaultAction(Identifiable selectedIdentifiable) {
        Action result = null;
        if ( UIPropertiesTimeSeries.class.isAssignableFrom(selectedIdentifiable.getClass())) {
            result = showSeriesInNewVisualizerAction;
        } else if ( DesktopContext.class.isAssignableFrom(selectedIdentifiable.getClass())) {
            result = showDesktopAction;
        } else if ( VisualizerContext.class.isAssignableFrom(selectedIdentifiable.getClass())) {
            result = showVisualizerAction;
        }
        return result;
    }

}
