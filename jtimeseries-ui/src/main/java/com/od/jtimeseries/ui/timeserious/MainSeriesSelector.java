package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.download.panel.LoadSeriesFromServerCommand;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.selector.shared.*;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;
import com.od.jtimeseries.ui.timeserious.action.VisualizerSelectionActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 24-Nov-2010
 * Time: 09:36:25
 */
public class MainSeriesSelector extends JPanel {

    private SeriesSelectionPanel<UIPropertiesTimeSeries> selectionPanel;
    private TimeSeriesContext rootContext;
    private ApplicationActionModels applicationActionModels;
    private DisplayNameCalculator displayNameCalculator;

    public MainSeriesSelector(TimeSeriesContext rootContext, ApplicationActionModels applicationActionModels, DisplayNameCalculator displayNameCalculator) {
        this.rootContext = rootContext;
        this.applicationActionModels = applicationActionModels;
        this.displayNameCalculator = displayNameCalculator;
        selectionPanel = new SeriesSelectionPanel<UIPropertiesTimeSeries>(
            rootContext,
            UIPropertiesTimeSeries.class
        );
        selectionPanel.setSeriesSelectionEnabled(false);
        selectionPanel.setSelectorActionFactory(new MainSelectorActionFactory());

        setLayout(new BorderLayout());
        add(selectionPanel, BorderLayout.CENTER);
    }

    private class MainSelectorActionFactory implements SelectorActionFactory {

        private Action addSeriesAction = new AddSeriesToActiveVisualizerAction(
                    applicationActionModels.getVisualizerSelectionActionModel(),
                    selectionPanel.getSelectionActionModel());

        private Action refreshServerAction = new RefreshServerSeriesAction(selectionPanel.getSelectionActionModel());
        private Action removeServerAction = new RemoveServerAction(selectionPanel.getSelectionActionModel());

        public java.util.List<Action> getActions(SelectorComponent s, java.util.List<Identifiable> selectedIdentifiable) {
            return Arrays.asList(
                    addSeriesAction,
                    refreshServerAction,
                    removeServerAction
            );
        }
    }

    private class RemoveServerAction extends ModelDrivenAction<IdentifiableListActionModel> {

        public RemoveServerAction(IdentifiableListActionModel actionModel) {
            super(actionModel, "Remove server", ImageUtils.TIMESERIES_SERVER_REMOVE_ICON_16x16);
        }

        public void actionPerformed(ActionEvent e) {
            java.util.List<TimeSeriesServerContext> serverContexts = getActionModel().getSelected(TimeSeriesServerContext.class);
            for ( TimeSeriesServerContext s : serverContexts ) {
                rootContext.removeChild(s);
            }
        }

        public boolean isModelStateActionable() {
            return getActionModel().isSelectionLimitedToType(TimeSeriesServerContext.class);
        }
    }

    private class RefreshServerSeriesAction extends ModelDrivenAction<IdentifiableListActionModel> {

        public RefreshServerSeriesAction(IdentifiableListActionModel actionModel) {
            super(actionModel, "Refresh Series from Server", ImageUtils.TIMESERIES_SERVER_REFRESH_ICON_16x16);
        }

        public void actionPerformed(ActionEvent e) {
            java.util.List<TimeSeriesServerContext> serverContexts = getActionModel().getSelected(TimeSeriesServerContext.class);
            LoadSeriesFromServerCommand l = new LoadSeriesFromServerCommand(rootContext, displayNameCalculator );
            for ( TimeSeriesServerContext c : serverContexts ) {
                l.execute(c.getServer());
            }
        }

        public boolean isModelStateActionable() {
            return getActionModel().isSelectionLimitedToType(TimeSeriesServerContext.class);
        }
    }

    private class AddSeriesToActiveVisualizerAction extends ModelDrivenAction<IdentifiableListActionModel> {

        private VisualizerSelectionActionModel visualizerSelectionActionModel;

        public AddSeriesToActiveVisualizerAction(VisualizerSelectionActionModel visualizerSelectionActionModel, IdentifiableListActionModel actionModel) {
            super(actionModel, "Add to Visualizer", ImageUtils.ADD_TO_VISUALIZER_16x16);
            this.visualizerSelectionActionModel = visualizerSelectionActionModel;
        }

        public void actionPerformed(ActionEvent e) {
            java.util.List<UIPropertiesTimeSeries> selectedSeries = getActionModel().getSelected(UIPropertiesTimeSeries.class);

            VisualizerInternalFrame v = visualizerSelectionActionModel.getSelectedVisualizer();
            if ( v != null) {
                v.getVisualizer().addTimeSeries(selectedSeries);
            }
        }

        public boolean isModelStateActionable() {
            return getActionModel().isSelectionLimitedToType(UIPropertiesTimeSeries.class);
        }
    }
}
