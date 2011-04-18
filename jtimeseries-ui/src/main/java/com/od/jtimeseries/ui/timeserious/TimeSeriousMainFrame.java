package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.displaypattern.EditDisplayNamePatternsAction;
import com.od.jtimeseries.ui.net.udp.UiTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.timeserious.action.*;
import com.od.jtimeseries.ui.config.ConfigAware;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 15:14:34
 */
public class TimeSeriousMainFrame extends AbstractDesktopFrame implements ConfigAware {

    private TimeSeriousRootContext rootContext;
    private JMenuBar mainMenuBar = new JMenuBar();
    private MainSeriesSelector mainSeriesSelector;
    private JToolBar mainToolBar = new JToolBar();
    private DesktopSelectionActionModel desktopSelectionActionModel;
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
        super(serverDictionary, displayNameCalculator, desktopContext, mainSeriesSelector.getSelectionPanel());
        this.serverDictionary = serverDictionary;
        this.exitAction = exitAction;
        this.displayNameCalculator = displayNameCalculator;
        this.rootContext = rootContext;
        this.mainSeriesSelector = mainSeriesSelector;
        createActions(actionModels);
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

    private void createActions(ApplicationActionModels actionModels) {
        desktopSelectionActionModel = actionModels.getDesktopSelectionActionModel();
        newVisualizerAction = new NewVisualizerAction(this, desktopSelectionActionModel, rootContext);
        newServerAction = new NewServerAction(this, serverDictionary);
        editDisplayNamePatternsAction = new EditDisplayNamePatternsAction(
            TimeSeriousMainFrame.this,
            displayNameCalculator
        );
        newDesktopAction = new NewDesktopAction(this, rootContext);
    }

    private void addListeners() {
        addWindowFocusListener(new DesktopSelectionWindowFocusListener());
        splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ( mainSeriesSelector.isTableSelectorVisible()) {
                    tableSplitPanePosition = (Integer)evt.getNewValue();
                } else {
                    treeSplitPanePosition = (Integer)evt.getNewValue();
                }
            }
        });
        addSelectorListener();
    }

    private void addSelectorListener() {
        //set the split pane position when we change between tree and table view
        mainSeriesSelector.addPropertyChangeListener(SeriesSelectionPanel.TREE_VIEW_SELECTED_PROPERTY,
        new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                splitPane.setDividerLocation(
                    ((Boolean)evt.getNewValue()) ? treeSplitPanePosition : tableSplitPanePosition
                );
            }
        });
    }

    private void layoutFrame() {
        setJMenuBar(mainMenuBar);
        splitPane.setLeftComponent(mainSeriesSelector);
        splitPane.setRightComponent(getDesktopPane());
        getContentPane().add(splitPane, BorderLayout.CENTER);
        add(mainToolBar, BorderLayout.NORTH);
    }

    private void createToolBar() {
        mainToolBar.add(newDesktopAction);
        mainToolBar.add(newVisualizerAction);
        mainToolBar.add(newServerAction);
        mainToolBar.add(editDisplayNamePatternsAction);
    }

    private void initializeFrame() {
        setTitle("TimeSerious");
        setIconImage(ImageUtils.FRAME_ICON_16x16.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(splitPane.getDividerSize() + 4);
    }

    private void createMenuBar() {
        JMenu fileMenu = new JMenu("File");
        mainMenuBar.add(fileMenu);

        JMenuItem newServerItem = new JMenuItem(newServerAction);
        fileMenu.add(newServerItem);

        JMenuItem exitItem = new JMenuItem(exitAction);
        fileMenu.add(exitItem);

        JMenu windowMenu = new JMenu("Window");
        JMenuItem newVisualizerItem = new JMenuItem(newVisualizerAction);
        windowMenu.add(newVisualizerItem);

        mainMenuBar.add(windowMenu);
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
