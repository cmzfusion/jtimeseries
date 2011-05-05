package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.selector.tree.IdentifiableTreeComparator;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;
import com.od.jtimeseries.ui.config.ConfigAware;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.visualizer.ImportExportTransferHandler;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.swing.util.ProxyingPropertyChangeListener;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 24-Nov-2010
 * Time: 09:36:25
 */
public class MainSeriesSelector extends JPanel implements ConfigAware {

    private SeriesSelectionPanel<UIPropertiesTimeSeries> selectionPanel;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public MainSeriesSelector(TimeSeriousRootContext rootContext, ApplicationActionModels applicationActionModels, TimeSeriesServerDictionary dictionary) {
        selectionPanel = new SeriesSelectionPanel<UIPropertiesTimeSeries>(
            rootContext,
            UIPropertiesTimeSeries.class,
            new MainSelectorTreeNodeFactory(UIPropertiesTimeSeries.class)
        );

        //don't enable selection of series for charting, that's not the purpose of the main selector
        selectionPanel.setSeriesSelectionEnabled(false);

        selectionPanel.setTreeComparator(new MainSelectorTreeComparator());

        MainSelectorPopupMenuPopulator selectorActionFactory = new MainSelectorPopupMenuPopulator(
            rootContext,
            applicationActionModels,
            selectionPanel,
            dictionary,
            this
        );

        selectionPanel.setSelectorActionFactory(selectorActionFactory);
        addProxyingPropertyListeners();

        selectionPanel.getSelectionActionModel().addActionModelListener(new MainSelectorTreeSelectionListener(selectionPanel));

        selectionPanel.setTransferHandler(new ImportExportTransferHandler(rootContext,
                selectionPanel.getSelectionActionModel()));

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

    public SeriesSelectionPanel<UIPropertiesTimeSeries> getSelectionPanel() {
        return selectionPanel;
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    private static class MainSelectorTreeComparator extends IdentifiableTreeComparator {
        public int compare(Identifiable o1, Identifiable o2) {
            //sort server context first
            boolean o1IsServerContext = o1 instanceof TimeSeriesServerContext;
            boolean o2IsServerContext = o2 instanceof TimeSeriesServerContext;
            if ( o1IsServerContext != o2IsServerContext) {
                return o1IsServerContext ? -1 : 1;
            }
            return super.compare(o1, o2);
        }
    }
}
