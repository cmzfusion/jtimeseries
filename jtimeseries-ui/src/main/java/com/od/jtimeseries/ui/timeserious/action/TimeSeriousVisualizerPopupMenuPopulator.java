package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.ui.visualizer.VisualizerSelectionPopupMenuPopulator;
import com.od.jtimeseries.util.identifiable.Identifiable;

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
