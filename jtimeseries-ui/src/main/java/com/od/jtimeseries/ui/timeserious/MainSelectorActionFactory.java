package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.SelectorActionFactory;
import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 16/03/11
* Time: 07:03
*/
public class MainSelectorActionFactory implements SelectorActionFactory {

    private TimeSeriesContext rootContext;
    private ApplicationActionModels applicationActionModels;
    private SeriesSelectionPanel<UIPropertiesTimeSeries> selectionPanel;
    private TimeSeriesServerDictionary timeSeriesServerDictionary;
    private JComponent parentSelector;
    private IdentifiableListActionModel selectionModel;

    public MainSelectorActionFactory(TimeSeriesContext rootContext, ApplicationActionModels applicationActionModels, SeriesSelectionPanel<UIPropertiesTimeSeries> selectionPanel, TimeSeriesServerDictionary timeSeriesServerDictionary, JComponent parentSelector) {
        this.rootContext = rootContext;
        this.applicationActionModels = applicationActionModels;
        this.selectionPanel = selectionPanel;
        this.timeSeriesServerDictionary = timeSeriesServerDictionary;
        this.parentSelector = parentSelector;
        this.selectionModel = selectionPanel.getSelectionActionModel();
    }

    private Action addSeriesAction = new AddSeriesToActiveVisualizerAction(
                applicationActionModels.getVisualizerSelectionActionModel(),
                selectionModel);

    private Action refreshServerAction = new RefreshServerSeriesAction(rootContext, selectionModel);
    private Action removeServerAction = new RemoveServerAction(parentSelector, timeSeriesServerDictionary, selectionModel);
    private Action renameServerAction = new RenameServerAction(parentSelector, selectionModel);

    public java.util.List<Action> getActions(SelectorComponent s, List<Identifiable> selectedIdentifiable) {
        java.util.List<Action> result;
        if (selectionModel.isSelectionLimitedToType(UIPropertiesTimeSeries.class)) {
            result = Arrays.asList(
                addSeriesAction
            );
        } else if (selectionModel.isSelectionLimitedToType(TimeSeriesServerContext.class)) {
            result = Arrays.asList(
                    addSeriesAction,
                    refreshServerAction,
                    removeServerAction,
                    renameServerAction
            );
        } else {
            result = Collections.emptyList();
        }
        return result;
    }
}
