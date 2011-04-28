package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.config.ConfigAware;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.displaypattern.EditDisplayNamePatternsAction;
import com.od.jtimeseries.ui.net.udp.UiTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;
import com.od.jtimeseries.ui.timeserious.action.NewDesktopAction;
import com.od.jtimeseries.ui.timeserious.action.NewServerAction;
import com.od.jtimeseries.ui.timeserious.action.NewVisualizerAction;
import com.od.jtimeseries.ui.util.ImageUtils;

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
    private DisplayNameCalculator displayNameCalculator;
    private UiTimeSeriesServerDictionary serverDictionary;
    private ExitAction exitAction;
    private final JSplitPane splitPane = new JSplitPane();
    private int tableSplitPanePosition;
    private int treeSplitPanePosition;

    public TimeSeriousMainFrame(UiTimeSeriesServerDictionary serverDictionary, ApplicationActionModels actionModels, ExitAction exitAction, DisplayNameCalculator displayNameCalculator, TimeSeriousRootContext rootContext, MainSeriesSelector mainSeriesSelector, DesktopContext desktopContext) {
        super(serverDictionary, displayNameCalculator, desktopContext, mainSeriesSelector.getSelectionPanel(), rootContext, actionModels);
        this.serverDictionary = serverDictionary;
        this.exitAction = exitAction;
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
                if (!exitAction.confirmAndSaveConfig(e.getWindow())) {
                    //there's no mechanism to cancel the close which I can find, barring throwing an exception
                    //which is then handled by some dedicated logic in the Component class
                    throw new RuntimeException("User cancelled exit");
                }
            }
        });
    }

    private void createActions() {
        newVisualizerAction = new NewVisualizerAction(this, getActionModels().getDesktopSelectionActionModel());
        newServerAction = new NewServerAction(this, serverDictionary);
        editDisplayNamePatternsAction = new EditDisplayNamePatternsAction(
            TimeSeriousMainFrame.this,
            displayNameCalculator
        );
        newDesktopAction = new NewDesktopAction(this, getRootContext());
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
        getToolBar().add(newDesktopAction);
        getToolBar().add(newVisualizerAction);
        getToolBar().add(newServerAction);
        getToolBar().add(editDisplayNamePatternsAction);
    }

    private void initializeFrame() {
        setTitle("TimeSerious");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(splitPane.getDividerSize() + 4);
    }

    private void createMenuBar() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setOpaque(false);
        mainMenuBar.add(fileMenu);

        JMenuItem newServerItem = new JMenuItem(newServerAction);
        fileMenu.add(newServerItem);

        JMenuItem exitItem = new JMenuItem(exitAction);
        fileMenu.add(exitItem);

        JMenu windowMenu = new JMenu("Window");
        JMenuItem newVisualizerItem = new JMenuItem(newVisualizerAction);
        windowMenu.add(newVisualizerItem);

        windowMenu.setOpaque(false);
        mainMenuBar.add(windowMenu);
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

    public void prepareConfigForSave(TimeSeriousConfig config) {
        config.setSplitPaneLocationWhenTreeSelected(treeSplitPanePosition);
        config.setSplitPaneLocationWhenTableSelected(tableSplitPanePosition);
    }

}
