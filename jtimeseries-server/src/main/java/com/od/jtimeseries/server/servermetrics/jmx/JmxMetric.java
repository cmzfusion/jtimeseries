package com.od.jtimeseries.server.servermetrics.jmx;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.servermetrics.ServerMetric;
import com.od.jtimeseries.server.servermetrics.jmx.measurement.JmxMeasurement;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;
import com.od.jtimeseries.scheduling.Triggerable;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;
import javax.naming.ServiceUnavailableException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;

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
 */
public class JmxMetric implements ServerMetric {

    private static LogMethods logMethods = LogUtils.getLogMethods(JmxMetric.class);
    private static JmxExecutorService jmxExecutorService = new DefaultJmxExecutorService(10, 60000);
    private static final AtomicInteger triggerableId = new AtomicInteger();

    private final TimePeriod timePeriod;
    private final String serviceUrl;
    private JMXServiceURL url;
    private List<JmxMeasurement> jmxMeasurements;
    private List<JmxMeasurementTask> measuerementTasks = new ArrayList<JmxMeasurementTask>();
    private String description = "";

    /**
     * A JmxMetric with a single series / measurement
     */
    public JmxMetric(TimePeriod timePeriod, String serviceUrl, String metricParentContextPath, String metricId, String metricDescription, JmxValue metricJmxValue ) {
        this(timePeriod, metricParentContextPath, metricId, metricDescription, serviceUrl, Arrays.asList(metricJmxValue), AggregateFunctions.LAST()); //last of 1 value is that value
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

    protected static JmxExecutorService getJmxExecutorService() {
        return jmxExecutorService;
    }

    public static void setJmxExecutorService(JmxExecutorService jmxExecutorService) {
        JmxMetric.jmxExecutorService = jmxExecutorService;
    }

    public void initializeMetrics(TimeSeriesContext rootContext) {
        try {
            url = new JMXServiceURL(serviceUrl);
        } catch (MalformedURLException e) {
            logMethods.logError("Failed to set up JMX Metric - bad URL " + serviceUrl, e);
        }

        createJmxTasks(rootContext);

        //adding the triggerable to root context should cause it to start getting triggered
        rootContext.addChild(new TriggerableJmxConnectTask());
    }

    private void createJmxTasks(TimeSeriesContext rootContext) {
        for (JmxMeasurement m : jmxMeasurements) {
            TimeSeriesContext c = rootContext.createContextForPath(m.getParentContextPath());
            ValueRecorder r = c.createValueRecorderSeries(m.getId(), m.getDescription());
            measuerementTasks.add(new JmxMeasurementTask(r, m));
        }
    }

    private class TriggerableJmxConnectTask extends IdentifiableBase implements Triggerable {

        public TriggerableJmxConnectTask() {
            super("TriggerableJmxConnectTask" + triggerableId.getAndIncrement(), "Trigger for jmx metric at serviceUrl " + serviceUrl);
        }

        public TimePeriod getTimePeriod() {
            return timePeriod;
        }

        public void trigger(long timestamp) {
            try {
                for (JmxMeasurementTask m : measuerementTasks) {
                    m.processMeasurement();
                }
            } catch (Throwable t) {
                if ( t.getCause() instanceof IOException || t.getCause() instanceof ServiceUnavailableException) {
                    //stop stack traces for connect exceptions filling the logs
                    logMethods.logWarning("Could not connect to JMX management service for " + JmxMetric.this);
                } else {
                    logMethods.logError("Error processing " + JmxMetric.this, t);
                }
            }
        }
    }

    private class JmxMeasurementTask implements JmxExecutorTask {

        private Numeric result = Numeric.NaN;
        private ValueRecorder valueRecorder;
        private AggregateFunction aggregateFunction;
        private JmxMeasurement measurement;

        private JmxMeasurementTask(ValueRecorder valueRecorder, JmxMeasurement measurement) {
            this.valueRecorder = valueRecorder;
            this.aggregateFunction = measurement.getAggregateFunction();
            this.measurement = measurement;
        }

        private void processMeasurement() throws JmxExecutionException {
            result = Numeric.NaN;
            getJmxExecutorService().executeTask(this);

            if ( ! result.isNaN() ) {
                if ( measurement.getDivisor() != 1) {
                    result = DoubleNumeric.valueOf(result.doubleValue() / measurement.getDivisor());
                }

                valueRecorder.newValue(result);
            }
        }

        public JMXServiceURL getServiceURL() {
            return url;
        }

        public void executeTask(MBeanServerConnection jmxConnection) throws Exception {
            retreiveAndAddValues(jmxConnection, aggregateFunction);
            result = aggregateFunction.calculateAggregateValue();
            aggregateFunction = aggregateFunction.next(); //allow chaining
        }

         private void retreiveAndAddValues(MBeanServerConnection jmxConnection, AggregateFunction aggregateFunction) throws Exception {
            for ( JmxValue n : measurement.getListOfJmxValue()) {
                n.readValues(jmxConnection, aggregateFunction);
            }
        }
    }
}