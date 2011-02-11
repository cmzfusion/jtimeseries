package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultContextFactory;
import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeriesFactory;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import com.od.jtimeseries.ui.timeseries.RemoteHttpTimeSeries;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.timeseries.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.util.Disposable;
import com.od.jtimeseries.util.JTimeSeriesConstants;
import com.od.jtimeseries.util.PathParser;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.QueryResult;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 09/01/11
 * Time: 09:26
 * To change this template use File | Settings | File Templates.
 */
public class VisualizerRootContext extends DefaultTimeSeriesContext {

    private static final LogMethods logMethods = LogUtils.getLogMethods(VisualizerRootContext.class);
    private TimeSeriesServerDictionary serverDictionary;

    public VisualizerRootContext(TimeSeriesServerDictionary serverDictionary) {
        this.serverDictionary = serverDictionary;
        setTimeSeriesFactory(new VisualizerTimeSeriesFactory());
        setContextFactory(new VisualizerContextFactory());
    }

    public void addIdentifiables(List<? extends Identifiable> identifiables) {

        LinkedHashSet<UIPropertiesTimeSeries> toAdd = new LinkedHashSet<UIPropertiesTimeSeries>();
        for ( Identifiable i : identifiables) {
            //identifiables in the list may be at different levels of the hierarchy from
            //the same tree structure, parents appear before their descendants
            //Check we have not already added this node to the list before adding it,
            if ( ! toAdd.contains(i)) {

                //this node, plus any children
                if ( i instanceof UIPropertiesTimeSeries) {
                    toAdd.add((UIPropertiesTimeSeries)i);
                }
                toAdd.addAll(i.findAll(UIPropertiesTimeSeries.class).getAllMatches());
            }
        }

        for ( UIPropertiesTimeSeries s : toAdd) {
            //TODO we may want to flag the conflict up to the user
            if ( get(s.getPath()) == null) {
                create(s.getPath(), s.getDescription(), ChartingTimeSeries.class, s.getRoot());
            }
        }
    }

    public void addChartConfigs(List<UiTimeSeriesConfig> chartConfigs) {
        for ( UiTimeSeriesConfig c : chartConfigs ) {

            URL url = null;
            try {

                //the first node in the path was the server description
                //when the series was saved.
                //the same server may have a different description
                //locally
                PathParser p = new PathParser(c.getPath());
                String serverDescription = p.removeFirstNode();
                TimeSeriesServer s = getTimeSeriesServer(c, serverDescription);

                String newLocalPath = s.getDescription() + JTimeSeriesConstants.NAMESPACE_SEPARATOR + p.getRemainingPath();
                if ( get(newLocalPath) == null) {
                    create(newLocalPath, c.getDescription(), ChartingTimeSeries.class, c);
                }
            } catch (Exception e) {
                logMethods.logError("Failed to create series for config " + c, e);
            }
        }
    }

    private TimeSeriesServer getTimeSeriesServer(UiTimeSeriesConfig c, String serverDescription) throws MalformedURLException, UnknownHostException {
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

    class VisualizerTimeSeriesFactory extends DefaultTimeSeriesFactory {

        public <E extends Identifiable> E createTimeSeries(Identifiable parent, String path, String id, String description, Class<E> clazzType, Object... parameters) {
            E result = null;
            try {
                if (clazzType.isAssignableFrom(ChartingTimeSeries.class) && parameters.length == 1) {
                    //if the parameter is a root context - this means we are trying to recreate a series from
                    //another context tree within this visualizer's context tree
                    if (parameterIsOtherRoot(parameters[0])) {
                        TimeSeriesContext otherRoot = (TimeSeriesContext) parameters[0];
                        UIPropertiesTimeSeries otherSeries = otherRoot.get(path, UIPropertiesTimeSeries.class);
                        UiTimeSeriesConfig config = new UiTimeSeriesConfig(otherSeries);
                        result = (E)getChartingTimeSeriesForConfig(config);
                    //the parameter may be a deserialized config
                    } else if (parameters[0] instanceof UiTimeSeriesConfig) {
                        result = (E)getChartingTimeSeriesForConfig((UiTimeSeriesConfig) parameters[0]);
                    }
                }
            } catch (Exception e) {
                logMethods.logError("Failed to create timeseries for visualizer based on series in source root context", e);
            }
            return result;
        }

        private <E extends Identifiable> E getChartingTimeSeriesForConfig(UiTimeSeriesConfig config) throws MalformedURLException {
            //http series are unique by URL, to minimise unnecessary queries.
            //Get or create the instance for this URL
            RemoteHttpTimeSeries httpSeries = RemoteHttpTimeSeries.getOrCreateHttpSeries(config);

            //the http series is wrapped with a ChartingTimeSeries instance which is unique to
            //this visualizier, and so can have local settings for display name, colour etc.
            return (E)new ChartingTimeSeries(httpSeries, config);
        }
    }

    private class VisualizerContextFactory extends DefaultContextFactory {

        //if we are creating a context in this tree and another root context is the parameter, we may be able to use
        //the information from the parameter to help us create a more specific type of context locally -
        //in this case we can recreate the TimeSeriesServerContext from the original context
        public <E extends Identifiable> E createContext(TimeSeriesContext parent, String id, String description, Class<E> classType, Object... parameters) {
            E result = null;
            if (classType.isAssignableFrom(TimeSeriesServerContext.class)) {
                if (parent == VisualizerRootContext.this && parameters.length == 1) {
                    if ( parameterIsOtherRoot(parameters[0])) {
                        Identifiable otherContext = ((Identifiable)parameters[0]).get(
                            parent.getPath() + JTimeSeriesConstants.NAMESPACE_SEPARATOR + id);
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

    private boolean parameterIsOtherRoot(Object parameter) {
        return parameter instanceof TimeSeriesContext && ((TimeSeriesContext) parameter).isRoot();
    }
}
