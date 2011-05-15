package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.selector.shared.*;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import com.od.jtimeseries.ui.timeseries.RemoteHttpTimeSeries;
import com.od.jtimeseries.ui.timeseries.RemoteHttpTimeSeriesCollection;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.util.identifiable.Identifiable;

import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 30/04/11
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
public class VisualizerImportExportHandler extends ContextImportExportHandler {

    public VisualizerImportExportHandler(TimeSeriesContext rootContext, TimeSeriesServerDictionary serverDictionary) {
        super(rootContext);
        setTimeSeriesFactory(new VisualizerTimeSeriesFactory());
        setContextFactory(new ServerContextCreatingContextFactory(rootContext, serverDictionary));
    }

    protected boolean canImport(Component component, IdentifiableListActionModel identifiables, Identifiable target) {
        return identifiables.isSelectionLimitedToType(TimeSeriesContext.class, UIPropertiesTimeSeries.class);
    }

    protected boolean shouldImport(Identifiable i, Identifiable target) {
        return ! TimeSeriesContext.class.isAssignableFrom(i.getClass());  //ignore contexts when importing, import only timeseries leaf nodes
    }

    protected ImportItem getImportItem(Component component, Identifiable identifiable, Identifiable target) {
        UIPropertiesTimeSeries s = (UIPropertiesTimeSeries)identifiable;
        return new ImportItem(
            s.getPath(),
            s.getDescription(),
            UIPropertiesTimeSeries.class,
            s.getConfig()
        );
    }

    public int getSourceActions(IdentifiableListActionModel selected) {
        return DnDConstants.ACTION_COPY_OR_MOVE;
    }

    private class VisualizerTimeSeriesFactory extends AbstractUIContextTimeSeriesFactory {

        protected UIPropertiesTimeSeries createTimeSeriesForConfig(UiTimeSeriesConfig config) throws MalformedURLException {
            //http series are unique by URL, to minimise unnecessary queries.
            //Get or create the instance for this URL
            RemoteHttpTimeSeries httpSeries = RemoteHttpTimeSeriesCollection.getOrCreateHttpSeries(config);

            //the http series is wrapped with a ChartingTimeSeries instance which is unique to
            //this visualizier, and so can have local settings for display name, colour etc.
            return new ChartingTimeSeries(httpSeries, config);
        }
    }
}
