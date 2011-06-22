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
package com.od.jtimeseries.agent;

import com.od.jtimeseries.component.AbstractJTimeSeriesComponent;
import com.od.jtimeseries.component.jmx.JmxManagementService;
import com.od.jtimeseries.component.managedmetric.ManagedMetricInitializer;
import com.od.jtimeseries.util.identifiable.PathParser;
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
import java.io.IOException;

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
        new JmxManagementService().startJmxManagementService(jmxManagementPort);
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
