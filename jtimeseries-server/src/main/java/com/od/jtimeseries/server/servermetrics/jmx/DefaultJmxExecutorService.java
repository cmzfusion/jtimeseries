package com.od.jtimeseries.server.servermetrics.jmx;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 06-Feb-2010
 * Time: 11:38:11
 *
 * A default service to execute JMX connection based tasks
 *
 * This service maintains a list of open connections to jmx services
 * There is an upper limit on the number of open connections, and a maximum connection age
 *
 * The intention is to prevent closing and reopening connections if there are several metrics defined which 
 * make use of the same service URL - if there is already a connection in the active list, it may be reused
 */
public class DefaultJmxExecutorService implements JmxExecutorService {

    private static LogMethods logMethods = LogUtils.getLogMethods(DefaultJmxExecutorService.class);

    private Map<String, ?> connectorEnvironment;

    private List<JmxConnectionWrapper> activeConnections = new LinkedList<JmxConnectionWrapper>();
    private int connectionLimit = 10;
    private int maxConnectionAgeMillis = 60000;

    public DefaultJmxExecutorService(int connectionLimit, int maxConnectionAgeMillis) {
        this.connectionLimit = connectionLimit;
        this.maxConnectionAgeMillis = maxConnectionAgeMillis;
        startConnectionPruningThread();
    }

    public void setConnectorEnvironment(Map<String, ?> connectorEnvironment) {
        this.connectorEnvironment = connectorEnvironment;
    }

    private void startConnectionPruningThread() {

        ScheduledExecutorService pruningExecutor = Executors.newSingleThreadScheduledExecutor();

        //set the name of the executor thread
        pruningExecutor.execute(new Runnable() {
            public void run() {
                Thread.currentThread().setName("DefaultJmxExecutorService Connection Pruning");
            }
        });

        pruningExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                pruneConnections();
            }},
            30,
            30,
            TimeUnit.SECONDS
        );
    }

    public synchronized void executeTask(JmxExecutorTask task) throws JmxExecutionException {
        JMXServiceURL serviceUrl = null;
        try {
            serviceUrl = task.getServiceURL();
            MBeanServerConnection connection = getConnection(serviceUrl);
            task.executeTask(connection);

            //prune any excess connections
            pruneByConnectionCount();
        } catch (Throwable t) {
            logMethods.logError("Failed to get connection to JMX service at " + serviceUrl, t);
            throw new JmxExecutionException(serviceUrl, t);
        }
    }

    private MBeanServerConnection getConnection(JMXServiceURL serviceUrl) throws IOException {
        logMethods.logDebug("Getting JMX connection to service " + serviceUrl);

        //remove it if it exists, we will add it back to the front of the list later
        JmxConnectionWrapper connectionWrapper = removeFromActiveConnections(serviceUrl);

        //create it if it doesn't already exist
        if ( connectionWrapper == null) {
            logMethods.logDebug("Creating JMX connection to service " + serviceUrl);
            connectionWrapper = new JmxConnectionWrapper(serviceUrl);
        } else {
            logMethods.logDebug("Reusing JMX connection to service " + serviceUrl + " from active connections list");
        }

        //add to the start of the list of active connections (the most recently used)
        activeConnections.add(0, connectionWrapper);

        return connectionWrapper.getConnection();
    }

    private JmxConnectionWrapper removeFromActiveConnections(JMXServiceURL serviceUrl) {
        JmxConnectionWrapper result = null;
        Iterator<JmxConnectionWrapper> i = activeConnections.iterator();
        JmxConnectionWrapper connection = null;
        while (i.hasNext()) {
            connection = i.next();
            if (connection.isForService(serviceUrl)) {
                i.remove();
                result = connection;
                break;
            }
        }
        return result;
    }

    //Remove any connections past the age limit
    //If after this there are still more connections than the limit, remove the connections from the end of the list
    //(these should be the least recently used)
    private synchronized void pruneConnections() {
        pruneByAge();
        pruneByConnectionCount();
    }

    private void pruneByAge() {
        JmxConnectionWrapper currentConnection;
        Iterator<JmxConnectionWrapper> i = activeConnections.iterator();
        while(i.hasNext()) {
            currentConnection = i.next();
            if ( currentConnection.getAge() > maxConnectionAgeMillis) {
                logMethods.logDebug("Closing JMX connection " + currentConnection + " which is " + currentConnection.getAge() + " millis old");
                i.remove();
                currentConnection.close();
            }
        }
    }

    private void pruneByConnectionCount() {
        while ( activeConnections.size() > connectionLimit ) {
            JmxConnectionWrapper w = activeConnections.remove(activeConnections.size() - 1);
            logMethods.logDebug("Closing JMX connection " + w + " due to active connection limit");
            w.close();
        }
    }

    private class JmxConnectionWrapper {

        private JMXConnector connector;
        private JMXServiceURL serviceURL;
        private MBeanServerConnection connection;
        private long lastUsageTime;

        private JmxConnectionWrapper(JMXServiceURL serviceURL) throws IOException {
            this.serviceURL = serviceURL;
            openConnection(serviceURL);
        }

        private void openConnection(JMXServiceURL serviceURL) throws IOException {
            this.connector = JMXConnectorFactory.connect(serviceURL, connectorEnvironment);
            this.connection = connector.getMBeanServerConnection();
        }

        public boolean isForService(JMXServiceURL url) {
            return serviceURL.equals(url);
        }

        public MBeanServerConnection getConnection() {
            lastUsageTime = System.currentTimeMillis();
            return connection;
        }

        public void close() {
            try {
                connector.close();
            } catch (IOException e) {
                logMethods.logError("Failed to close JMX connection to " + serviceURL, e);
            }
        }

        public long getAge() {
            return System.currentTimeMillis() - lastUsageTime;
        }

        public String toString() {
            return "JmxConnectionWrapper { " + serviceURL + " }";
        }
    }

    public String toString() {
        return "DefaultJmxExecutorService, max connections: " + connectionLimit + " max age ms:" + maxConnectionAgeMillis;
    }

}
