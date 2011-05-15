package com.od.jtimeseries.ui.event;

import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;

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

    void desktopSelected(DesktopContext desktopPane);

    void desktopDisposed(DesktopContext desktopPane);

    void visualizerSelected(VisualizerContext visualizerFrame);

    void visualizerFrameDisposed(VisualizerContext visualizerFrame);
}
