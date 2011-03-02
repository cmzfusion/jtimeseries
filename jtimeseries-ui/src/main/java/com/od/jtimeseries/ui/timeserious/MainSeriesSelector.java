package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.download.panel.LoadSeriesFromServerCommand;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.selector.shared.*;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;
import com.od.jtimeseries.ui.timeserious.action.VisualizerSelectionActionModel;
import com.od.jtimeseries.ui.timeserious.config.ConfigAware;
import com.od.jtimeseries.ui.timeserious.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.swing.action.ModelDrivenAction;
import com.od.swing.util.ProxyingPropertyChangeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 24-Nov-2010
 * Time: 09:36:25
 */
public class MainSeriesSelector extends JPanel implements ConfigAware {

    private SeriesSelectionPanel<UIPropertiesTimeSeries> selectionPanel;
    private TimeSeriesContext rootContext;
    private ApplicationActionModels applicationActionModels;
    private DisplayNameCalculator displayNameCalculator;
    private TimeSeriesServerDictionary dictionary;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public MainSeriesSelector(TimeSeriesContext rootContext, ApplicationActionModels applicationActionModels, DisplayNameCalculator displayNameCalculator, TimeSeriesServerDictionary dictionary) {
        this.rootContext = rootContext;
        this.applicationActionModels = applicationActionModels;
        this.displayNameCalculator = displayNameCalculator;
        this.dictionary = dictionary;
        selectionPanel = new SeriesSelectionPanel<UIPropertiesTimeSeries>(
            rootContext,
            UIPropertiesTimeSeries.class
        );
        selectionPanel.setSeriesSelectionEnabled(false);
        selectionPanel.setSelectorActionFactory(new MainSelectorActionFactory());

        addProxyingPropertyListeners();

        setLayout(new BorderLayout());
        add(selectionPanel, BorderLayout.CENTER);
    }

    private void addProxyingPropertyListeners() {
        //allow clients to subscribe to the main selector to receive
        //tree view selected events from the selector panel
        selectionPanel.addPropertyChangeListener(
            SeriesSelectionPanel.TREE_VIEW_SELECTED_PROPERTY,
            new ProxyingPropertyChangeListener(
                propertyChangeSupport
            )
        );
    }

    public boolean isTableSelectorVisible() {
        return selectionPanel.isTableSelectorVisible();
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
        config.setMainSeriesSelectorTableVisible(selectionPanel.isTableSelectorVisible());
    }

    public void restoreConfig(TimeSeriousConfig config) {
        selectionPanel.setTableSelectorVisible(config.isMainSeriesSelectorTableVisible());
    }

    public List<ConfigAware> getConfigAwareChildren() {
        return Collections.emptyList();
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    private class MainSelectorActionFactory implements SelectorActionFactory {

        private Action addSeriesAction = new AddSeriesToActiveVisualizerAction(
                    applicationActionModels.getVisualizerSelectionActionModel(),
                    selectionPanel.getSelectionActionModel());

        private Action refreshServerAction = new RefreshServerSeriesAction(selectionPanel.getSelectionActionModel());
        private Action removeServerAction = new RemoveServerAction(selectionPanel.getSelectionActionModel());
        private Action renameServerAction = new RenameServerAction(selectionPanel.getSelectionActionModel());

        public java.util.List<Action> getActions(SelectorComponent s, List<Identifiable> selectedIdentifiable) {
            return Arrays.asList(
                    addSeriesAction,
                    refreshServerAction,
                    removeServerAction,
                    renameServerAction
            );
        }
    }

    private class RenameServerAction extends ModelDrivenAction<IdentifiableListActionModel> {

        public RenameServerAction(IdentifiableListActionModel actionModel) {
            super(actionModel, "Rename Server", ImageUtils.TIMESERIES_SERVER_RENAME_ICON_16x16);
        }

        public void actionPerformed(ActionEvent e) {
             java.util.List<TimeSeriesServerContext> serverContexts = getActionModel().getSelected(TimeSeriesServerContext.class);
             for ( TimeSeriesServerContext s : serverContexts ) {
                String name = JOptionPane.showInputDialog(
                        SwingUtilities.getRoot(MainSeriesSelector.this),
                        "Rename Server " + s.getServer().getDescription() + "?",
                        s.getServer().getDescription()
                );
                if ( name != null && name.length() > 0) {
                    s.getServer().setDescription(name);
                }
            }
        }
    }

    private class RemoveServerAction extends ModelDrivenAction<IdentifiableListActionModel> {

        public RemoveServerAction(IdentifiableListActionModel actionModel) {
            super(actionModel, "Remove Server", ImageUtils.TIMESERIES_SERVER_REMOVE_ICON_16x16);
        }

        public void actionPerformed(ActionEvent e) {
            java.util.List<TimeSeriesServerContext> serverContexts = getActionModel().getSelected(TimeSeriesServerContext.class);
            for ( TimeSeriesServerContext s : serverContexts ) {
                int remove = JOptionPane.showConfirmDialog(
                    SwingUtilities.getRoot(MainSeriesSelector.this),
                    "Remove Server " + s.getServer().getDescription() + ", and all its timeseries?",
                    "Remove Server?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if ( remove == JOptionPane.YES_OPTION) {
                    dictionary.removeServer(s.getServer());
                }
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
            LoadSeriesFromServerCommand l = new LoadSeriesFromServerCommand(rootContext);
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
