package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.component.jmx.JmxManagementService;
import com.od.jtimeseries.component.managedmetric.DefaultMetricSource;
import com.od.jtimeseries.component.managedmetric.ManagedMetric;
import com.od.jtimeseries.component.managedmetric.ManagedMetricInitializer;
import com.od.jtimeseries.component.managedmetric.ManagedMetricSource;
import com.od.jtimeseries.component.managedmetric.jmx.DefaultJmxExecutorService;
import com.od.jtimeseries.component.managedmetric.jmx.JmxMetric;
import com.od.jtimeseries.component.managedmetric.jmx.measurement.JmxMeasurement;
import com.od.jtimeseries.component.managedmetric.jmx.measurement.JmxMeasurements;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.time.Time;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 10-Dec-2010
 * <p/>
 * To change this template use File | Settings | File Templates.
 */
public class LocalJmxMetrics {

    private static LogMethods logMethods = LogUtils.getLogMethods(LocalJmxMetrics.class);

    private JmxManagementService jmxService = new JmxManagementService();
    private TimeSeriesContext rootContext = JTimeSeries.createRootContext();

    private String metricRootPath = "timeSerious";

    public void startJmxManagementService(int port) {
        logMethods.logInfo("Starting jmx management service");
        jmxService.startJmxManagementService(port);
    }

    public void startLocalMetricCollection() {
        logMethods.logInfo("Starting local metric connection");

        JmxMeasurement cpu = JmxMeasurements.cpuTimePercentage(metricRootPath);
        JmxMeasurement memoryPercentOfMax = JmxMeasurements.heapMemoryPercentageOfMax(metricRootPath);
        JmxMeasurement memoryUsage = JmxMeasurements.heapMemoryUsage(metricRootPath);
        JmxMeasurement gcTimePercentage = JmxMeasurements.gcTimePercentage(metricRootPath);

        ManagedMetric m = new JmxMetric(
            Time.seconds(10),
            jmxService.getServiceUrl(),
            Arrays.asList(cpu, memoryPercentOfMax, memoryUsage, gcTimePercentage)
        );

        DefaultMetricSource metricSource = new DefaultMetricSource(Collections.singletonList(m));

        DefaultJmxExecutorService executorService = new DefaultJmxExecutorService(1, 1000 * 60 * 60 * 24); //when this number is exceeded, service should reconnect in any case

        ManagedMetricInitializer managedMetricInitializer = new ManagedMetricInitializer(
            rootContext,
            Collections.singletonList((ManagedMetricSource)metricSource),
            executorService
        );

        managedMetricInitializer.initializeServerMetrics();

        rootContext.startScheduling(); 

    }

    public TimeSeriesContext getRootContext() {
        return rootContext;
    }
}
