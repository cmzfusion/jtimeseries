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
            serviceUrl = "service:jmx:rmi:///jndi/rmi://localhost:" + jmxManagementPort + "/jmxrmi";
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
