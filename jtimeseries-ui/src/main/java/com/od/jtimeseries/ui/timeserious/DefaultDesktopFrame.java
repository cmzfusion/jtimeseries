package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.net.udp.UiTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;
import com.od.jtimeseries.ui.timeserious.action.NewVisualizerAction;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11/04/11
 * Time: 06:55
 */
public class DefaultDesktopFrame extends AbstractDesktopFrame {

    private Action newVisualizerAction;

    public DefaultDesktopFrame(UiTimeSeriesServerDictionary serverDictionary, DisplayNameCalculator displayNameCalculator, DesktopContext desktopContext, SeriesSelectionPanel selectionPanel,
                               TimeSeriousRootContext rootContext, ApplicationActionModels actionModels) {
        super(serverDictionary, displayNameCalculator, desktopContext, selectionPanel, rootContext, actionModels);
        createActions();
        createToolBar();
        layoutFrame();
    }

    private void createActions() {
        newVisualizerAction = new NewVisualizerAction(this, getActionModels().getDesktopSelectionActionModel());
    }

     private void createToolBar() {
        getToolBar().add(newVisualizerAction);
    }

    protected Component getMainComponent() {
        return getDesktopPane();
    }
}
