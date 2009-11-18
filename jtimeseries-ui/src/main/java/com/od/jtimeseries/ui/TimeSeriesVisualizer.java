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
package com.od.jtimeseries.ui;

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.RemoteServerDictionary;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.ui.displaypattern.DisplayNamePattern;
import com.od.jtimeseries.ui.displaypattern.DisplayPatternDialog;
import com.od.jtimeseries.ui.displaypattern.EditDisplayNamePatternsAction;
import com.od.jtimeseries.ui.download.ShowDownloadSeriesDialogAction;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.selector.TimeSeriesSelectorListener;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeriesConfig;
import com.od.jtimeseries.util.logging.LogDefaults;
import com.od.jtimeseries.util.logging.LogMethods;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 07-Jan-2009
 * Time: 15:58:18
 */
public class TimeSeriesVisualizer extends JPanel {

    private JToolBar toolbar;
    private TimeSeriesChart contextTimeSeriesChart;
    private SeriesSelectionPanel seriesSelectionPanel;
    private TimeSeriesContext rootContext = JTimeSeries.createRootContext();
    private RemoteServerDictionary remoteServerDictionary;
    private LogMethods logMethods = LogDefaults.getDefaultLogMethods(TimeSeriesVisualizer.class);
    private EditDisplayNamePatternsAction editDisplayNameAction;
    private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private final ChartControlPanel chartControlPanel = new ChartControlPanel();

    public TimeSeriesVisualizer(String title, RemoteServerDictionary remoteServerDictionary) {
        this.remoteServerDictionary = remoteServerDictionary;
        contextTimeSeriesChart = new TimeSeriesChart(title);
        seriesSelectionPanel = new SeriesSelectionPanel(rootContext);

        createDisplayNameAction();
        createToolbar();
        JPanel chartPanel = createChartPanel();

        splitPane.setLeftComponent(seriesSelectionPanel);
        splitPane.setRightComponent(chartPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.5d);
        splitPane.setDividerSize(splitPane.getDividerSize() + 2);

        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        addSeriesSelectionListener();
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
        contextTimeSeriesChart.setTitle(title);
    }

    public String getChartsTitle() {
        return contextTimeSeriesChart.getTitle();
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

    public boolean isMultipleRangeChart() {
        return contextTimeSeriesChart.isMultipleRange();
    }

    public void setMultipleRangeChart(boolean multipleRange) {
        contextTimeSeriesChart.setMultipleRange(multipleRange);
        chartControlPanel.setMultipleRange(multipleRange);
    }

    public int getDividerLocation() {
        return splitPane.getDividerLocation();
    }

    public void setDividerLocation(int location) {
        splitPane.setDividerLocation(location);
    }

    public VisualizerConfiguration getConfiguration() {
        return new VisualizerConfiguration(
                getChartsTitle(),
                getDisplayNamePatterns(),
                isTableSelectorVisible(),
                getChartConfigs(),
                isMultipleRangeChart(),
                getDividerLocation()
        );
    }

    public void setConfiguration(VisualizerConfiguration c) {
        setChartsTitle(c.getChartsTitle());
        setDisplayNamePatterns(c.getDisplayNamePatterns());
        setTableSelectorVisible(c.isTableSelectorVisible());
        addChartConfigs(c.getChartConfigs());
        setMultipleRangeChart(c.isMultipleRangeChart());
        setDividerLocation(c.getDividorLocation());
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
                    remoteServerDictionary,
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
        chartPanel.add(contextTimeSeriesChart, BorderLayout.CENTER);
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
            TimeSeriesContext context = rootContext.getOrCreateContextForPath(c.getParentPath());
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
                contextTimeSeriesChart.setSeries(newSelection);
            }
        });
    }

    private class ChartControlPanel extends JPanel {

        JCheckBox useMultipleRangeCheckbox = new JCheckBox("Use Multiple Ranges", true);

        public ChartControlPanel() {
            setLayout(new BorderLayout());
            Box b = Box.createHorizontalBox();
            b.add(Box.createHorizontalGlue());
            b.add(useMultipleRangeCheckbox);
            add(b, BorderLayout.CENTER);

            useMultipleRangeCheckbox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    contextTimeSeriesChart.setMultipleRange(useMultipleRangeCheckbox.isSelected());
                }
            });
        }


        public void setMultipleRange(boolean multipleRange) {
            this.useMultipleRangeCheckbox.setSelected(multipleRange);
        }
    }

}
