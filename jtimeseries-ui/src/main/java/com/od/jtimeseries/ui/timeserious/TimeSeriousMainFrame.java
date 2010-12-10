package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.timeserious.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 15:14:34
 */
public class TimeSeriousMainFrame extends JFrame {

    public static final String MAIN_FRAME_NAME = "mainFrame";

    private JMenuBar mainMenuBar = new JMenuBar();
    private DesktopPanel desktopPanel = new DesktopPanel();
    private MainSeriesTreePanel seriesTreePanel = new MainSeriesTreePanel();
    private JToolBar mainToolBar = new JToolBar();
    private DesktopSelectionActionModel desktopSelectionActionModel;
    private NewVisualizerAction newVisualizerAction;

    public TimeSeriousMainFrame(ApplicationActionModels actionModels) {
        createActions(actionModels);
        initializeFrame();
        createMenuBar();
        createToolBar();
        layoutFrame();
        addListeners();
    }

    private void createActions(ApplicationActionModels actionModels) {
        desktopSelectionActionModel = actionModels.getDesktopSelectionActionModel();
        newVisualizerAction = new NewVisualizerAction(desktopSelectionActionModel);
    }

    private void addListeners() {
        addWindowFocusListener(new DesktopSelectionWindowFocusListener());
    }

    private void layoutFrame() {
        setJMenuBar(mainMenuBar);
        JSplitPane splitPane = new JSplitPane();
        splitPane.setLeftComponent(seriesTreePanel);
        splitPane.setRightComponent(desktopPanel);
        getContentPane().add(splitPane, BorderLayout.CENTER);
        add(mainToolBar, BorderLayout.NORTH);
    }

    private void createToolBar() {
        mainToolBar.add(newVisualizerAction);
    }

    private void initializeFrame() {
        setTitle("TimeSerious");
        setIconImage(ImageUtils.SERIES_ICON_16x16.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void createMenuBar() {
        JMenu fileMenu = new JMenu("File");
        mainMenuBar.add(fileMenu);

        JMenuItem exitItem = new JMenuItem(new ExitAction());
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
        desktopPanel.restoreConfig(config);
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
        config.setFrameLocation(MAIN_FRAME_NAME, getBounds());
        config.setFrameExtendedState(MAIN_FRAME_NAME, getExtendedState());
        desktopPanel.prepareConfigForSave(config);
    }

    public DesktopPanel getSelectedDesktop() {
        return desktopPanel;
    }

    private class ExitAction extends AbstractAction {

        private ExitAction() {
            super("Exit");
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    //set the selected desktop in the desktopSelectionActionModel when this window is focused
    private class DesktopSelectionWindowFocusListener implements WindowFocusListener {

        public void windowGainedFocus(WindowEvent e) {
            desktopSelectionActionModel.setDesktop(desktopPanel);
        }

        public void windowLostFocus(WindowEvent e) {
        }
    }
}
