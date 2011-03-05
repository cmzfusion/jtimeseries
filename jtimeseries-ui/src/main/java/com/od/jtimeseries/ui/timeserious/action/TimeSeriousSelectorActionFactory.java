package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.ui.visualizer.VisualizerSelectionActionFactory;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02/03/11
 * Time: 06:45
 */
public class TimeSeriousSelectorActionFactory extends VisualizerSelectionActionFactory {

    private ShowInMainSelectorAction showInMainSelectorAction;

    public TimeSeriousSelectorActionFactory(IdentifiableListActionModel selectionActionModel, SeriesSelectionPanel seriesSelectionPanel) {
        super(selectionActionModel);
        showInMainSelectorAction = new ShowInMainSelectorAction(seriesSelectionPanel, selectionActionModel);
    }

    public List<Action> getActions(SelectorComponent s, List<Identifiable> selectedIdentifiable) {
        List<Action> actions = super.getActions(s, selectedIdentifiable);
        actions.add(showInMainSelectorAction);
        return actions;
    }
}
