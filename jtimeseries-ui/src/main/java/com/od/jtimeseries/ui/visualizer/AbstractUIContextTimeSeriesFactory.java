package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.timeseries.impl.DefaultTimeSeriesFactory;
import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.util.identifiable.Identifiable;

import java.net.MalformedURLException;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 30/04/11
* Time: 15:51
*/
public abstract class AbstractUIContextTimeSeriesFactory extends DefaultTimeSeriesFactory {

    public <E extends Identifiable> E createTimeSeries(Identifiable parent, String path, String id, String description, Class<E> clazzType, Object... parameters) {
        UIPropertiesTimeSeries result = null;
        try {
            if (clazzType.isAssignableFrom(UIPropertiesTimeSeries.class) && parameters.length == 1) {
                if (parameters[0] instanceof UiTimeSeriesConfig) {
                    result = createTimeSeriesForConfig((UiTimeSeriesConfig) parameters[0]);
                }
            }
        } catch (Exception e) {
            ImportExportHandler.logMethods.logError("Failed to create timeseries for visualizer based on series in source root context", e);
        }
        return (E)result;
    }

    protected abstract UIPropertiesTimeSeries createTimeSeriesForConfig(UiTimeSeriesConfig config) throws MalformedURLException;
}
