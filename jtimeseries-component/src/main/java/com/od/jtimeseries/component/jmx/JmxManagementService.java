package com.od.jtimeseries.component.jmx;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 10-Dec-2010
 * <p/>
 *
 * Logic to start the local jmx management service at a specified port
 */
public class JmxManagementService {

    protected static LogMethods logMethods = LogUtils.getLogMethods(JmxManagementService.class);

    private JMXConnectorServer connectorServer;
    public String serviceUrl;

    public void startJmxManagementService(int jmxManagementPort) {
        try {
            logMethods.logInfo("Starting JMX Management Service on port " + jmxManagementPort);
            LocateRegistry.createRegistry(jmxManagementPort);
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            serviceUrl = "service:jmx:rmi:///jndi/rmi://:" + jmxManagementPort + "/jmxrmi";
            JMXServiceURL url = new JMXServiceURL(serviceUrl);
            connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
            connectorServer.start();
        } catch (IOException e) {
            logMethods.logError("Error creating jmx server", e);
        }
    }

    public JMXConnectorServer getConnectorServer() {
        return connectorServer;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }
}
