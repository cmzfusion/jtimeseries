package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.source.ValueSupplier;

import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnector;
import javax.management.*;
import javax.management.openmbean.CompositeDataSupport;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.io.IOException;
import java.net.MalformedURLException;

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

    private final TimePeriod timePeriod;
    private final String id;
    private final String description;
    private final String serviceUrl;
    private JMXServiceURL url;
    private final List<JmxValue> listOfJmxValue;
    private final AggregateFunction aggregateFunction;
    private Map<String, ?> connectorEnvironment;
    private JmxValueSupplier valueSupplier = new JmxValueSupplier();
    private String parentContextPath;
    private double divisor = 1;

    public JmxMetric(TimePeriod timePeriod, String parentContextPath, String id, String description, String serviceUrl, JmxValue jmxValue ) {
        this(timePeriod, parentContextPath, id, description, serviceUrl, Arrays.asList(jmxValue), AggregateFunctions.MAX()); //max of 1 value is that value
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

    public void setupSeries(TimeSeriesContext metricContext) {
        try {
            url = new JMXServiceURL(serviceUrl);
        } catch (MalformedURLException e) {
            logMethods.logError("Failed to set up JMX Metric " + id + " - bad URL " + serviceUrl, e);
        }
        metricContext.createTimedValueSource(id, description, valueSupplier, timePeriod);
    }

    public void setConnectorEnvironment(Map<String, ?> connectorEnvironment) {
        this.connectorEnvironment = connectorEnvironment;
    }

    private class JmxValueSupplier implements ValueSupplier {

        public Numeric getValue() {
            JMXConnector jmxc = null;
            Numeric result = null;
            try {
                jmxc = JMXConnectorFactory.connect(url, connectorEnvironment);
                MBeanServerConnection jmxConnection = jmxc.getMBeanServerConnection();
                retreiveAndAddValues(jmxConnection);
                result = aggregateFunction.calculateAggregateValue();
                aggregateFunction.clear();
            } catch (Throwable t) {
                logMethods.logError("Error in JMX Metric connection", t);
            } finally {
                if ( jmxc != null) {
                    try {
                        jmxc.close();
                    } catch (IOException e) {
                        logMethods.logError("Failed to close jmx connection", e);
                    }
                }
            }
            if ( result != null && divisor != 1) {
                result = new DoubleNumeric(result.doubleValue() / divisor);
            }
            return result;
        }

        private void retreiveAndAddValues(MBeanServerConnection jmxConnection) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException, MalformedObjectNameException {
            for ( JmxValue n : listOfJmxValue) {
                Object value = ((CompositeDataSupport)jmxConnection.getAttribute(
                    new ObjectName(n.getObjectName()), n.getAttribute())).get(n.getCompositeDataKey()
                );
                Double d = Double.valueOf(value.toString());
                aggregateFunction.addValue(d);
            }
        }
    }

}
