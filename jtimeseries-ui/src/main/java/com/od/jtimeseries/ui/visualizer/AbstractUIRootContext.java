package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.context.ContextFactory;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultContextFactory;
import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeriesFactory;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.timeseries.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.timeserious.ContextUpdatingBusListener;
import com.od.jtimeseries.ui.util.Disposable;
import com.od.jtimeseries.util.JTimeSeriesConstants;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.swing.eventbus.UIEventBus;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/02/11
 * Time: 06:48
 */
public abstract class AbstractUIRootContext extends DefaultTimeSeriesContext {

    protected static final LogMethods logMethods = LogUtils.getLogMethods(VisualizerRootContext.class);
    protected TimeSeriesServerDictionary serverDictionary;
    private DisplayNameCalculator displayNameCalculator;

    public AbstractUIRootContext(TimeSeriesServerDictionary serverDictionary, DisplayNameCalculator displayNameCalculator) {
        this.serverDictionary = serverDictionary;
        this.displayNameCalculator = displayNameCalculator;
        displayNameCalculator.addRootContext(this);
    }

    protected void initializeFactoriesAndBusListener() {
        setTimeSeriesFactory(createTimeSeriesFactory());
        setContextFactory(createContextFactory());

        UIEventBus.getInstance().addEventListener(
            TimeSeriousBusListener.class,
            createContextBusListener()
        );
    }

    protected abstract ContextFactory createContextFactory();

    protected abstract TimeSeriesFactory createTimeSeriesFactory();

    protected abstract ContextUpdatingBusListener createContextBusListener();

    protected TimeSeriesServer getTimeSeriesServer(UiTimeSeriesConfig c, String serverDescription) throws MalformedURLException, UnknownHostException {
        URL url;//the host and port in the URL uniquely defines the server
        //get the local server which corresponds to this host + port
        url = new URL(c.getTimeSeriesUrl());
        return serverDictionary.getOrCreateServer(
            url.getHost(),
            url.getPort(),
            serverDescription
        );
    }

    public void dispose() {
        for (Identifiable i : findAll(Identifiable.class).getAllMatches()) {
            if ( i instanceof Disposable) {
                ((Disposable)i).dispose();
            }
        }
    }

    private boolean parameterIsOtherRoot(Object parameter) {
        return parameter instanceof TimeSeriesContext && ((TimeSeriesContext) parameter).isRoot();
    }

    protected abstract class AbstractUIContextTimeSeriesFactory extends DefaultTimeSeriesFactory {

        public <E extends Identifiable> E createTimeSeries(Identifiable parent, String path, String id, String description, Class<E> clazzType, Object... parameters) {
            UIPropertiesTimeSeries result = null;
            try {
                if (clazzType.isAssignableFrom(UIPropertiesTimeSeries.class) && parameters.length == 1) {
                    //if the parameter is a root context - this means we are trying to recreate a series from
                    //another context tree within this visualizer's context tree
                    if (parameterIsOtherRoot(parameters[0])) {
                        TimeSeriesContext otherRoot = (TimeSeriesContext) parameters[0];
                        UIPropertiesTimeSeries otherSeries = otherRoot.get(path, UIPropertiesTimeSeries.class);
                        UiTimeSeriesConfig config = new UiTimeSeriesConfig(otherSeries);
                        result = createTimeSeriesForConfig(config);
                    //the parameter may be a deserialized config
                    } else if (parameters[0] instanceof UiTimeSeriesConfig) {
                        result = createTimeSeriesForConfig((UiTimeSeriesConfig) parameters[0]);
                    } else if (parameters[0] instanceof UIPropertiesTimeSeries) {
                        result = createTimeSeriesForConfig(new UiTimeSeriesConfig((UIPropertiesTimeSeries)parameters[0]));
                    }
                }
            } catch (Exception e) {
                logMethods.logError("Failed to create timeseries for visualizer based on series in source root context", e);
            }

            if ( result != null) {
                displayNameCalculator.setDisplayName(result);
            }
            return (E)result;
        }

        protected abstract UIPropertiesTimeSeries createTimeSeriesForConfig(UiTimeSeriesConfig config) throws MalformedURLException;
    }

    public class ServerContextCreatingContextFactory extends DefaultContextFactory {

        //if we are creating a context in this tree and another root context is the parameter, we may be able to use
        //the information from the parameter to help us create a more specific type of context locally -
        //in this case we can recreate the TimeSeriesServerContext from the original context
        public <E extends Identifiable> E createContext(TimeSeriesContext parent, String id, String description, Class<E> classType, Object... parameters) {
            E result = null;
            if (classType.isAssignableFrom(TimeSeriesServerContext.class)) {
                if (parent == AbstractUIRootContext.this && parameters.length == 1) {
                    if ( parameters[0] instanceof Identifiable) {
                        Identifiable otherContext = ((Identifiable)parameters[0]).getRoot().get(id);
                        if ( otherContext instanceof TimeSeriesServerContext) {
                            TimeSeriesServer server = ((TimeSeriesServerContext) otherContext).getServer();
                            result = (E)new TimeSeriesServerContext(parent, server);
                        }
                    } else if (parameters[0] instanceof UiTimeSeriesConfig) {
                        try {
                            TimeSeriesServer server = getTimeSeriesServer(((UiTimeSeriesConfig)parameters[0]), id);
                            result = (E)new TimeSeriesServerContext(parent, server);
                        } catch (Exception e) {
                           logMethods.logError("Failed to create ServerContext for " + id, e);
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
}
