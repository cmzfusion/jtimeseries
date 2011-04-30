package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.timeseries.ServerTimeSeries;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.visualizer.AbstractUIContextTimeSeriesFactory;
import com.od.jtimeseries.ui.visualizer.ContextImportExportHandler;
import com.od.jtimeseries.util.identifiable.Identifiable;

import java.awt.dnd.DnDConstants;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 30/04/11
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class TimeSeriousRootImportExportHandler extends ContextImportExportHandler {

    public TimeSeriousRootImportExportHandler(TimeSeriesContext rootContext) {
        super(rootContext);
        setTimeSeriesFactory(new TimeSeriousRootContextTimeSeriesFactory());
    }

    protected boolean shouldIgnoreForImport(Identifiable i, Identifiable target) {
        return false;
    }

    protected boolean canImport(Identifiable i, Identifiable target) {
        return false;
    }

    protected ImportDetails getImportDetails(Identifiable identifiable, Identifiable target) {
        return null;
    }

    public int getSourceActions(List<? extends Identifiable> selected) {
        return DnDConstants.ACTION_COPY;
    }

    public void doExport(List<Identifiable> transferData, int action) {
    }

    //create ServerTimeSeries, which are lighter weight and not backed by an HttpTimeSeries
    //we don't want to create a RemoteHttpTimeSeries for every series in the main selector tree
    private class TimeSeriousRootContextTimeSeriesFactory extends AbstractUIContextTimeSeriesFactory {

        protected UIPropertiesTimeSeries createTimeSeriesForConfig(UiTimeSeriesConfig config) throws MalformedURLException {
            return new ServerTimeSeries(config);
        }
    }

}
