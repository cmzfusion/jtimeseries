package com.od.jtimeseries.ui.event;

import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.timeserious.DesktopPanel;
import com.od.jtimeseries.ui.timeserious.VisualizerInternalFrame;
import com.od.jtimeseries.ui.visualizer.VisualizerConfiguration;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 12-Dec-2010
 * Time: 16:21:47
 * To change this template use File | Settings | File Templates.
 */
public class TimeSeriousBusListenerAdapter implements TimeSeriousBusListener {

    public void serverAdded(TimeSeriesServer s) {
    }

    public void serverRemoved(TimeSeriesServer s) {
    }

    public void visualizerFrameDisplayed(VisualizerInternalFrame visualizerFrame) {
    }

    //also used to 'show' a hidden visualizer from the main selector tree
    public void visualizerImportedFromConfig(VisualizerConfiguration visualizerConfiguration) {
    }

    public void visualizerSelected(VisualizerInternalFrame visualizerFrame) {
    }

    public void visualizerFrameDisposed(VisualizerInternalFrame visualizerFrame) {
    }

    public void desktopSelected(DesktopPanel desktopPane) {
    }

}
