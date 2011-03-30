package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.ui.visualizer.TimeSeriesVisualizer;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListenerAdapter;
import com.od.swing.eventbus.EventSender;
import com.od.swing.eventbus.UIEventBus;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 * Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 26-Mar-2010
* Time: 17:59:22
*/
public class VisualizerInternalFrame extends JInternalFrame implements VInternalFrame {

    private TimeSeriesVisualizer visualizer;
    private JDesktopPane desktopPane;

    public VisualizerInternalFrame(final TimeSeriesVisualizer visualizer, JDesktopPane desktopPane, final VisualizerNode visualizerNode) {
        super(visualizer.getChartsTitle(), true, true, true, true);
        this.visualizer = visualizer;
        this.desktopPane = desktopPane;
        visualizer.setToolbarVisible(false);
        setFrameIcon(ImageUtils.FRAME_ICON_16x16);
        getContentPane().add(visualizer);
        setSize(800, 600);

        addInternalFrameListener(new InternalFrameAdapter() {

            public void internalFrameOpened(InternalFrameEvent e) {
                visualizerNode.setVisualizerFrame(VisualizerInternalFrame.this);
            }

            public void internalFrameActivated(InternalFrameEvent e) {
                UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
                        new EventSender<TimeSeriousBusListener>() {
                            public void sendEvent(TimeSeriousBusListener listener) {
                                listener.visualizerSelected(VisualizerInternalFrame.this);
                            }
                        });
            }

            public void internalFrameClosed(InternalFrameEvent e) {
                UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
                    new EventSender<TimeSeriousBusListener>() {
                        public void sendEvent(TimeSeriousBusListener listener) {
                            listener.visualizerFrameDisposed(VisualizerInternalFrame.this);
                        }
                    });
                visualizerNode.setShown(false);
            }
        });

        //the visualizerNode is essentially the model for this frame view
        //although the user can hide the visualizer by closing the frame, we also have to monitor the shown state
        //of the node, and close the frame if it changes
        visualizerNode.addTreeListener(new IdentifiableTreeListenerAdapter() {
             public void nodeChanged(Identifiable node, Object changeDescription) {
                if ( "shown".equals(changeDescription)) {
                    if ( ! visualizerNode.isShown()) {
                        dispose();
                    }
                }
             }
        });
    }

    public TimeSeriesVisualizer getVisualizer() {
        return visualizer;
    }

    public int getZPosition() {
        return desktopPane.getPosition(this);
    }
}
