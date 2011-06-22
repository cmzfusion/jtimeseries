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
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.PeerDesktop;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;
import com.od.jtimeseries.ui.timeserious.rootcontext.TimeSeriousRootContext;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.eventbus.EventSender;
import com.od.swing.eventbus.UIEventBus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 08/04/11
 * Time: 17:02
 */
public abstract class AbstractDesktopFrame extends JFrame implements PeerDesktop {

    private TimeSeriousDesktopPane desktopPane;
    private DesktopContext desktopContext;
    private TimeSeriousRootContext rootContext;
    private ApplicationActionModels actionModels;
    private JToolBar mainToolBar = new JToolBar();

    public AbstractDesktopFrame(TimeSeriesServerDictionary serverDictionary, DisplayNameCalculator displayNameCalculator, DesktopContext desktopContext, SeriesSelectionPanel selectionPanel, TimeSeriousRootContext rootContext, ApplicationActionModels actionModels) {
        this.desktopContext = desktopContext;
        this.rootContext = rootContext;
        this.actionModels = actionModels;
        this.desktopPane = new TimeSeriousDesktopPane(this, serverDictionary, displayNameCalculator, selectionPanel, desktopContext, rootContext, actionModels);
        setIconImage(ImageUtils.FRAME_ICON_16x16.getImage());
        getContentPane().add(desktopPane, BorderLayout.CENTER);
        setConfiguration(desktopContext);
        desktopContext.setPeerResource(this);
        addWindowFocusListener(new DesktopSelectionWindowFocusListener());
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

    //set the selected desktop in the desktopSelectionActionModel when this window is focused
    private class DesktopSelectionWindowFocusListener implements WindowFocusListener {

        public void windowGainedFocus(WindowEvent e) {
            UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
                new EventSender<TimeSeriousBusListener>() {
                    public void sendEvent(TimeSeriousBusListener listener) {
                        listener.desktopSelected(getDesktopContext());
                    }
                }
            );
        }

        public void windowLostFocus(WindowEvent e) {
        }
    }

    private void setConfiguration(DesktopContext c) {
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
