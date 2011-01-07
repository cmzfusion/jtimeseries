package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.SelectorActionFactory;
import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.ui.timeseries.RemoteHttpTimeSeries;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.timeseries.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;
import com.od.jtimeseries.ui.timeserious.action.VisualizerSelectionActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.ui.visualizer.TimeSeriesVisualizer;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.swing.action.ListSelectionActionModel;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.awt.*;
import java.awt.List;
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
    private ApplicationActionModels applicationActionModels;

    public MainSeriesSelector(TimeSeriesContext rootContext, ApplicationActionModels applicationActionModels) {
        this.applicationActionModels = applicationActionModels;
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
                    selectionPanel.getSeriesSelectionActionModel());

        //private Action refreshServerAction = new RefreshServerSeriesAction();

        public java.util.List<Action> getActions(SelectorComponent s, java.util.List<Identifiable> selectedIdentifiable) {
            return Collections.singletonList(
                    addSeriesAction
            );
        }
    }

    private class RefreshServerSeriesAction extends ModelDrivenAction<ListSelectionActionModel<Identifiable>> {

        public RefreshServerSeriesAction(ListSelectionActionModel<Identifiable> actionModel) {
            super(actionModel);
        }

        public void actionPerformed(ActionEvent e) {
        }
    }

    private class AddSeriesToActiveVisualizerAction extends ModelDrivenAction<IdentifiableListActionModel> {

        private VisualizerSelectionActionModel visualizerSelectionActionModel;

        public AddSeriesToActiveVisualizerAction(VisualizerSelectionActionModel visualizerSelectionActionModel, IdentifiableListActionModel actionModel) {
            super(actionModel, "Add to Visualizer", ImageUtils.REMOTE_CHART_16x16);
            this.visualizerSelectionActionModel = visualizerSelectionActionModel;
        }

        public void actionPerformed(ActionEvent e) {
            java.util.List<UIPropertiesTimeSeries> selectedSeries = getActionModel().getSelected();
            java.util.List<UiTimeSeriesConfig> configs = new LinkedList<UiTimeSeriesConfig>();
            for ( UIPropertiesTimeSeries s : selectedSeries ) {
                configs.add(new UiTimeSeriesConfig(s));
            }

            VisualizerInternalFrame v = visualizerSelectionActionModel.getSelectedVisualizer();
            if ( v != null) {
                v.getVisualizer().addChartConfigs(
                    configs
                );
            }
        }

        public boolean isModelStateActionable() {
            return true;
        }
    }
}
