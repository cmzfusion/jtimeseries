package com.od.jtimeseries.component.managedmetric.jmx;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24/11/11
 * Time: 15:26
 */
public interface JmxConnectionWrapper {

    JMXServiceURL getServiceURL();

    MBeanServerConnection getConnection();
}
