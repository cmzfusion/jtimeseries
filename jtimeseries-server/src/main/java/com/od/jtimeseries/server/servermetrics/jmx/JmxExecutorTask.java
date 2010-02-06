package com.od.jtimeseries.server.servermetrics.jmx;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 06-Feb-2010
 * Time: 13:20:26
 * To change this template use File | Settings | File Templates.
 */
public interface JmxExecutorTask {

    /**
     * @return the JMXServiceURL used to create the connection required by this task
     */
    JMXServiceURL getServiceURL();

    /**
     * Carry out a task using a jmxConnection
     *
     * The jmxConnection instance supplied should not be reused outside the context of the task execution
     * (do not keep a reference to it)
     */
    void executeTask(MBeanServerConnection jmxConnection) throws Exception;

}
