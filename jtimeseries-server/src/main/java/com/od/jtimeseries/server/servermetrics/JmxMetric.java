package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AbstractDoubleBasedAggregateFunction;
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
 */
public class JmxMetric extends ServerMetric {

    private static LogMethods logMethods = LogUtils.getLogMethods(JmxMetric.class);

    private final TimePeriod timePeriod;
    private final String id;
    private final String description;
    private final String serviceUrl;
    private JMXServiceURL url;
    private final List<NameAttributeAndKey> listOfNameAttributeAndKey;
    private final AggregateFunction aggregateFunction;
    private Map<String, ?> connectorEnvironment;
    private JmxValueSupplier valueSupplier = new JmxValueSupplier();

    public JmxMetric(TimePeriod timePeriod, String id, String description, String serviceUrl, String objectName, String attribute, String compositeDataKey ) {
        this(timePeriod, id, description, serviceUrl, Arrays.asList(new NameAttributeAndKey(objectName, attribute, compositeDataKey)), new FinalValueAggregateFunction());
    }

    public JmxMetric(TimePeriod timePeriod, String id, String description, String serviceUrl, List<NameAttributeAndKey> listOfNameAttributeAndKey, AggregateFunction aggregateFunction) {
        this.timePeriod = timePeriod;
        this.id = id;
        this.description = description;
        this.serviceUrl = serviceUrl;
        this.listOfNameAttributeAndKey = listOfNameAttributeAndKey;
        this.aggregateFunction = aggregateFunction;
    }

    public TimePeriod getSchedulingPeriod() {
        return timePeriod;
    }

    public String getSeriesId() {
        return id;
    }

    public String getMetricDescription() {
        return description;
    }

    public void setupSeries(TimeSeriesContext metricContext, IdentifiableTimeSeries series) {
        try {
            url = new JMXServiceURL(serviceUrl);
        } catch (MalformedURLException e) {
            logMethods.logError("Failed to set up JMX Metric " + id + " - bad URL " + serviceUrl, e);
        }
        metricContext.createTimedValueSource(id, description, valueSupplier, timePeriod);
    }

    public void run() {
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
                return result;
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
            return result;
        }

        private void retreiveAndAddValues(MBeanServerConnection jmxConnection) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException, MalformedObjectNameException {
            for ( NameAttributeAndKey n : listOfNameAttributeAndKey) {
                Object value = ((CompositeDataSupport)jmxConnection.getAttribute(
                    new ObjectName(n.getObjectName()), n.getAttribute())).get(n.getCompositeDataKey()
                );
                Double d = Double.valueOf(value.toString());
                aggregateFunction.addValue(d);
            }
        }
    }

    public static class NameAttributeAndKey {

        private final String objectName;
        private final String attribute;
        private String compositeDataKey;

        public NameAttributeAndKey(String objectName, String attribute, String compositeDataKey) {
            this.objectName = objectName;
            this.attribute = attribute;
            this.compositeDataKey = compositeDataKey;
        }

        public String getObjectName() {
            return objectName;
        }

        public String getAttribute() {
            return attribute;
        }

        public String getCompositeDataKey() {
            return compositeDataKey;
        }
    }

    private static class FinalValueAggregateFunction extends AbstractDoubleBasedAggregateFunction {

        private double value = Double.NaN;

        protected void doAddValue(double d) {
            this.value = d;
        }

        public Numeric calculateAggregateValue() {
            return new DoubleNumeric(value);
        }

        public String getDescription() {
            return "FinalValue";
        }

        public void clear() {
            value = Double.NaN;
        }

        public AggregateFunction newInstance() {
            return new FinalValueAggregateFunction();
        }
    }
}
