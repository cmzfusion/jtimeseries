package com.od.jtimeseries.ui.event;

import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.timeserious.DesktopPanel;
import com.od.jtimeseries.ui.timeserious.TimeSeriesDesktopPane;
import com.od.jtimeseries.ui.timeserious.VisualizerInternalFrame;
import com.od.jtimeseries.ui.visualizer.VisualizerConfiguration;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 12-Dec-2010
* Time: 16:21:06
* To change this template use File | Settings | File Templates.
*/
public interface TimeSeriousBusListener {

    void serverAdded(TimeSeriesServer s);

    void serverRemoved(TimeSeriesServer s);

    void visualizerSelected(VisualizerInternalFrame visualizerFrame);

    void desktopSelected(DesktopPanel desktopPanel);

    void visualizerFrameDisposed(VisualizerInternalFrame visualizerFrame);

    void visualizerFrameDisplayed(VisualizerInternalFrame visualizerFrame);

    /**
     * A visualizer is imported from a saved config
     * Also used to 'show' a hidden visualizer from the main selector tree
     */
    void visualizerImportedFromConfig(VisualizerConfiguration visualizerConfiguration);

    /**
     * A visualizer is removed from the application
     * @param visualizerConfiguration, null if the visualizer is currently shown
     * @param internalFrame, null if the visualizer is currently hidden
     */
    void visualizerRemoved(VisualizerConfiguration visualizerConfiguration, VisualizerInternalFrame internalFrame);
}
