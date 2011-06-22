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
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04/03/11
 * Time: 18:19
 */
public class FindInMainSelectorAction extends AbstractAction {

    private SeriesSelectionPanel mainSelectionPanel;
    private IdentifiableListActionModel selections;

    public FindInMainSelectorAction(SeriesSelectionPanel mainSelectionPanel, IdentifiableListActionModel selections) {
        super("Find in Main Selector", ImageUtils.FIND_IN_MAIN_SELECTOR_16x16);
        super.putValue(SHORT_DESCRIPTION, "Find the selected series in the main series selector");
        this.mainSelectionPanel = mainSelectionPanel;
        this.selections = selections;
    }

    public void actionPerformed(ActionEvent e) {
        List<Identifiable> selected = selections.getSelected(Identifiable.class);
        mainSelectionPanel.showSelections(selected);
    }
}
