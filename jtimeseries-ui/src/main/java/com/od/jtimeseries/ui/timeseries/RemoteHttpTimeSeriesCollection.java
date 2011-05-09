package com.od.jtimeseries.ui.timeseries;

import com.od.jtimeseries.ui.config.ConfigAware;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 06/05/11
 * Time: 14:07
 *
 * Instances of remote http series are shared, so we don't duplicate time series data
 */
public class RemoteHttpTimeSeriesCollection {

    private static CollectionClearingConfigAware collectionClearingConfigAware = new CollectionClearingConfigAware();

    private static Map<String, WeakReference<RemoteHttpTimeSeries>> existingHttpSeries = Collections.synchronizedMap(new HashMap<String, WeakReference<RemoteHttpTimeSeries>>());

    public static RemoteHttpTimeSeries getOrCreateHttpSeries(UiTimeSeriesConfig config) throws MalformedURLException {
        RemoteHttpTimeSeries result = getWeakReferencedSeries(config);
        if ( result == null ) {
            //use the config mechanism as a way of cloning the time series, the original
            //need only have been a UIPropertiesTimeSeries, not necessarily RemoteHttpTimeSeries
            //r.scheduleRefreshIfDisplayed(true);
            //return r;
            result = new RemoteHttpTimeSeries(config);
            WeakReference<RemoteHttpTimeSeries> httpSeries = new WeakReference<RemoteHttpTimeSeries>(result);
            existingHttpSeries.put(config.getTimeSeriesUrl(), httpSeries);
        }
        return result;
    }

    static RemoteHttpTimeSeries getWeakReferencedSeries(UiTimeSeriesConfig config) {
        RemoteHttpTimeSeries result = null;
        WeakReference<RemoteHttpTimeSeries> httpSeries = existingHttpSeries.get(config.getTimeSeriesUrl());
        if ( httpSeries != null ) {
            result = httpSeries.get();
        }
        return result;
    }

    public static CollectionClearingConfigAware getConfigAware() {
        return collectionClearingConfigAware;
    }

    private static class CollectionClearingConfigAware implements ConfigAware {

        public void prepareConfigForSave(TimeSeriousConfig config) {}

        public void restoreConfig(TimeSeriousConfig config) {}

        public List<ConfigAware> getConfigAwareChildren() {
            return Collections.emptyList();
        }

        public void clearConfig() {
            existingHttpSeries.clear();
        }
    }
}
