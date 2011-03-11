package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.visualizer.TimeSeriesVisualizer;
import com.od.jtimeseries.ui.util.ImageUtils;
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
public class VisualizerInternalFrame extends JInternalFrame {

    private TimeSeriesVisualizer visualizer;

    public VisualizerInternalFrame(TimeSeriesVisualizer visualizer) {
        super(visualizer.getChartsTitle(), true, true, true, true);
        this.visualizer = visualizer;
        visualizer.setToolbarVisible(false);
        setFrameIcon(ImageUtils.FRAME_ICON_16x16);
        getContentPane().add(visualizer);
        setSize(800,600);

        addInternalFrameListener(new InternalFrameAdapter() {

            public void internalFrameOpened(InternalFrameEvent e) {
                UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
                new EventSender<TimeSeriousBusListener>() {
                    public void sendEvent(TimeSeriousBusListener listener) {
                        listener.visualizerFrameDisplayed(VisualizerInternalFrame.this);
                    }
                } );
            }

            public void internalFrameActivated(InternalFrameEvent e) {
                UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
                new EventSender<TimeSeriousBusListener>() {
                    public void sendEvent(TimeSeriousBusListener listener) {
                        listener.visualizerSelected(VisualizerInternalFrame.this);
                    }
                } );
            }

            public void internalFrameClosed(InternalFrameEvent e) {
                UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
                new EventSender<TimeSeriousBusListener>() {
                    public void sendEvent(TimeSeriousBusListener listener) {
                        listener.visualizerFrameDisposed(VisualizerInternalFrame.this);
                    }
                } );
            }
        });

    }

    public TimeSeriesVisualizer getVisualizer() {
        return visualizer;
    }
}
