package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.serialization.RoundRobinSerializer;
import com.od.jtimeseries.server.serialization.SerializationException;
import com.od.jtimeseries.server.timeseries.FilesystemTimeSeries;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 22-Nov-2009
 * Time: 00:38:44
 * To change this template use File | Settings | File Templates.
 */
public class ServerMetricInitializer {

    private static LogMethods logMethods = LogUtils.getLogMethods(ServerMetricInitializer.class);

    private TimeSeriesContext rootContext;
    private RoundRobinSerializer roundRobinSerializer;
    private TimeSeriesContext metricsContext;

    private String metricsContextPath;
    private int jmxManagementPort;

    private List<ServerMetric> metrics = new ArrayList<ServerMetric>();
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2);

    public ServerMetricInitializer(String metricsContextPath, TimeSeriesContext rootContext, RoundRobinSerializer roundRobinSerializer, int jmxManagementPort) {
        this.rootContext = rootContext;
        this.roundRobinSerializer = roundRobinSerializer;
        this.metricsContextPath = metricsContextPath;
        this.jmxManagementPort = jmxManagementPort;
        metricsContext = rootContext.getOrCreateContextForPath(metricsContextPath);
    }

    public void initializeServerMetrics() {
        createMetrics();
        setupMetrics();
    }

    private void createMetrics() {
        metrics.add(new GarbageCollectedSeriesMetric());
        metrics.add(new LiveSeriesMetric());
        metrics.add(new UpdatesReceivedMetric());
        metrics.add(new TotalSeriesCountMetric(rootContext));
        metrics.add(new ServerMemoryMetric(jmxManagementPort));
    }

    private void setupMetrics() {
        for (ServerMetric t : metrics) {
            setupMetric(t);
            scheduleMetric(t);
        }
    }

    private void scheduleMetric(ServerMetric t) {
        if ( t.getSchedulingPeriod() != null) {

            //schedule the task for this metric, if it requires scheduling
            scheduledExecutor.scheduleAtFixedRate(
                t, t.getSchedulingPeriod().getLengthInMillis(), t.getSchedulingPeriod().getLengthInMillis(), TimeUnit.MILLISECONDS
            );
        }
    }

    /**
     *  The server's own metrics series should have been loaded along with all the other persisted series if this is not the first startup.
     *  We handle the case where the metrics context path has changed, and we need to move the old series data into a new node in the tree.
     */
    private void setupMetric(ServerMetric metric) {
        String seriesId = metric.getSeriesId();
        String expectedPath = metricsContextPath + "." + metric.getSeriesId();
        IdentifiableTimeSeries oldSeries = rootContext.findTimeSeries(seriesId).getFirstMatch();

        IdentifiableTimeSeries newSeries = null;
        if ( oldSeries != null) {
            logMethods.logInfo("Found existing server metrics series " + seriesId );

            if ( oldSeries.getParent() == metricsContext) {
                newSeries = oldSeries;
                oldSeries = null;
            } else {
                removeSeriesAtOldPath(seriesId, expectedPath, oldSeries);
            }
        }

        logMethods.logInfo("Setting up server metrics series at " + expectedPath );
        if ( newSeries == null) {
            newSeries = metricsContext.createTimeSeries(metric.getSeriesId(), metric.getMetricDescription());
        }
        metric.setupSeries(metricsContext, newSeries);

        insertDataFromOldSeries(newSeries, oldSeries);
    }

    private void insertDataFromOldSeries(IdentifiableTimeSeries series, IdentifiableTimeSeries oldSeries) {
        if ( oldSeries != null ) {
            logMethods.logInfo("Adding preserved timepoints from " + oldSeries.getPath() );
            series.addAll(oldSeries);
        }
    }

    private void removeSeriesAtOldPath(String seriesId, String expectedPath, IdentifiableTimeSeries oldSeries) {
        logMethods.logInfo("Path for server metrics series " + seriesId + " has changed, the series will be moved to " + expectedPath  );

        //remove the current series for the time being, we will allow the ServerMetric to create a new one with any other resources it needs,
        removeCurrentSeries(oldSeries);

        FilesystemTimeSeries f = (FilesystemTimeSeries)oldSeries;
        f.stopPersistence();
        try {
            File file = roundRobinSerializer.getFile(f.getFileHeader());
            if ( file.canWrite() ) {
                file.delete();
            }
        } catch (SerializationException e) {
            logMethods.logError("Failed to delete server metrics series at old path " + oldSeries.getPath());
        }
    }

    private void removeCurrentSeries(IdentifiableTimeSeries oldSeries) {
        if (oldSeries != null) {
            oldSeries.getParent().removeChild(oldSeries);
        }
    }


}
