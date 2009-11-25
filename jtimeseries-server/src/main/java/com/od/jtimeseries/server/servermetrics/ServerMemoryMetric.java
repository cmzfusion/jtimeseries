package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.source.impl.DefaultValueRecorder;
import com.od.jtimeseries.capture.impl.DefaultCapture;

import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.JMX;
import java.lang.management.MemoryMXBean;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2009
 * Time: 22:11:24
 * To change this template use File | Settings | File Templates.
 *
 * Use JMX to monitor server's own memory usage
 */
public class ServerMemoryMetric extends ServerMetric {

    LogMethods logMethods = LogUtils.getLogMethods(ServerMemoryMetric.class);

    private String SERVER_MEMORY_ID = "ServerMemory";
    private String SERVER_MEMORY_DESCRIPTION = "Memory usage by server in MB";

    private volatile ValueRecorder valueRecorder;
    private volatile MemoryMXBean remoteMemoryBean;

    public ServerMemoryMetric() {}

    public TimePeriod getSchedulingPeriod() {
        return Time.seconds(15);
    }

    public String getSeriesId() {
        return SERVER_MEMORY_ID;
    }

    public String getMetricDescription() {
        return SERVER_MEMORY_DESCRIPTION;
    }

    public void setupSeries(TimeSeriesContext metricContext, IdentifiableTimeSeries series) {
        valueRecorder = new DefaultValueRecorder("Source_" + getSeriesId(), "Value Recorder for " + getSeriesId());
        DefaultCapture t = new DefaultCapture("Capture " + getSeriesId(), valueRecorder, series);
        metricContext.addChild(valueRecorder, t);

        connectJmx();
    }

    private void connectJmx() {
        String port = System.getProperty("com.sun.management.jmxremote.port");
        String nonSecure = System.getProperty("com.sun.management.jmxremote.authenticate");
        if ( port != null && nonSecure != null) {
            try {
                JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + port + "/jmxrmi");
                JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
                MBeanServerConnection m = jmxc.getMBeanServerConnection();
                remoteMemoryBean = JMX.newMXBeanProxy(m, new ObjectName("java.lang:type=Memory"), MemoryMXBean.class, false);

                //
                //            System.out.println("\nDomains:");
                //            String domains[] = m.getDomains();
                //            Arrays.sort(domains);
                //            for (String domain : domains) {
                //                System.out.println("\tDomain = " + domain);
                //            }
                //
                //            System.out.println("\nMBeanServer default domain = " + m.getDefaultDomain());
                //
                //            System.out.println("\nMBean count = " + m.getMBeanCount());
                //            System.out.println("\nQuery MBeanServer MBeans:");
                //            Set<ObjectName> names =  new TreeSet<ObjectName>(m.queryNames(null, null));
                //            for (ObjectName name : names) {
                //                System.out.println("\tObjectName = " + name);
                //            }

            } catch ( Throwable t) {
                logMethods.logError("Failed to start server monitoring by jmx", t);
            }
        } else {
            logMethods.logInfo("Not starting memory monitoring, jmxremote port not set or requires authentication");
        }
    }

    public void run() {
        if ( remoteMemoryBean != null) {
            long memory = (remoteMemoryBean.getHeapMemoryUsage().getUsed() + remoteMemoryBean.getNonHeapMemoryUsage().getUsed()) / 1000000;
            valueRecorder.newValue(memory);
        }
    }
}
