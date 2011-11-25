/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.component.managedmetric.jmx;

import com.od.jtimeseries.component.managedmetric.ManagedMetric;
import com.od.jtimeseries.component.managedmetric.jmx.measurement.JmxMeasurement;
import com.od.jtimeseries.component.managedmetric.jmx.value.JmxValue;
import com.od.jtimeseries.component.util.path.PathMapper;
import com.od.jtimeseries.component.util.path.PathMappingResult;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.identifiable.IdentifiableBase;
import com.od.jtimeseries.scheduling.Triggerable;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.util.TimeSeriesExecutorFactory;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.time.TimePeriod;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;
import javax.naming.ServiceUnavailableException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 03-Dec-2009
 * Time: 22:14:47
 * To change this template use File | Settings | File Templates.
 *
 * Connect to a JMX management service to capture values to a timeseries (e.g. heap memory)
 * This is used by the server itself to capture its own memory usage.
 * It may also be configured to capture performance stats from third party processes.
 * See the serverMetricsContext.xml where the jmx metrics are defined.
 *
 * One JmxMetric may collect a number of jmx values - this enables us to make the most efficient use of 
 * the connection
 */
public class JmxMetric implements ManagedMetric {

    private static final LogMethods logMethods = LogUtils.getLogMethods(JmxMetric.class);

    private static JmxConnectionPool jmxConnectionPool = new DefaultJmxConnectionPool(10, 60000);
    private static final AtomicInteger triggerableId = new AtomicInteger();

    private final TimePeriod timePeriod;
    private final String serviceUrl;
    private JMXServiceURL url;
    private List<com.od.jtimeseries.component.managedmetric.jmx.measurement.JmxMeasurement> jmxMeasurements;
    private List<JmxMeasurementTask> measurementTaskTasks = new ArrayList<JmxMeasurementTask>();
    private String description = "";

    /**
     * A JmxMetric with a single series / measurement
     */
    public JmxMetric(TimePeriod timePeriod, String serviceUrl, String metricParentContextPath, String metricId, String metricDescription, JmxValue metricJmxValue ) {
        this(timePeriod, metricParentContextPath, metricId, metricDescription, serviceUrl, Arrays.asList(metricJmxValue), AggregateFunctions.LATEST()); //last of 1 value is that value
    }

    /**
     * A JmxMetric with a single series, which reads several jmx values and aggregates them using a defined function (e.g. Sum)
     */
    public JmxMetric(TimePeriod timePeriod, String serviceUrl, String metricParentContextPath, String metricId, String metricDescription, List<JmxValue> listOfJmxValue, AggregateFunction aggregateFunction) {
        this(timePeriod, serviceUrl, Arrays.asList(new JmxMeasurement(metricParentContextPath, metricId, metricDescription, listOfJmxValue, aggregateFunction)));
    }

    public JmxMetric(TimePeriod timePeriod, String serviceUrl, JmxMeasurement jmxMeasurement) {
        this(timePeriod, serviceUrl, Arrays.asList(jmxMeasurement));
    }

    public JmxMetric(TimePeriod timePeriod, String serviceUrl, List<JmxMeasurement> jmxMeasurements) {
        this.timePeriod = timePeriod;
        this.serviceUrl = serviceUrl;
        this.jmxMeasurements = jmxMeasurements;
    }

    /**
     * Set a description for this JMX Metric
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return "JmxMetric " + description + " " + serviceUrl + " timeperiod: " + timePeriod + " measurements: " + jmxMeasurements.size();
    }

    protected static JmxConnectionPool getJmxConnectionPool() {
        return jmxConnectionPool;
    }

    public static void setJmxConnectionPool(JmxConnectionPool jmxExecutorService) {
        JmxMetric.jmxConnectionPool = jmxExecutorService;
    }

    public void initializeMetrics(TimeSeriesContext rootContext, PathMapper pathMapper) {
        try {
            url = new JMXServiceURL(serviceUrl);
        } catch (MalformedURLException e) {
            logMethods.logError("Failed to set up JMX Metric - bad URL " + serviceUrl, e);
        }

        createJmxMeasurementTasks(rootContext, pathMapper);

        //adding the triggerable to root context should cause it to start getting triggered
        rootContext.addChild(new TriggerableJmxConnectTask());
    }

    private void createJmxMeasurementTasks(TimeSeriesContext rootContext, PathMapper pathMapper) {
        for (JmxMeasurement m : jmxMeasurements) {
            String path = m.getParentContextPath() + Identifiable.NAMESPACE_SEPARATOR + m.getId();
            PathMappingResult r = pathMapper.getPathMapping(path);
            switch ( r.getType()) {
                case PERMIT:
                    doCreateMeasurementTask(rootContext, path, m);
                    break;
                case MIGRATE:
                    doCreateMeasurementTask(rootContext, r.getNewPath(), m);
                    break;
                case DENY:
                default:
                    logMethods.logError("Cannot create JMX metric at path " + path + " this path is denied by PathMapper rules");
            }
        }
    }

    private void doCreateMeasurementTask(TimeSeriesContext rootContext, String newPath, com.od.jtimeseries.component.managedmetric.jmx.measurement.JmxMeasurement m) {
        ValueRecorder r = rootContext.createValueRecorderSeries(newPath, m.getDescription());
        measurementTaskTasks.add(new JmxMeasurementTask(r, m));
    }

    private class TriggerableJmxConnectTask extends IdentifiableBase implements Triggerable {

        public TriggerableJmxConnectTask() {
            super("TriggerableJmxConnectTask" + triggerableId.getAndIncrement(), "Trigger for jmx metric at serviceUrl " + serviceUrl);
        }

        public TimePeriod getTimePeriod() {
            return timePeriod;
        }

        public void trigger(long timestamp) {
            Runnable jmxMetricTask = new Runnable() {
                public void run() {
                    JmxConnectionWrapper w = null;
                    try {
                        w = getConnection();
                        if ( w != null) {
                            runMeasurementTasks(w);
                        }  else {
                            recordNaN();
                        }

                    } finally {
                        getJmxConnectionPool().returnConnection(w);
                    }
                }
            };
            TimeSeriesExecutorFactory.getJmxMetricExecutor(JmxMetric.this).execute(jmxMetricTask);
        }

        private void recordNaN() {
            for (JmxMeasurementTask m : measurementTaskTasks) {
                if ( m.recordNanIfFailed()) {
                    m.recordNaN();
                }
            }
        }

        private void runMeasurementTasks(JmxConnectionWrapper w) {
            for (JmxMeasurementTask m : measurementTaskTasks) {
                try {
                     m.processMeasurement(w.getConnection());
                } catch ( Throwable t) {
                    logMethods.logWarning("Could not read JmxMeasurement " + m + " from connection " + w + ", " + t.getClass().getSimpleName());
                    if ( m.recordNanIfFailed()) {
                        m.recordNaN();
                    }
                }
            }
        }

        private JmxConnectionWrapper getConnection() {
            JmxConnectionWrapper w = null;
            try {
                w = getJmxConnectionPool().getConnection(url);
            } catch (Throwable t) {
                if ( t instanceof IOException || t instanceof ServiceUnavailableException) {
                    //stop stack traces for connect exceptions filling the logs
                    logMethods.logWarning("Could not get JMX connection to JMX management service for " + JmxMetric.this);
                } else {
                    logMethods.logError("Error getting JMX connection for " + JmxMetric.this, t);
                }
            }
            return w;
        }
    }

    private class JmxMeasurementTask {

        private Numeric result = DoubleNumeric.NaN;
        private ValueRecorder valueRecorder;
        private AggregateFunction aggregateFunction;
        private JmxMeasurement measurement;

        private JmxMeasurementTask(ValueRecorder valueRecorder, com.od.jtimeseries.component.managedmetric.jmx.measurement.JmxMeasurement measurement) {
            this.valueRecorder = valueRecorder;
            this.aggregateFunction = measurement.getAggregateFunction();
            this.measurement = measurement;
        }

        private void processMeasurement(MBeanServerConnection jmxConnection) throws Exception {
            result = DoubleNumeric.NaN;
            retreiveAndAddValues(jmxConnection, aggregateFunction);
            result = aggregateFunction.calculateAggregateValue();
            aggregateFunction = aggregateFunction.next(); //allow chaining
            if ( measurement.getDivisor() != 1) {
                result = DoubleNumeric.valueOf(result.doubleValue() / measurement.getDivisor());
            }
            valueRecorder.newValue(result);
        }

        private void retreiveAndAddValues(MBeanServerConnection jmxConnection, AggregateFunction aggregateFunction) throws Exception {
            for ( JmxValue n : measurement.getListOfJmxValue()) {
                n.readValues(jmxConnection, aggregateFunction);
            }
        }

        public boolean recordNanIfFailed() {
            return measurement.isRecordNanIfFailed();
        }

        public void recordNaN() {
            valueRecorder.newValue(DoubleNumeric.NaN);
        }

        public String toString() {
            return measurement.toString();
        }
    }

}
