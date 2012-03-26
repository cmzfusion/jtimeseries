/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.ui.identifiable;

import com.od.jtimeseries.context.impl.SeriesContext;
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
public class TimeSeriesServerContext extends SeriesContext implements Displayable {

    private TimeSeriesServer server;
    private boolean loading;

    private PropertyChangeListener connectionListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            fireNodeChanged("connectionFailed");
        }
    };

    private PropertyChangeListener descriptionListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            fireNodeChanged("description");
        }
    };

    public TimeSeriesServerContext(TimeSeriesServer server) {
        //the server context renderer will render the description, which may change
        //the actual identifier is the address + port, which forms the unchanging key for the server
        super(server.getServerContextIdentifier(), server.getServerContextIdentifier(), false);
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

                    l = new WeakReferenceListener("description", descriptionListener);
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
