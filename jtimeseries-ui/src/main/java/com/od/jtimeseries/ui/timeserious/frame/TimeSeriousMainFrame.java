/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
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
package com.od.jtimeseries.ui.timeserious.frame;

import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.config.ConfigAware;
import com.od.jtimeseries.ui.config.ConfigAwareTreeManager;
import com.od.jtimeseries.ui.config.ConfigInitializer;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.displaypattern.EditDisplayNamePatternsAction;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.timeserious.action.*;
import com.od.jtimeseries.ui.timeserious.mainselector.MainSeriesSelector;
import com.od.jtimeseries.ui.timeserious.rootcontext.TimeSeriousRootContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 15:14:34
 */
public class TimeSeriousMainFrame extends AbstractDesktopFrame implements ConfigAware {

    private JMenuBar mainMenuBar = new JMenuBar();
    private MainSeriesSelector mainSeriesSelector;
    private NewVisualizerAction newVisualizerAction;
    private NewDesktopAction newDesktopAction;
    private NewServerAction newServerAction;
    private EditDisplayNamePatternsAction editDisplayNamePatternsAction;
    private ConfigAwareTreeManager configTreeManager;
    private ConfigInitializer configInitializer;
    private DisplayNameCalculator displayNameCalculator;
    private TimeSeriesServerDictionary serverDictionary;
    private JSplitPane splitPane = new JSplitPane();
    private int tableSplitPanePosition;
    private int treeSplitPanePosition;
    private ExitAction exitAction;
    private SaveAction saveAction;
    private ExportConfigAction exportConfigAction;
    private ImportConfigAction importConfigAction;
    private ShowAboutDialogAction showAboutDialogAction;
    public WindowMenu windowMenu;

    public TimeSeriousMainFrame(TimeSeriesServerDictionary serverDictionary, ApplicationActionModels actionModels, ConfigAwareTreeManager configTreeManager, ConfigInitializer configInitializer, DisplayNameCalculator displayNameCalculator, TimeSeriousRootContext rootContext, MainSeriesSelector mainSeriesSelector, DesktopContext desktopContext) {
        super(serverDictionary, displayNameCalculator, desktopContext, mainSeriesSelector.getSelectionPanel(), rootContext, actionModels);
        this.serverDictionary = serverDictionary;
        this.configTreeManager = configTreeManager;
        this.configInitializer = configInitializer;
        this.displayNameCalculator = displayNameCalculator;
        this.mainSeriesSelector = mainSeriesSelector;
        createActions();
        initializeFrame();
        createMenuBar();
        createToolBar();
        layoutFrame();
        addListeners();
        addExitListener();
    }

    private void addExitListener() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (!exitAction.confirmAndSaveConfig("Exit TimeSerious", JOptionPane.YES_NO_CANCEL_OPTION)) {
                    //there's no mechanism to cancel the close which I can find, barring throwing an exception
                    //which is then handled by some dedicated logic in the Component class
                    throw new RuntimeException("User cancelled exit");
                }
            }
        });
    }

    private void createActions() {
        exitAction = new ExitAction(this, configTreeManager, configInitializer);
        saveAction = new SaveAction(this, configTreeManager, configInitializer);
        exportConfigAction = new ExportConfigAction(this, configTreeManager, configInitializer);
        importConfigAction = new ImportConfigAction(this, getRootContext(), configInitializer);
        newVisualizerAction = new NewVisualizerAction(
            this,
            getActionModels().getDesktopSelectionActionModel(),
            getActionModels().getVisualizerSelectionActionModel()
        );
        newServerAction = new NewServerAction(this, serverDictionary);
        editDisplayNamePatternsAction = new EditDisplayNamePatternsAction(
            TimeSeriousMainFrame.this,
            displayNameCalculator
        );
        newDesktopAction = new NewDesktopAction(this, getRootContext(), getActionModels().getDesktopSelectionActionModel());
        showAboutDialogAction = new ShowAboutDialogAction(this, configInitializer.getConfigVersion());
    }

    private void addListeners() {
        addSplitLocationListener();
        addSelectorChangeListener();
    }

    private void addSplitLocationListener() {
        splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ( mainSeriesSelector.isTableSelectorVisible()) {
                    tableSplitPanePosition = (Integer)evt.getNewValue();
                } else {
                    treeSplitPanePosition = (Integer)evt.getNewValue();
                }
            }
        });
    }

    private void addSelectorChangeListener() {
        //set the split pane position when we change between tree and table view
        mainSeriesSelector.addPropertyChangeListener(SeriesSelectionPanel.TREE_VIEW_SELECTED_PROPERTY,
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        splitPane.setDividerLocation(
                                ((Boolean) evt.getNewValue()) ? treeSplitPanePosition : tableSplitPanePosition
                        );
                    }
                });
    }

    protected Component getMainComponent() {
        Component c = splitPane;
        splitPane.setLeftComponent(mainSeriesSelector);
        splitPane.setRightComponent(getDesktopPane());
        return c;
    }

    private void createToolBar() {
        JToolBar toolBar = getToolBar();
        toolBar.add(editDisplayNamePatternsAction);
        toolBar.add(newServerAction);
        toolBar.add(newDesktopAction);
        toolBar.add(getNewVisualizerAction());
        addSeparator(toolBar);
        toolBar.add(getTileVisualizersAction());
        toolBar.add(getCascadeVisualizersAction());
        toolBar.add(getToggleLegendAction());
        toolBar.add(getToggleChartTypesAction());
    }

    private void initializeFrame() {
        setTitle("TimeSerious");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(splitPane.getDividerSize() + 4);
    }

    private void createMenuBar() {
        JMenu fileMenu = new BaseMenu("File");
        mainMenuBar.add(fileMenu);

        JMenuItem newServerItem = new JMenuItem(newServerAction);
        fileMenu.add(newServerItem);

        fileMenu.add(new JMenuBar());

        JMenuItem importConfigItem = new JMenuItem(importConfigAction);
        fileMenu.add(importConfigItem);

        JMenuItem exportConfigItem = new JMenuItem(exportConfigAction);
        fileMenu.add(exportConfigItem);

        fileMenu.add(new JMenuBar());

        JMenuItem saveItem = new JMenuItem(saveAction);
        fileMenu.add(saveItem);

        JMenuItem exitItem = new JMenuItem(exitAction);
        fileMenu.add(exitItem);

        windowMenu = new WindowMenu(newDesktopAction, getRootContext());
        getRootContext().addTreeListener(windowMenu); //show new desktops added
        mainMenuBar.add(windowMenu);

        JMenu helpMenu = new BaseMenu("Help");
        JMenuItem aboutItem = new JMenuItem(showAboutDialogAction);
        helpMenu.add(aboutItem);
        mainMenuBar.add(helpMenu);

        setJMenuBar(mainMenuBar);
    }

    public void restoreConfig(TimeSeriousConfig config) {
        splitPane.setDividerLocation(mainSeriesSelector.isTableSelectorVisible() ?
            config.getSplitPaneLocationWhenTableSelected() :
            config.getSplitPaneLocationWhenTreeSelected());
        tableSplitPanePosition = config.getSplitPaneLocationWhenTableSelected();
        treeSplitPanePosition = config.getSplitPaneLocationWhenTreeSelected();
    }

    public java.util.List<ConfigAware> getConfigAwareChildren() {
        return Collections.emptyList();
    }

    public void clearConfig() {
        getRootContext().removeTreeListener(windowMenu);
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
        config.setSplitPaneLocationWhenTreeSelected(treeSplitPanePosition);
        config.setSplitPaneLocationWhenTableSelected(tableSplitPanePosition);
    }

}
