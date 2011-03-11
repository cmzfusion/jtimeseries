package com.od.jtimeseries.ui.event;

import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.timeserious.DesktopPanel;
import com.od.jtimeseries.ui.timeserious.TimeSeriesDesktopPane;
import com.od.jtimeseries.ui.timeserious.VisualizerInternalFrame;

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
}
