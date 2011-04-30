package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.context.ContextFactory;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import com.od.jtimeseries.ui.timeseries.RemoteHttpTimeSeries;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.timeserious.ContextUpdatingBusListener;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiablePathUtils;
import com.od.jtimeseries.util.identifiable.PathParser;

import java.net.MalformedURLException;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 09/01/11
 * Time: 09:26
 * To change this template use File | Settings | File Templates.
 */
public class VisualizerRootContext extends AbstractUIRootContext {


    public VisualizerRootContext(TimeSeriesServerDictionary serverDictionary, DisplayNameCalculator displayNameCalculator) {
        super(serverDictionary, displayNameCalculator);
        initializeFactoriesAndContextBusListener();
    }

    protected ContextFactory createContextFactory() {
        return new ServerContextCreatingContextFactory();
    }

    protected TimeSeriesFactory createTimeSeriesFactory() {
        return new VisualizerTimeSeriesFactory();
    }

    protected ContextUpdatingBusListener createContextBusListener() {
        return new ContextUpdatingBusListener(this);
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
            if ( ! contains(s.getPath())) {
                create(s.getPath(), s.getDescription(), UIPropertiesTimeSeries.class, s);
            }
        }
    }

    /**
     * Add chart configs to this visualizer, under the local server node with
     * matching URL to the server specified in each config
     */
    public void addChartConfigs(List<UiTimeSeriesConfig> chartConfigs) {
        for ( UiTimeSeriesConfig c : chartConfigs ) {
            try {

                //the first node in the path was the server description
                //when the series was saved.
                //the same server may have a different description
                //locally
                PathParser p = new PathParser(c.getPath());
                String serverDescription = p.removeFirstNode();
                TimeSeriesServer s = getTimeSeriesServer(c, serverDescription);

                //TODO - handle case where we already have a series with this path?
                String newLocalPath = s.getServerContextIdentifier() + IdentifiablePathUtils.NAMESPACE_SEPARATOR + p.getRemainingPath();
                if ( ! contains(newLocalPath)) {
                    create(newLocalPath, c.getDescription(), UIPropertiesTimeSeries.class, c);
                }
            } catch (Exception e) {
                logMethods.logError("Failed to create series for config " + c, e);
            }
        }
    }

    private class VisualizerTimeSeriesFactory extends AbstractUIContextTimeSeriesFactory {

        protected UIPropertiesTimeSeries createTimeSeriesForConfig(UiTimeSeriesConfig config) throws MalformedURLException {
            //http series are unique by URL, to minimise unnecessary queries.
            //Get or create the instance for this URL
            RemoteHttpTimeSeries httpSeries = RemoteHttpTimeSeries.getOrCreateHttpSeries(config);

            //the http series is wrapped with a ChartingTimeSeries instance which is unique to
            //this visualizier, and so can have local settings for display name, colour etc.
            return new ChartingTimeSeries(httpSeries, config);
        }
    }
}
