package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.displaypattern.EditDisplayNamePatternsAction;
import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.net.udp.UiTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;
import com.od.jtimeseries.ui.timeserious.action.DesktopSelectionActionModel;
import com.od.jtimeseries.ui.timeserious.action.NewServerAction;
import com.od.jtimeseries.ui.timeserious.action.NewVisualizerAction;
import com.od.jtimeseries.ui.timeserious.config.ConfigAware;
import com.od.jtimeseries.ui.timeserious.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.eventbus.EventSender;
import com.od.swing.eventbus.UIEventBus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 15:14:34
 */
public class TimeSeriousMainFrame extends JFrame implements ConfigAware {

    public static final String MAIN_FRAME_NAME = "mainFrame";

    private TimeSeriousRootContext rootContext;
    private JMenuBar mainMenuBar = new JMenuBar();
    private DesktopPanel desktopPanel;
    private MainSeriesSelector mainSeriesSelector;
    private JToolBar mainToolBar = new JToolBar();
    private DesktopSelectionActionModel desktopSelectionActionModel;
    private NewVisualizerAction newVisualizerAction;
    private NewServerAction newServerAction;
    private EditDisplayNamePatternsAction editDisplayNamePatternsAction;
    private DisplayNameCalculator displayNameCalculator = new DisplayNameCalculator();
    private UiTimeSeriesServerDictionary serverDictionary;
    private AbstractAction exitAction;
    private final JSplitPane splitPane = new JSplitPane();
    private int tableSplitPanePosition;
    private int treeSplitPanePosition;

    public TimeSeriousMainFrame(UiTimeSeriesServerDictionary serverDictionary, ApplicationActionModels actionModels, AbstractAction exitAction) {
        this.serverDictionary = serverDictionary;
        this.exitAction = exitAction;
        this.rootContext = new TimeSeriousRootContext(serverDictionary, displayNameCalculator);
        this.mainSeriesSelector = new MainSeriesSelector(
            rootContext,
            actionModels,
            serverDictionary
        );
        this.desktopPanel = new DesktopPanel(serverDictionary, displayNameCalculator, mainSeriesSelector.getSelectionPanel());
        createActions(actionModels);
        initializeFrame();
        createMenuBar();
        createToolBar();
        layoutFrame();
        addListeners();
    }

    private void createActions(ApplicationActionModels actionModels) {
        desktopSelectionActionModel = actionModels.getDesktopSelectionActionModel();
        newVisualizerAction = new NewVisualizerAction(this, desktopSelectionActionModel, rootContext);
        newServerAction = new NewServerAction(this, serverDictionary);
        editDisplayNamePatternsAction = new EditDisplayNamePatternsAction(
            TimeSeriousMainFrame.this,
            displayNameCalculator
        );
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
    }

    private void addSplitPaneListener() {
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
        splitPane.setRightComponent(desktopPanel);
        getContentPane().add(splitPane, BorderLayout.CENTER);
        add(mainToolBar, BorderLayout.NORTH);
    }

    private void createToolBar() {
        mainToolBar.add(newVisualizerAction);
        mainToolBar.add(newServerAction);
        mainToolBar.add(editDisplayNamePatternsAction);
    }

    private void initializeFrame() {
        setTitle("TimeSerious");
        setIconImage(ImageUtils.FRAME_ICON_16x16.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        Rectangle frameLocation = config.getFrameLocation(MAIN_FRAME_NAME);
        if ( frameLocation != null) {
            setBounds(frameLocation);
            setExtendedState(config.getFrameExtendedState(MAIN_FRAME_NAME));
        } else {
            setSize(800, 600);
            setLocationRelativeTo(null);
        }
        splitPane.setDividerLocation(mainSeriesSelector.isTableSelectorVisible() ?
            config.getSplitPaneLocationWhenTableSelected() :
            config.getSplitPaneLocationWhenTreeSelected());
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(splitPane.getDividerSize() + 4);
        tableSplitPanePosition = config.getSplitPaneLocationWhenTableSelected();
        treeSplitPanePosition = config.getSplitPaneLocationWhenTreeSelected();
        addSplitPaneListener();
    }

    public java.util.List<ConfigAware> getConfigAwareChildren() {
        return Arrays.asList(rootContext, mainSeriesSelector, desktopPanel);
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
        config.setFrameLocation(MAIN_FRAME_NAME, getBounds());
        config.setFrameExtendedState(MAIN_FRAME_NAME, getExtendedState());
        config.setSplitPaneLocationWhenTreeSelected(treeSplitPanePosition);
        config.setSplitPaneLocationWhenTableSelected(tableSplitPanePosition);
    }

    //set the selected desktop in the desktopSelectionActionModel when this window is focused
    private class DesktopSelectionWindowFocusListener implements WindowFocusListener {

        public void windowGainedFocus(WindowEvent e) {
            UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
                new EventSender<TimeSeriousBusListener>() {
                    public void sendEvent(TimeSeriousBusListener listener) {
                        listener.desktopSelected(desktopPanel);
                    }
                }
            );
        }

        public void windowLostFocus(WindowEvent e) {
        }
    }
}
