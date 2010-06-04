package com.od.jtimeseries.agent;

import com.od.jtimeseries.component.AbstractJTimeSeriesComponent;
import com.od.jtimeseries.component.managedmetric.ManagedMetricInitializer;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.UdpClient;
import com.od.jtimeseries.net.udp.HttpServerAnnouncementMessage;
import com.od.jtimeseries.net.httpd.JTimeSeriesHttpd;
import com.od.jtimeseries.agent.jmx.AgentConfigJmx;
import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.lang.management.ManagementFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Jun-2010
 * Time: 11:00:39
 */
public class JTimeSeriesAgent extends AbstractJTimeSeriesComponent {

    public static long startTime = System.currentTimeMillis();

    private int serverAnnouncementPingPeriodSeconds = 30;
    private int jmxManagementPort;
    private TimeSeriesContext rootContext;
    private UdpClient udpClient;
    private HttpServerAnnouncementMessage serverAnnouncementMessage;
    private AgentConfigJmx agentConfigJmx;
    private ManagedMetricInitializer managedMetricInitializer;
    private HtmlAdaptorServer htmlAdaptorServer;
    private JTimeSeriesHttpd httpdServer;


    static {
        initialize(JTimeSeriesAgent.class);
    }

    public JTimeSeriesAgent() {}

    private void startup() {
        logMethods.logInfo("Starting JTimeSeriesAgent");
        try {
            doStartup();
            logMethods.logInfo("JTimeSeriesAgent is up.");
        } catch ( Throwable t) {
            logMethods.logError("Error starting JTimeSeriesAgent", t);
        }
    }

    private void doStartup() throws IOException {
        startJmxManagementServer();
        setupManagedMetrics();
        startServerAnnouncementPings();
        startJmx();
        startTimeSeriesHttpServer();

        //start scheduling for any series (e.g server metrics) which require it
        rootContext.startScheduling().startDataCapture();
    }

    private void startJmxManagementServer() {
        try {
            logMethods.logInfo("Starting JMX Management Service");
            LocateRegistry.createRegistry(jmxManagementPort);
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:" + jmxManagementPort + "/jmxrmi");
            JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
            cs.start();
        } catch (IOException e) {
            logMethods.logError("Error creating jmx server", e);
        }
    }

    private void startServerAnnouncementPings() {
        logMethods.logInfo("Starting Server Announcement Pings");
        udpClient.sendRepeatedMessage(serverAnnouncementMessage, Time.seconds(serverAnnouncementPingPeriodSeconds));
    }

    private void setupManagedMetrics() {
        logMethods.logInfo("Setting up server metrics series");
        managedMetricInitializer.initializeServerMetrics();
    }

    private void startJmx() {
        logMethods.logInfo("Starting JMX Html Adapter Interface");
        try {
            MBeanServer mBeanServer = MBeanServerFactory.createMBeanServer();

            ObjectName configMBeanName = new ObjectName("JTimeSeriesServerConfig:name=JTimeSeriesServerConfig");
            mBeanServer.registerMBean(agentConfigJmx, configMBeanName);
            mBeanServer.registerMBean(htmlAdaptorServer, new ObjectName("adaptor:protocol=HTTP"));

            htmlAdaptorServer.start();
        } catch (Exception e) {
            logMethods.logError("Failed to start JMX interface", e);
        }
    }

    private void startTimeSeriesHttpServer() throws IOException {
        httpdServer.start();
    }

    public void setServerAnnouncementPingPeriodSeconds(int serverAnnouncementPingPeriodSeconds) {
        this.serverAnnouncementPingPeriodSeconds = serverAnnouncementPingPeriodSeconds;
    }

    public void setRootContext(TimeSeriesContext rootContext) {
        this.rootContext = rootContext;
    }

    public void setUdpClient(UdpClient udpClient) {
        this.udpClient = udpClient;
    }

    public void setServerAnnouncementMessage(HttpServerAnnouncementMessage announceMessage) {
        this.serverAnnouncementMessage = announceMessage;
    }

    public void setAgentConfigJmx(AgentConfigJmx agentConfigJmx) {
        this.agentConfigJmx = agentConfigJmx;
    }

    public void setManagedMetricInitializer(ManagedMetricInitializer metricInitializer) {
        this.managedMetricInitializer = metricInitializer;
    }

    public void setHtmlAdaptorServer(HtmlAdaptorServer htmlAdaptorServer) {
        this.htmlAdaptorServer = htmlAdaptorServer;
    }

    public void setHttpdServer(JTimeSeriesHttpd httpdServer) {
        this.httpdServer = httpdServer;
    }

    public void setJmxManagementPort(int jmxManagementPort) {
        this.jmxManagementPort = jmxManagementPort;
    }

    public static void main(String[] args) throws IOException {
        JTimeSeriesAgent server = (JTimeSeriesAgent)ctx.getBean("timeSeriesAgent");
        server.startup();
    }
}
