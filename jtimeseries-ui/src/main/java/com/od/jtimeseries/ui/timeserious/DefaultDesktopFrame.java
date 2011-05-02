package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.net.udp.UiTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;
import com.od.jtimeseries.ui.timeserious.action.NewVisualizerAction;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListener;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListenerAdapter;
import com.od.swing.eventbus.EventSender;
import com.od.swing.eventbus.UIEventBus;
import com.od.swing.util.AwtSafeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
        initializeFrame();
        addListeners();
    }

    private void addListeners() {
        getDesktopContext().addTreeListener(
            AwtSafeListener.getAwtSafeListener(
                new FrameDisposingContextListener(),
                IdentifiableTreeListener.class
            )
        );
        addWindowListener(new DesktopWindowListener());
    }

    private class DesktopWindowListener extends WindowAdapter {

        public void windowClosing(WindowEvent e) {
            UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
                new EventSender<TimeSeriousBusListener>() {
                    public void sendEvent(TimeSeriousBusListener listener) {
                        listener.desktopDisposed(getDesktopContext());
                    }
                }
            );
            getDesktopContext().setShown(false);
        }
    }

    private void initializeFrame() {
        setTitle("TimeSerious " + getDesktopContext().getId());
    }

    private void createActions() {
        newVisualizerAction = new NewVisualizerAction(
            this,
            getActionModels().getDesktopSelectionActionModel(),
            getActionModels().getVisualizerSelectionActionModel()
        );
    }

    private void createToolBar() {
        getToolBar().add(newVisualizerAction);
    }

    protected Component getMainComponent() {
        return getDesktopPane();
    }

    //monitor the shown status to dispose the frame if this changes to false
    //this is only done for default frame, since it should never be possible for the user to
    //close the main frame
    private class FrameDisposingContextListener extends IdentifiableTreeListenerAdapter {
        public void nodeChanged(Identifiable node, Object changeDescription) {
            if (HideablePeerContext.SHOWN_PROPERTY.equals(changeDescription)) {
                if (!getDesktopContext().isShown()) {
                    dispose();
                }
            }
        }
    }
}
