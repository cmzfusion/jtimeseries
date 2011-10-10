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

import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.identifiable.HidablePeerContext;
import com.od.jtimeseries.ui.identifiable.PeerVisualizerFrame;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.ui.visualizer.TimeSeriesVisualizer;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListener;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListenerAdapter;
import com.od.swing.eventbus.EventSender;
import com.od.swing.eventbus.UIEventBus;
import com.od.swing.util.AwtSafeListener;
import com.od.swing.weakreferencelistener.WeakReferenceListener;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 26-Mar-2010
* Time: 17:59:22
*/
public class VisualizerInternalFrame extends JInternalFrame implements PeerVisualizerFrame {

    private TimeSeriesVisualizer visualizer;
    private JDesktopPane desktopPane;
    private VisualizerContext visualizerContext;
    private IdentifiableTreeListener frameDisposingContextTreeListener;

    public VisualizerInternalFrame(final TimeSeriesVisualizer visualizer, final JDesktopPane desktopPane, final VisualizerContext visualizerContext) {
        super(visualizer.getChartsTitle(), true, true, true, true);
        this.visualizer = visualizer;
        this.desktopPane = desktopPane;
        this.visualizerContext = visualizerContext;
        visualizer.setToolbarVisible(false);
        setFrameIcon(ImageUtils.FRAME_ICON_16x16);
        getContentPane().add(visualizer);
        setSize(VisualizerConfiguration.DEFAULT_WIDTH, VisualizerConfiguration.DEFAULT_HEIGHT);

        addInternalFrameListener(new InternalFrameAdapter() {

            public void internalFrameOpened(InternalFrameEvent e) {
                visualizerContext.setPeerResource(VisualizerInternalFrame.this);
            }

            public void internalFrameActivated(InternalFrameEvent e) {
                UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
                        new EventSender<TimeSeriousBusListener>() {
                            public void sendEvent(TimeSeriousBusListener listener) {
                                listener.visualizerSelected(visualizerContext);
                            }
                        }
                );
            }

            public void internalFrameClosed(InternalFrameEvent e) {
                UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
                    new EventSender<TimeSeriousBusListener>() {
                        public void sendEvent(TimeSeriousBusListener listener) {
                            listener.visualizerFrameDisposed(visualizerContext);
                        }
                    }
                );
                visualizerContext.setShown(false);

                //workaround for bug where jDesktopPane holds on to internal frame references in its framesCache, after the internal frame is closed
                //http://stackoverflow.com/questions/4517931/java-swing-jtree-is-not-garbage-collected
                desktopPane.selectFrame(true);
            }
        });

        //the visualizerContext is essentially the model for this frame view
        //although the user can hide the visualizer by closing the frame, we also have to monitor the shown state
        //of the node, and close the frame if it changes
        frameDisposingContextTreeListener = AwtSafeListener.getAwtSafeListener(
                new FrameDisposingContextListener(),
                IdentifiableTreeListener.class
        );
        WeakReferenceListener w = new WeakReferenceListener(frameDisposingContextTreeListener);
        w.addListenerTo(visualizerContext);
    }

    public VisualizerConfiguration getVisualizerConfiguration() {
        return TimeSeriesVisualizer.createVisualizerConfiguration(visualizer);
    }

    public int getZPosition() {
        return desktopPane.getPosition(this);
    }

    public void setConfiguration(VisualizerConfiguration c) {
        if ( c != null) {
            TimeSeriesVisualizer.setVisualizerConfiguration(visualizer, c);
            if ( c.getFrameLocation() != null) {
                setBounds(c.getFrameLocation());
            }
        }
    }

    public void addTimeSeries(List<UIPropertiesTimeSeries> selectedSeries) {
        visualizer.addTimeSeries(selectedSeries);
    }

    private class FrameDisposingContextListener extends IdentifiableTreeListenerAdapter {

        public void nodeChanged(Identifiable node, Object changeDescription) {
            if (HidablePeerContext.SHOWN_PROPERTY.equals(changeDescription)) {
                if (! visualizerContext.isShown()) {
                    dispose();
                }
            }
        }
    }
}
