package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.config.ConfigAware;
import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.net.udp.UiTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.eventbus.EventSender;
import com.od.swing.eventbus.UIEventBus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 08/04/11
 * Time: 17:02
 */
public abstract class AbstractDesktopFrame extends JFrame {

    private TimeSeriousDesktopPane desktopPane;
    private DesktopContext desktopContext;
    private TimeSeriousRootContext rootContext;
    private ApplicationActionModels actionModels;
    private JToolBar mainToolBar = new JToolBar();

    public AbstractDesktopFrame(UiTimeSeriesServerDictionary serverDictionary, DisplayNameCalculator displayNameCalculator, DesktopContext desktopContext, SeriesSelectionPanel selectionPanel, TimeSeriousRootContext rootContext, ApplicationActionModels actionModels) {
        this.desktopContext = desktopContext;
        this.rootContext = rootContext;
        this.actionModels = actionModels;
        this.desktopPane = new TimeSeriousDesktopPane(this, serverDictionary, displayNameCalculator, selectionPanel, desktopContext);
        getContentPane().add(desktopPane, BorderLayout.CENTER);
        addWindowListener();
        configureFrame();
    }

    private void configureFrame() {
        setIconImage(ImageUtils.FRAME_ICON_16x16.getImage());
    }

    protected TimeSeriousDesktopPane getDesktopPane() {
        return desktopPane;
    }

    protected void layoutFrame() {
        Component c = getMainComponent();
        getContentPane().add(c, BorderLayout.CENTER);
        getContentPane().add(mainToolBar, BorderLayout.NORTH);
    }

    protected JToolBar getToolBar() {
        return mainToolBar;
    }

    protected abstract Component getMainComponent();

    protected TimeSeriousRootContext getRootContext() {
        return rootContext;
    }

    protected DesktopContext getDesktopContext() {
        return desktopContext;
    }

    protected ApplicationActionModels getActionModels() {
        return actionModels;
    }

    private void addWindowListener() {
        addWindowFocusListener(new DesktopSelectionWindowFocusListener());
    }

    //set the selected desktop in the desktopSelectionActionModel when this window is focused
    private class DesktopSelectionWindowFocusListener implements WindowFocusListener {

        public void windowGainedFocus(WindowEvent e) {
            UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
                new EventSender<TimeSeriousBusListener>() {
                    public void sendEvent(TimeSeriousBusListener listener) {
                        listener.desktopSelected(desktopPane);
                    }
                }
            );
        }

        public void windowLostFocus(WindowEvent e) {
        }
    }

    public void setConfiguration(DesktopContext c) {
        Rectangle frameLocation = c.getFrameLocation();
        if ( frameLocation != null) {
            setBounds(frameLocation);
            setExtendedState(c.getFrameExtendedState());
        } else {
            setSize(800, 600);
            setLocationRelativeTo(null);
        }
        desktopPane.setConfiguration(c);
        setVisible(c.isShown());
    }
}
