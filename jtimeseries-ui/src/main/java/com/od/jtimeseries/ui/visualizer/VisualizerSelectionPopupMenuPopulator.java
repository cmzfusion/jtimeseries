package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.ui.selector.action.ReconnectSeriesAction;
import com.od.jtimeseries.ui.selector.action.RemoveSeriesAction;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.SelectorPopupMenuPopulator;
import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 02/03/11
* Time: 08:23
*/
public class VisualizerSelectionPopupMenuPopulator implements SelectorPopupMenuPopulator {

    private ReconnectSeriesAction reconnectSeriesAction;
    private RemoveSeriesAction removeSeriesAction;

    public VisualizerSelectionPopupMenuPopulator(IdentifiableListActionModel selectionActionModel) {
        reconnectSeriesAction = new ReconnectSeriesAction(selectionActionModel);
        removeSeriesAction = new RemoveSeriesAction(selectionActionModel);
    }

    public void addMenuItems(JPopupMenu menu, SelectorComponent s, List<Identifiable> selectedIdentifiable) {
        menu.add(new JMenuItem(removeSeriesAction));
        menu.add(new JMenuItem(reconnectSeriesAction));
    }
}
