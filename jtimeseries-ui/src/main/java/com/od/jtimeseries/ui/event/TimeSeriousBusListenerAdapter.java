package com.od.jtimeseries.ui.event;

import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 12-Dec-2010
 * Time: 16:21:47
 *
 */
public class TimeSeriousBusListenerAdapter implements TimeSeriousBusListener {

    public void serverAdded(TimeSeriesServer s) {
    }

    public void serverRemoved(TimeSeriesServer s) {
    }

    public void visualizerSelected(VisualizerContext f) {
    }

    public void visualizerFrameDisposed(VisualizerContext f) {
    }

    public void desktopSelected(DesktopContext desktopPane) {
    }

    public void desktopDisposed(DesktopContext desktopPane) {
    }

}
