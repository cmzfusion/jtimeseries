package com.od.jtimeseries.ui.net.udp;

import com.od.jtimeseries.net.udp.RemoteHttpServer;
import com.od.jtimeseries.net.udp.UdpPingHttpServerDictionary;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 11-Dec-2010
 * Time: 21:52:10
 * To change this template use File | Settings | File Templates.
 *
 * TimeSeriesServerDictionary which notified listeners on the swing event thread
 * when servers are added or removed
 */
public class UiRemoteHttpServerDictionary extends UdpPingHttpServerDictionary {

    private List<HttpServerDictionaryListener> listeners = new LinkedList<HttpServerDictionaryListener>();

    public void addDictionaryListener(HttpServerDictionaryListener l) {
        listeners.add(l);
    }

    public void removeDictionaryListener(HttpServerDictionaryListener l) {
        listeners.remove(l);
    }

    protected void serverAdded(final RemoteHttpServer s) {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    List<HttpServerDictionaryListener> listenerSnapshot = new LinkedList<HttpServerDictionaryListener>(listeners);
                    for (HttpServerDictionaryListener l : listenerSnapshot) {
                        l.serverAdded(s);
                    }
                }
            }
        );
    }

     protected void serverRemoved(final RemoteHttpServer s) {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    List<HttpServerDictionaryListener> listenerSnapshot = new LinkedList<HttpServerDictionaryListener>(listeners);
                    for (HttpServerDictionaryListener l : listenerSnapshot) {
                        l.serverRemoved(s);
                    }
                }
            }
        );
    }

    public static interface HttpServerDictionaryListener {

        void serverAdded(RemoteHttpServer s);

        void serverRemoved(RemoteHttpServer s);
    }
}
