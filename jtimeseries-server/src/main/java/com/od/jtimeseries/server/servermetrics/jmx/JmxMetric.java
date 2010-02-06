package com.od.jtimeseries.server.servermetrics.jmx;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.servermetrics.AbstractServerMetric;
import com.od.jtimeseries.source.ValueSupplier;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.time.TimePeriod;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

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
public class JmxMetric extends AbstractServerMetric {

    private static LogMethods logMethods = LogUtils.getLogMethods(JmxMetric.class);
    private static JmxExecutorService jmxExecutorService = new DefaultJmxExecutorService(10, 60000);

    private final TimePeriod timePeriod;
    private final String id;
    private final String description;
    private final String serviceUrl;
    private JMXServiceURL url;
    private final List<JmxValue> listOfJmxValue;
    private final AggregateFunction aggregateFunction;
    private JmxValueSupplier valueSupplier = new JmxValueSupplier();
    private String parentContextPath;
    private double divisor = 1;

    public JmxMetric(TimePeriod timePeriod, String parentContextPath, String id, String description, String serviceUrl, JmxValue jmxValue ) {
        this(timePeriod, parentContextPath, id, description, serviceUrl, Arrays.asList(jmxValue), AggregateFunctions.LAST()); //last of 1 value is that value
    }

    public JmxMetric(TimePeriod timePeriod, String parentContextPath, String id, String description, String serviceUrl, List<JmxValue> listOfJmxValue, AggregateFunction aggregateFunction) {
        this.timePeriod = timePeriod;
        this.parentContextPath = parentContextPath;
        this.id = id;
        this.description = description;
        this.serviceUrl = serviceUrl;
        this.listOfJmxValue = listOfJmxValue;
        this.aggregateFunction = aggregateFunction;
    }

    public String getSeriesId() {
        return id;
    }

    public String getParentContextPath() {
        return parentContextPath;
    }

    public void setDivisor(double divisor) {
        this.divisor = divisor;
    }

    protected static JmxExecutorService getJmxExecutorService() {
        return jmxExecutorService;
    }

    public static void setJmxExecutorService(JmxExecutorService jmxExecutorService) {
        JmxMetric.jmxExecutorService = jmxExecutorService;
    }

    public void initializeMetric(TimeSeriesContext metricContext) {
        try {
            url = new JMXServiceURL(serviceUrl);
        } catch (MalformedURLException e) {
            logMethods.logError("Failed to set up JMX Metric " + id + " - bad URL " + serviceUrl, e);
        }
        metricContext.newTimedValueSource(id, description, valueSupplier, timePeriod);
    }

    private class JmxValueSupplier implements ValueSupplier {

        //Use the jmx executor service to execute a jmx task to calculate the new value for the jmx metric
        public Numeric getValue() {
            Numeric result = Numeric.NaN;
            try {
                CalculateJmxMetricTask task = new CalculateJmxMetricTask();
                getJmxExecutorService().executeTask(task);
                result = task.getResult();
            } catch (JmxExecutionException e) {
                logMethods.logError("Error performing CalculateJmxMetricTask", e);
            }

            if ( ! result.isNaN() ) {
                if ( divisor != 1) {
                    result = DoubleNumeric.valueOf(result.doubleValue() / divisor);
                }    
            }
            return result;
        }

    }

    /**
     * Calculate the value for the JMX metric
     */
    private class CalculateJmxMetricTask implements JmxExecutorTask {

        private Numeric result = Numeric.NaN;

        public void executeTask(MBeanServerConnection jmxConnection) throws Exception {
            synchronized(aggregateFunction) {
                retreiveAndAddValues(jmxConnection, aggregateFunction);
                result = aggregateFunction.calculateAggregateValue();
                aggregateFunction.clear();
            }
        }

        public JMXServiceURL getServiceURL() {
            return url;
        }

        private void retreiveAndAddValues(MBeanServerConnection jmxConnection, AggregateFunction aggregateFunction) throws Exception {
            for ( JmxValue n : listOfJmxValue) {
                n.readValues(jmxConnection, aggregateFunction);
            }
        }

        public Numeric getResult() {
            return result;
        }
    }

}
