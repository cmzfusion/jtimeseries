package com.od.jtimeseries.ui.net.udp;

import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.net.udp.UdpPingTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.timeserious.event.TimeSeriousBusListener;
import com.od.swing.eventbus.EventSender;
import com.od.swing.eventbus.UIEventBus;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 11-Dec-2010
 * Time: 21:52:10
 * To change this template use File | Settings | File Templates.
 *
 * TimeSeriesServerDictionary which notified listeners via UIEventBus
 * when server are added or removed
 */
public class UiTimeSeriesServerDictionary extends UdpPingTimeSeriesServerDictionary {

    protected void serverAdded(final TimeSeriesServer s) {
        UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
            new EventSender<TimeSeriousBusListener>() {
                public void sendEvent(TimeSeriousBusListener listener) {
                    listener.serverAdded(s);
                }
            }
        );
    }

    protected void serverRemoved(final TimeSeriesServer s) {
        UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
            new EventSender<TimeSeriousBusListener>() {
                public void sendEvent(TimeSeriousBusListener listener) {
                    listener.serverRemoved(s);
                }
            }
        );
    }

}
