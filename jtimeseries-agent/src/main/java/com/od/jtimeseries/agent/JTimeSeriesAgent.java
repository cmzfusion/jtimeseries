package com.od.jtimeseries.agent;

import com.od.jtimeseries.component.AbstractJTimeSeriesComponent;
import com.od.jtimeseries.component.managedmetric.ManagedMetricInitializer;
import com.od.jtimeseries.util.PathParser;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.net.udp.UdpClient;
import com.od.jtimeseries.net.httpd.JTimeSeriesHttpd;
import com.od.jtimeseries.agent.jmx.AgentConfigJmx;
import com.od.jtimeseries.agent.input.InputHandlerSource;
import com.od.jtimeseries.agent.input.InputProcessor;
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

    private int jmxManagementPort;
    private TimeSeriesContext rootContext;
    private UdpClient udpClient;
    private AgentConfigJmx agentConfigJmx;
    private ManagedMetricInitializer managedMetricInitializer;
    private HtmlAdaptorServer htmlAdaptorServer;
    private JTimeSeriesHttpd httpdServer;
    private InputHandlerSource inputHandlerSource;
    private String agentMetricsContextPath;
    private boolean sendAgentMetricsToServer;

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
        startJmx();
        startTimeSeriesHttpServer();
        startInputHandler();

        //start scheduling for any series (e.g server metrics) which require it
        rootContext.startScheduling().startDataCapture();
    }

    private void startInputHandler() {
        InputProcessor inputProcessor = new InputProcessor(inputHandlerSource);
        inputProcessor.start();
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

    private void setupManagedMetrics() {
        logMethods.logInfo("Setting up server metrics series");
        if ( !sendAgentMetricsToServer) {
            logMethods.logInfo("Not sending agent self-monitoring stats to timeseries-server");
            createStandardContextForMetrics();
        }
        managedMetricInitializer.initializeServerMetrics();
    }

    //the metrics context must not be a UdpPublishing context, which would be the case by default
    //since the root context is a UdpPublishing context.
    //We don't want to send the agent's own stats to the timeseries-server.
    private void createStandardContextForMetrics() {
        PathParser p = new PathParser(agentMetricsContextPath);
        String contextName = p.removeLastNode();
        String parentPath = p.getRemainingPath();
        TimeSeriesContext parentContext = rootContext.createContext(parentPath);
        DefaultTimeSeriesContext metricsContext = new DefaultTimeSeriesContext(contextName, "Context for agent self-monitoring statistics");
        parentContext.addChild(metricsContext);
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

    public void setRootContext(TimeSeriesContext rootContext) {
        this.rootContext = rootContext;
    }

    public void setUdpClient(UdpClient udpClient) {
        this.udpClient = udpClient;
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

    public void setInputHandlerSource(InputHandlerSource inputHandlerSource) {
        this.inputHandlerSource = inputHandlerSource;
    }

    public void setAgentMetricsContextPath(String agentMetricsContextPath) {
        this.agentMetricsContextPath = agentMetricsContextPath;
    }

    public void setSendAgentMetricsToServer(boolean sendAgentMetricsToServer) {
        this.sendAgentMetricsToServer = sendAgentMetricsToServer;
    }

    public static void main(String[] args) throws IOException {
        JTimeSeriesAgent server = (JTimeSeriesAgent)ctx.getBean("timeSeriesAgent");
        server.startup();
    }
}
