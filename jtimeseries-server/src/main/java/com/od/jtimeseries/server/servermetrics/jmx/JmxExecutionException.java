package com.od.jtimeseries.server.servermetrics.jmx;

import javax.management.remote.JMXServiceURL;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 06-Feb-2010
* Time: 13:12:19
* To change this template use File | Settings | File Templates.
*/
public class JmxExecutionException extends Exception {

    public JmxExecutionException(JMXServiceURL serviceUrl, Throwable cause) {
        super("Exception creating JMX connection to service " + serviceUrl, cause);
    }
}
