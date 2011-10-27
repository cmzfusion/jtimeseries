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
package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.visualizer.VisualizerSelectionPopupMenuPopulator;
import com.od.jtimeseries.identifiable.Identifiable;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02/03/11
 * Time: 06:45
 */
public class TimeSeriousVisualizerPopupMenuPopulator extends VisualizerSelectionPopupMenuPopulator {

    private FindInMainSelectorAction showInMainSelectorAction;

    public TimeSeriousVisualizerPopupMenuPopulator(IdentifiableListActionModel selectionActionModel, SeriesSelectionPanel seriesSelectionPanel) {
        super(selectionActionModel);
        showInMainSelectorAction = new FindInMainSelectorAction(seriesSelectionPanel, selectionActionModel);
    }

    public void addMenuItems(JPopupMenu menu, SelectorComponent s, List<Identifiable> selectedIdentifiable) {
        super.addMenuItems(menu, s, selectedIdentifiable);
        menu.add(new JMenuItem(showInMainSelectorAction));
    }
}
