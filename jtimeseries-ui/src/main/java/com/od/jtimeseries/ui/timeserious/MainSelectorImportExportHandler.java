package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.ContextFactory;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultContextFactory;
import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
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
 */
public class MainSelectorImportExportHandler extends ContextImportExportHandler {

    public MainSelectorImportExportHandler(TimeSeriesContext rootContext) {
        super(rootContext);
        setTimeSeriesFactory(new TimeSeriousRootContextTimeSeriesFactory());
        setContextFactory(new MainSelectorContextFactory());
    }

    protected boolean shouldIgnoreForImport(Identifiable i, Identifiable target) {
        return ! (i instanceof VisualizerContext);
    }

    protected boolean canImport(Identifiable i, Identifiable target) {
        boolean canImport = i instanceof VisualizerContext &&
            target instanceof DesktopContext &&
            ( i.getParent() != target);
        System.out.println(i + " : " + target + ": " + canImport);
        return canImport;
    }

    protected ImportDetails getImportDetails(Identifiable identifiable, Identifiable target) {
        return new ImportDetails(
            target.getPath() + "." + identifiable.getId(),
            identifiable.getDescription(),
            VisualizerContext.class,
            ((VisualizerContext)identifiable).getConfiguration()
        );
    }

    public int getSourceActions(List<? extends Identifiable> selected) {
        return DnDConstants.ACTION_COPY_OR_MOVE;
    }

    //create ServerTimeSeries, which are lighter weight and not backed by an HttpTimeSeries
    //we don't want to create a RemoteHttpTimeSeries for every series in the main selector tree
    private class TimeSeriousRootContextTimeSeriesFactory extends AbstractUIContextTimeSeriesFactory {

        protected UIPropertiesTimeSeries createTimeSeriesForConfig(UiTimeSeriesConfig config) throws MalformedURLException {
            return new ServerTimeSeries(config);
        }
    }


    private class MainSelectorContextFactory extends DefaultContextFactory {

        public <E extends Identifiable> E createContext(TimeSeriesContext parent, String id, String description, Class<E> classType, Object... parameters) {
            if ( VisualizerContext.class.isAssignableFrom(classType)) {
                return (E)new VisualizerContext(getRootContext(), (VisualizerConfiguration)parameters[0]);
            }  else if ( DesktopContext.class.isAssignableFrom(classType)) {
                return (E)new DesktopContext(getRootContext(),(DesktopConfiguration)parameters[0]);
            } else {
                return super.createContext(parent, id, description, classType, parameters);
            }
        }
    }
}
