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

import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.identifiable.IdentifiableTreeListener;
import com.od.jtimeseries.identifiable.IdentifiableTreeListenerAdapter;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.HidablePeerContext;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;
import com.od.jtimeseries.ui.timeserious.rootcontext.TimeSeriousRootContext;
import com.od.swing.eventbus.EventSender;
import com.od.swing.eventbus.UIEventBus;
import com.od.swing.util.AwtSafeListener;
import com.od.swing.weakreferencelistener.WeakReferenceListener;

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

    private IdentifiableTreeListener frameDisposingDesktopContextListener;

    public DefaultDesktopFrame(TimeSeriesServerDictionary serverDictionary, DisplayNameCalculator displayNameCalculator, DesktopContext desktopContext, SeriesSelectionPanel selectionPanel,
                               TimeSeriousRootContext rootContext, ApplicationActionModels actionModels) {
        super(serverDictionary, displayNameCalculator, desktopContext, selectionPanel, rootContext, actionModels);
        createToolBar();
        layoutFrame();
        initializeFrame();
        addListeners();
    }

    private void addListeners() {
        frameDisposingDesktopContextListener = AwtSafeListener.getAwtSafeListener(
                new FrameDisposingContextListener(),
                IdentifiableTreeListener.class
        );
        WeakReferenceListener l = new WeakReferenceListener(frameDisposingDesktopContextListener);
        l.addListenerTo(getDesktopContext());
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
            disposeVisualizerResources();
        }

        //for each shown visualizer, we need it to release its
        //peer visualizer frame, and convert it to config when desktop
        //is hidden
        private void disposeVisualizerResources() {
            for ( VisualizerContext v : getDesktopContext().findAll(VisualizerContext.class).getAllMatches() ) {
                if ( v.isShown()) {
                    v.disposePeerWhenParentHidden();
                }
            }
        }
    }

    private void initializeFrame() {
        setTitle("TimeSerious " + getDesktopContext().getId());
    }

    private void createToolBar() {
        JToolBar toolBar = getToolBar();
        toolBar.add(getNewVisualizerAction());
        addSeparator(toolBar);
        toolBar.add(getTileVisualizersAction());
        toolBar.add(getCascadeVisualizersAction());
    }

    protected Component getMainComponent() {
        return getDesktopPane();
    }

    //monitor the shown status to dispose the frame if this changes to false
    //this is only done for default frame, since it should never be possible for the user to
    //close the main frame
    private class FrameDisposingContextListener extends IdentifiableTreeListenerAdapter {
        public void nodeChanged(Identifiable node, Object changeDescription) {
            if (HidablePeerContext.SHOWN_PROPERTY.equals(changeDescription)) {
                if (!getDesktopContext().isShown()) {
                    dispose();
                }
            }
        }
    }
}
