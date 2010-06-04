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

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.ui.visualizer.displaypattern.DisplayNamePattern;
import com.od.jtimeseries.ui.visualizer.displaypattern.DisplayPatternDialog;
import com.od.jtimeseries.ui.visualizer.displaypattern.EditDisplayNamePatternsAction;
import com.od.jtimeseries.ui.visualizer.download.ShowDownloadSeriesDialogAction;
import com.od.jtimeseries.ui.visualizer.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.visualizer.selector.TimeSeriesSelectorListener;
import com.od.jtimeseries.ui.visualizer.selector.table.ColumnSettings;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeriesConfig;
import com.od.jtimeseries.ui.util.JideInitialization;
import com.od.jtimeseries.ui.visualizer.chart.ChartControlPanel;
import com.od.jtimeseries.ui.visualizer.chart.TimeSeriesChart;
import com.od.jtimeseries.ui.visualizer.chart.ChartRangeMode;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 07-Jan-2009
 * Time: 15:58:18
 *
 * The TimeSeriesVisualizer is designed to be a standalone component that can be integrated
 * into third party applications. It allows time series to be selected and downloaded/synchronized
 * from time series server(s), and charts plotted of selected series. The time series server(s)
 * provide access to time series data via http, and  may be remote or local  applications,
 * or even a time series service provided by the client application itself.
 */
public class TimeSeriesVisualizer extends JPanel {

    private static final LogMethods logMethods = LogUtils.getLogMethods(TimeSeriesVisualizer.class);

    private JToolBar toolbar;
    private TimeSeriesChart chart;
    private SeriesSelectionPanel seriesSelectionPanel;
    private TimeSeriesContext rootContext = JTimeSeries.createRootContext();
    private TimeSeriesServerDictionary timeSeriesServerDictionary;
    private EditDisplayNamePatternsAction editDisplayNameAction;
    private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private final ChartControlPanel chartControlPanel;

    public TimeSeriesVisualizer(String title, TimeSeriesServerDictionary timeSeriesServerDictionary) {
        JideInitialization.applyLicense();
        JideInitialization.setupJide();

        this.timeSeriesServerDictionary = timeSeriesServerDictionary;
        chart = new TimeSeriesChart(title);
        chartControlPanel = new ChartControlPanel(chart);
        createDisplayNameAction();
        JPanel chartPanel = createChartPanel();
        seriesSelectionPanel = new SeriesSelectionPanel(rootContext, "Chart");
        createToolbar();
        createSplitPane(chartPanel);
        layoutVisualizer();
        addSeriesSelectionListener();
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
        splitPane.setDividerSize(splitPane.getDividerSize() + 2);
        setDividerLocation(300);
    }

    private void createDisplayNameAction() {
        editDisplayNameAction = new EditDisplayNamePatternsAction(rootContext, this);

        editDisplayNameAction.addDisplayPatternListener(new DisplayPatternDialog.DisplayPatternListener() {
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

    public List<DisplayNamePattern> getDisplayNamePatterns() {
        return editDisplayNameAction.getDisplayNamePatterns();
    }

    public void setDisplayNamePatterns(List<DisplayNamePattern> patterns) {
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
        seriesSelectionPanel.setColumns(columnSettings);
    }

    public static void setStartOfDayOffsetMinutes(int mins) {
        RemoteChartingTimeSeries.setStartOfDayOffsetMinutes(mins);
    }

    private void createToolbar() {
        toolbar = new JToolBar();
        toolbar.add(new JButton(
            new ShowDownloadSeriesDialogAction(
                    seriesSelectionPanel,
                    rootContext,
                    timeSeriesServerDictionary,
                    editDisplayNameAction.getDisplayNameCalculator(),
                    this
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

    public SeriesSelectionPanel getSeriesSelectionPanel() {
        return seriesSelectionPanel;
    }

    public List<RemoteChartingTimeSeriesConfig> getChartConfigs() {
        List<IdentifiableTimeSeries> l = rootContext.findAllTimeSeries().getAllMatches();
        List<RemoteChartingTimeSeriesConfig> configs = new ArrayList<RemoteChartingTimeSeriesConfig>();
        for ( IdentifiableTimeSeries i : l ) {
            configs.add(((RemoteChartingTimeSeries)i).getConfig());
        }
        return configs;
    }

    public void addChartConfigs(List<RemoteChartingTimeSeriesConfig> chartConfigs) {
        addChartsFromConfigs(chartConfigs);
        seriesSelectionPanel.refresh();
    }

    private void addChartsFromConfigs(List<RemoteChartingTimeSeriesConfig> configs) {
        for ( RemoteChartingTimeSeriesConfig c : configs) {
            TimeSeriesContext context = rootContext.createContextForPath(c.getParentPath());
            try {
                context.addChild(new RemoteChartingTimeSeries(c));
            } catch (MalformedURLException e) {
                logMethods.logError("Failed to reload time series with URL " + c.getTimeSeriesUrl() + " - bad URL", e);
            }
        }
    }

    private void addSeriesSelectionListener() {
        seriesSelectionPanel.addSelectionListener(new TimeSeriesSelectorListener() {

            public void selectionChanged(List<RemoteChartingTimeSeries> newSelection) {
                chart.setSeries(newSelection);
            }
        });
    }

}
