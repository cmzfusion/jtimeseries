/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.config.*;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.displaypattern.DisplayNamePatternDialog;
import com.od.jtimeseries.ui.displaypattern.EditDisplayNamePatternsAction;
import com.od.jtimeseries.ui.download.ShowDownloadSeriesDialogAction;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.selector.TimeSeriesSelectorListener;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.SelectorPopupMenuPopulator;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.uicontext.ImportExportTransferHandler;
import com.od.jtimeseries.ui.util.JideInitialization;
import com.od.jtimeseries.ui.visualizer.chart.ChartControlPanel;
import com.od.jtimeseries.ui.config.ChartRangeMode;
import com.od.jtimeseries.ui.config.DomainTimeSelection;
import com.od.jtimeseries.ui.visualizer.chart.TimeSeriesChart;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 07-Jan-2009
 * Time: 15:58:18
 *
 * The TimeSeriesVisualizer is designed to be a standalone component that can be integrated
 * into third party applications, as well as used in TimeSerious standalone application.
 * It has a feature which allows time series to be selected and downloaded/synchronized
 * from time series server(s), and charts plotted of selected series. (This feature is disabled
 * in TimeSerious, which has alternative way of adding servers/timeseries).
 * The time series server(s) provide access to time series data via http, and may be remote or
 * local applications, or even a time series service provided by the client application itself.
 */
public class TimeSeriesVisualizer extends JPanel {

    private static final LogMethods logMethods = LogUtils.getLogMethods(TimeSeriesVisualizer.class);

    private JToolBar toolbar;
    private TimeSeriesChart chart;
    private SeriesSelectionPanel<ChartingTimeSeries> seriesSelectionPanel;
    private VisualizerRootContext rootContext;
    private TimeSeriesServerDictionary timeSeriesServerDictionary;
    private EditDisplayNamePatternsAction editDisplayNameAction;
    private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private final ChartControlPanel chartControlPanel;
    private boolean toolbarVisible = true;
    private DisplayNameCalculator displayNameCalculator;

    public TimeSeriesVisualizer(String title, TimeSeriesServerDictionary timeSeriesServerDictionary) {
        this(title, timeSeriesServerDictionary, new DisplayNameCalculator());
    }

    public TimeSeriesVisualizer(String title, TimeSeriesServerDictionary timeSeriesServerDictionary, DisplayNameCalculator displayNameCalculator) {
        this.displayNameCalculator = displayNameCalculator;
        JideInitialization.applyLicense();
        JideInitialization.setupJide();

        this.timeSeriesServerDictionary = timeSeriesServerDictionary;
        this.rootContext = new VisualizerRootContext(timeSeriesServerDictionary, displayNameCalculator);
        chart = new TimeSeriesChart(title);
        chartControlPanel = new ChartControlPanel(chart);
        createDisplayNameAction();
        JPanel chartPanel = createChartPanel();
        createSeriesSelectionPanel();
        createToolbar();
        createSplitPane(chartPanel);
        layoutVisualizer();
        addSeriesSelectionListener();
    }

    public static VisualizerConfiguration createVisualizerConfiguration(TimeSeriesVisualizer visualizer) {
        return new VisualizerConfiguration(
            visualizer.getChartsTitle(),
            visualizer.isTableSelectorVisible(),
            visualizer.getChartConfigs(),
            visualizer.getChartRangeMode(),
            visualizer.getDomainStartTimeSelection(),
            visualizer.getDividerLocation(),
            visualizer.isShowLegendOnChart(),
            visualizer.getChartBackgroundColor(),
            visualizer.getColumns()
        );
    }

    public static void setVisualizerConfiguration(TimeSeriesVisualizer visualizer, VisualizerConfiguration c) {
        visualizer.setChartsTitle(c.getTitle());
        visualizer.setTableSelectorVisible(c.isTableSelectorVisible());
        visualizer.addChartConfigs(c.getChartConfigs());
        visualizer.setChartRangeMode(ChartRangeMode.valueOf(c.getChartRangeMode()));
        visualizer.setDomainStartTimeSelection(c.getDomainStartTimeSelection());
        visualizer.setDividerLocation(c.getDividorLocation());
        visualizer.setShowLegendOnChart(c.isShowLegendOnChart());
        visualizer.setChartBackgroundColor(c.getChartBackgroundColor());

        //if there are no columns, assume we will use the default column set
        if ( c.getTableColumns().size() > 0) {
            visualizer.setColumns(c.getTableColumns());
        }
    }

    private void createSeriesSelectionPanel() {
        seriesSelectionPanel = new SeriesSelectionPanel<ChartingTimeSeries>(rootContext, "Chart", ChartingTimeSeries.class);

        SelectorPopupMenuPopulator popupMenuPopulator = new VisualizerSelectionPopupMenuPopulator(getSelectionActionModel());
        seriesSelectionPanel.setSelectorActionFactory(popupMenuPopulator);
        seriesSelectionPanel.setTransferHandler(new ImportExportTransferHandler(rootContext, getSelectionActionModel()));
    }

    private void layoutVisualizer() {
        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private void createSplitPane(JPanel chartPanel) {
        splitPane.setLeftComponent(seriesSelectionPanel);
        splitPane.setRightComponent(chartPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.5d);
        splitPane.setDividerSize(splitPane.getDividerSize() + 4);
        setDividerLocation(VisualizerConfiguration.DEFAULT_DIVIDER_LOCATION);
    }

    private void createDisplayNameAction() {
        editDisplayNameAction = new EditDisplayNamePatternsAction(this, displayNameCalculator);

        editDisplayNameAction.addDisplayPatternListener(new DisplayNamePatternDialog.DisplayPatternListener() {
            public void displayPatternsChanged(List<DisplayNamePattern> newPatterns, boolean applyNow) {
                //display names may have changed, repaint the ui to reflect the new names
                repaint();
            }
        });
    }

    public void setChartsTitle(String title) {
        chart.setTitle(title);
    }

    public String getChartsTitle() {
        return chart.getTitle();
    }

    public DisplayNamePatternConfig getDisplayNamePatterns() {
        return editDisplayNameAction.getDisplayNamePatterns();
    }

    public void setDisplayNamePatterns(DisplayNamePatternConfig patterns) {
        editDisplayNameAction.setDisplayNamePatterns(patterns);
    }

    public boolean isTableSelectorVisible() {
        return seriesSelectionPanel.isTableSelectorVisible();
    }

    public void setTableSelectorVisible(boolean isVisible) {
        seriesSelectionPanel.setTableSelectorVisible(isVisible);
    }

    public ChartRangeMode getChartRangeMode() {
        return chart.getChartRangeMode();
    }

    public DomainTimeSelection getDomainStartTimeSelection() {
        return chart.getDomainStartTimeSelection();
    }

    public void setDomainStartTimeSelection(DomainTimeSelection domainTimeSelection) {
        chart.setDomainStartTimeSelection(domainTimeSelection);
    }

    public void setChartRangeMode(ChartRangeMode chartRangeMode) {
        chart.setChartRangeMode(chartRangeMode);
        chartControlPanel.refreshStateFromChart();
    }

    public int getDividerLocation() {
        return splitPane.getDividerLocation();
    }

    public void setDividerLocation(int location) {
        splitPane.setDividerLocation(location);
    }

    public void setShowLegendOnChart(boolean showLegendOnChart) {
        chart.setShowLegend(showLegendOnChart);
        chartControlPanel.refreshStateFromChart();        
    }

    public boolean isShowLegendOnChart() {
        return chart.isShowLegend();
    }

    public void setChartBackgroundColor(Color c) {
        chart.setChartBackgroundColor(c);
        chartControlPanel.refreshStateFromChart();
    }

    public Color getChartBackgroundColor() {
        return chart.getChartBackgroundColor();
    }

    public List<ColumnSettings> getColumns() {
        return seriesSelectionPanel.getColumnSettings();
    }

    public void setColumns(List<ColumnSettings> columnSettings) {
        seriesSelectionPanel.setColumnSettings(columnSettings);
    }

    public void setSelectorActionFactory(SelectorPopupMenuPopulator selectorPopupMenuPopulator) {
        seriesSelectionPanel.setSelectorActionFactory(selectorPopupMenuPopulator);
    }

    public IdentifiableListActionModel getSelectionActionModel() {
        return seriesSelectionPanel.getSelectionActionModel();
    }

    public void setToolbarVisible(boolean visible) {
        if ( visible != toolbarVisible ) {
            if ( toolbarVisible ) {
                remove(toolbar);
            } else {
                add(toolbar, BorderLayout.NORTH);
            }
            toolbarVisible = visible;
        }
    }

    private void createToolbar() {
        toolbar = new JToolBar();
        toolbar.add(new JButton(
            new ShowDownloadSeriesDialogAction(
                timeSeriesServerDictionary,
                this,
                rootContext,
                displayNameCalculator
            )
        ));
        toolbar.add(Box.createHorizontalStrut(5));
        toolbar.add(new JButton(editDisplayNameAction));
    }

    private JPanel createChartPanel() {
        JPanel chartPanel = new JPanel();
        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(chart, BorderLayout.CENTER);
        chartPanel.add(chartControlPanel, BorderLayout.SOUTH);
        return chartPanel;
    }

    public List<UiTimeSeriesConfig> getChartConfigs() {
        List<UIPropertiesTimeSeries> l = rootContext.findAll(UIPropertiesTimeSeries.class).getAllMatches();
        List<UiTimeSeriesConfig> configs = new ArrayList<UiTimeSeriesConfig>();
        for ( UIPropertiesTimeSeries i : l ) {
            configs.add(i.getConfig());
        }
        return configs;
    }

    public void addChartConfigs(List<UiTimeSeriesConfig> chartConfigs) {
        rootContext.addChartConfigs(chartConfigs);
    }

    private void addSeriesSelectionListener() {
        seriesSelectionPanel.addSelectionListener(new TimeSeriesSelectorListener<ChartingTimeSeries>() {

            public void selectionChanged(List<ChartingTimeSeries> newSelection) {
                chart.setSeries(newSelection);
            }
        });
    }

    public void addTimeSeries(List<UIPropertiesTimeSeries> selectedSeries) {
        IdentifiableListActionModel identifiables = new IdentifiableListActionModel(selectedSeries);
        rootContext.doImport(this, identifiables, rootContext);
    }

    //when this visualizer is garbage collected, give its series the chance to
    //dispose themselves / disable any subscriptions
    public void finalize() throws Throwable {
        rootContext.dispose();
        super.finalize();
    }

}
