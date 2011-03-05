package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.ui.selector.action.ReconnectSeriesAction;
import com.od.jtimeseries.ui.selector.action.RemoveSeriesAction;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.SelectorActionFactory;
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
public class VisualizerSelectionActionFactory implements SelectorActionFactory {

    private ReconnectSeriesAction reconnectSeriesAction;
    private RemoveSeriesAction removeSeriesAction;

    public VisualizerSelectionActionFactory(IdentifiableListActionModel selectionActionModel) {
        reconnectSeriesAction = new ReconnectSeriesAction(selectionActionModel);
        removeSeriesAction = new RemoveSeriesAction(selectionActionModel);
    }

    public List<Action> getActions(SelectorComponent s, List<Identifiable> selectedIdentifiable) {
        return new LinkedList(Arrays.asList((Action) removeSeriesAction, reconnectSeriesAction));
    }
}
