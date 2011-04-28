package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.SelectorActionFactory;
import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.timeserious.action.*;
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

    private IdentifiableListActionModel selectionModel;
    private Action addSeriesAction;
    private Action refreshServerAction;
    private Action removeServerAction;
    private Action renameServerAction;
    private Action showHiddenVisualizerAction;
    private Action removeVisualizerAction;
    private Action removeDesktopAction;
    private Action showHiddenDesktopAction;

    public MainSelectorActionFactory(TimeSeriousRootContext rootContext, ApplicationActionModels applicationActionModels, SeriesSelectionPanel<UIPropertiesTimeSeries> selectionPanel, TimeSeriesServerDictionary timeSeriesServerDictionary, JComponent parentComponent) {
        this.selectionModel = selectionPanel.getSelectionActionModel();
        addSeriesAction = new AddSeriesToActiveVisualizerAction(applicationActionModels.getVisualizerSelectionActionModel(), selectionModel);
        refreshServerAction = new RefreshServerSeriesAction(rootContext, selectionModel);
        removeServerAction = new RemoveServerAction(parentComponent, timeSeriesServerDictionary, selectionModel);
        renameServerAction = new RenameServerAction(parentComponent, selectionModel);
        showHiddenVisualizerAction = new ShowHiddenVisualizerAction(selectionModel);
        removeVisualizerAction = new RemoveVisualizerAction(selectionModel, parentComponent);
        removeDesktopAction = new RemoveDesktopAction(selectionModel, parentComponent);
        showHiddenDesktopAction = new ShowHiddenDesktopAction(selectionModel);
    }

    public java.util.List<Action> getActions(SelectorComponent s, List<Identifiable> selectedIdentifiable) {
        java.util.List<Action> result = Collections.emptyList();
        if (selectionModel.isSelectionLimitedToType(UIPropertiesTimeSeries.class)) {
            result = Arrays.asList(
                addSeriesAction
            );
        } else if ( selectionModel.isSelectionLimitedToType(VisualizerContext.class)) {
            result = Arrays.asList(
                showHiddenVisualizerAction,
                removeVisualizerAction
            );
        } else if ( selectionModel.isSelectionLimitedToType(TimeSeriesServerContext.class)) {
            result = Arrays.asList(
                refreshServerAction,
                removeServerAction,
                renameServerAction
            );
        } else if ( selectionModel.isSelectionLimitedToType(DesktopContext.class)) {
            result = Arrays.asList(
                removeDesktopAction,
                showHiddenDesktopAction
            );
        }
        return result;
    }
}
