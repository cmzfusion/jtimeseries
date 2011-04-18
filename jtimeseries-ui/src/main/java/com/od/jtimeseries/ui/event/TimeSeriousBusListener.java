package com.od.jtimeseries.ui.event;

import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.timeserious.TimeSeriousDesktopPane;
import com.od.jtimeseries.ui.timeserious.VInternalFrame;
import com.od.jtimeseries.ui.timeserious.VisualizerInternalFrame;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;

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

    void desktopSelected(TimeSeriousDesktopPane desktopPanel);

    void visualizerSelected(VisualizerInternalFrame visualizerFrame);

    void visualizerFrameDisposed(VisualizerInternalFrame visualizerFrame);

    /**
     * A visualizer config was imported to timeserious from a config fle
     */
    void visualizerImported(VisualizerConfiguration visualizerConfiguration);

    void desktopCreated(String name);
}
