package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.selector.shared.AbstractUIRootContext;
import com.od.jtimeseries.ui.selector.shared.ContextImportExportHandler;
import com.od.jtimeseries.ui.selector.shared.ContextUpdatingBusListener;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.util.identifiable.PathParser;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 09/01/11
 * Time: 09:26
 * To change this template use File | Settings | File Templates.
 */
public class VisualizerRootContext extends AbstractUIRootContext {

    private TimeSeriesServerDictionary serverDictionary;

    public VisualizerRootContext(TimeSeriesServerDictionary serverDictionary, DisplayNameCalculator displayNameCalculator) {
        super(displayNameCalculator);
        this.serverDictionary = serverDictionary;
        ContextImportExportHandler h = new VisualizerImportExportHandler(this, serverDictionary);
        setImportExportHandler(h);
    }

    protected ContextUpdatingBusListener createContextBusListener() {
        return new ContextUpdatingBusListener(this);
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
                TimeSeriesServer s = ServerContextCreatingContextFactory.getTimeSeriesServer(c, serverDescription, serverDictionary);

                //TODO - handle case where we already have a series with this path?
                String newLocalPath = s.getServerContextIdentifier() + NAMESPACE_SEPARATOR + p.getRemainingPath();
                if ( ! contains(newLocalPath)) {
                    create(newLocalPath, c.getDescription(), UIPropertiesTimeSeries.class, c);
                }
            } catch (Exception e) {
                logMethods.logError("Failed to create series for config " + c, e);
            }
        }
    }
}
