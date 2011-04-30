package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultContextFactory;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.util.identifiable.Identifiable;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 30/04/11
* Time: 15:51
*/
public class ServerContextCreatingContextFactory extends DefaultContextFactory {

    private TimeSeriesContext rootContext;
    private TimeSeriesServerDictionary serverDictionary;

    public ServerContextCreatingContextFactory(TimeSeriesContext rootContext, TimeSeriesServerDictionary serverDictionary) {
        this.rootContext = rootContext;
        this.serverDictionary = serverDictionary;
    }

    public static TimeSeriesServer getTimeSeriesServer(UiTimeSeriesConfig c, String serverDescription, TimeSeriesServerDictionary serverDictionary) throws MalformedURLException, UnknownHostException {
        URL url;//the host and port in the URL uniquely defines the server
        //get the local server which corresponds to this host + port
        url = new URL(c.getTimeSeriesUrl());
        return serverDictionary.getOrCreateServer(
            url.getHost(),
            url.getPort(),
            serverDescription
        );
    }


    //if we are creating a context in this tree we may be able to use the information from the parameter to
    //help us create a more specific type of context locally -
    //in this case we can recreate the TimeSeriesServerContext from the original context
    public <E extends Identifiable> E createContext(TimeSeriesContext parent, String id, String description, Class<E> classType, Object... parameters) {
        E result = null;
        if (classType.isAssignableFrom(TimeSeriesServerContext.class)) {
            if (parent == rootContext && parameters.length == 1) {
                if (parameters[0] instanceof UiTimeSeriesConfig) {
                    try {
                        TimeSeriesServer server = getTimeSeriesServer(((UiTimeSeriesConfig)parameters[0]), id, serverDictionary);
                        result = (E)new TimeSeriesServerContext(parent, server);
                    } catch (Exception e) {
                       ImportExportHandler.logMethods.logError("Failed to create ServerContext for " + id, e);
                    }
                }
            }
        }

        if (result == null) {
            result = super.createContext(parent, id, description, classType, parameters);
        }
        return result;
    }
}
