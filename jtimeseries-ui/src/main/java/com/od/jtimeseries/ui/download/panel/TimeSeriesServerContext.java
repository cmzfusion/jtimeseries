package com.od.jtimeseries.ui.download.panel;

import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.util.Displayable;
import com.od.swing.util.UIUtilities;
import com.od.swing.weakreferencelistener.WeakReferenceListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 09-Dec-2010
 * Time: 14:13:37
 * To change this template use File | Settings | File Templates.
 */
public class TimeSeriesServerContext extends DefaultTimeSeriesContext implements Displayable {

    private TimeSeriesServer server;
    private boolean loading;

    private PropertyChangeListener connectionListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            fireNodeChanged("connectionFailed");
        }
    };

    public TimeSeriesServerContext(TimeSeriesServer server) {
        super(server.getDescription(), server.getDescription());
        this.server = server;
        addServerListener(server);
    }

    //listen to the bound property on server
    //so that we can notify context tree observers when the connection fails
    //use a weak ref listener so that this context can be garbage collected without removing the listener instance
    private void addServerListener(final TimeSeriesServer server) {
        UIUtilities.runInDispatchThread(
            new Runnable() {
                public void run() {
                    WeakReferenceListener l = new WeakReferenceListener("connectionFailed", connectionListener);
                    l.addListenerTo(server);
                }
            }
        );
    }

    public boolean isConnectionFailed() {
        return server.isConnectionFailed();
    }

    public void setConnectionFailed(boolean connectionFailed) {
        server.setConnectionFailed(connectionFailed);
    }

    public TimeSeriesServer getServer() {
        return server;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        fireNodeChanged("loadingState");
    }

    public String getDisplayName() {
        return server.getDescription();
    }
}
