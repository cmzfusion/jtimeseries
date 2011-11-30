package com.od.jtimeseries.component.managedmetric.jmx;

import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.impl.DefaultCounter;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24/11/11
 * Time: 21:48
 *
 * A default service to create and pool JMX connections
 * This service maintains a map of connections by JmxServiceURL
 *
 * It will periodically close any connections which have not been accessed
 * for a configurable time period
 *
 * Unlike a traditional pool, synchronization/ownership of the connection
 * for each serviceURL must be handled by client classes, the pool
 * acts as a mechanism for storing created connections only
 */
public class DefaultJmxConnectionPool implements JmxConnectionPool {

    private static final LogMethods logMethods = LogUtils.getLogMethods(DefaultJmxConnectionPool.class);
    private static Counter jmxConnectionCounter = new DefaultCounter("dummy counter", "");

    private int maxConnectionIdlePeriod = 1800000;  //0.5 hours
    private Map<String, ?> connectorEnvironment;

    private Map<JMXServiceURL, JmxConnectionWrapperImpl> connectionMap = new ConcurrentHashMap<JMXServiceURL, JmxConnectionWrapperImpl>();

    public DefaultJmxConnectionPool(int maxConnectionIdlePeriodMillis) {
        this.maxConnectionIdlePeriod = maxConnectionIdlePeriodMillis;
        startConnectionPruningThread();
    }

    public JmxConnectionWrapper getConnection(JMXServiceURL serviceUrl) throws Exception {
        logMethods.logDebug("Getting JMX connection to service " + serviceUrl);
        return connectionMap.get(serviceUrl);
    }

    public void removeConnection(JmxConnectionWrapper connection) {
        if ( connectionMap.remove(connection.getServiceURL()) != null ) {
            jmxConnectionCounter.decrementCount();
        }
    }

    public JmxConnectionWrapper createAndAddConnection(JMXServiceURL key) throws IOException {
        if ( connectionMap.containsKey(key)) {
            throw new UnsupportedOperationException("Connection pool already contains a connection for " + key);
        } else {
            logMethods.logInfo("Creating JMX connection to service " + key);
            JmxConnectionWrapperImpl c = new JmxConnectionWrapperImpl(key, connectorEnvironment);
            connectionMap.put(key, c);
            jmxConnectionCounter.incrementCount();
            return c;
        }
    }


    //Remove any connections past the age limit
    //If after this there are still more connections than the limit, remove the connections from the end of the list
    //(these should be the least recently used)
    private synchronized void pruneByAge() {

        Map<JMXServiceURL,JmxConnectionWrapperImpl> snapshotAcquirables = new HashMap<JMXServiceURL, JmxConnectionWrapperImpl>(connectionMap);
        JmxConnectionWrapperImpl c;

        for (Map.Entry<JMXServiceURL, JmxConnectionWrapperImpl> e : snapshotAcquirables.entrySet()) {
            c = e.getValue();
            if (c.getAge() > maxConnectionIdlePeriod) {
                logMethods.logDebug("Closing JMX connection " + c + " which is " + c.getAge() + " millis old");
                c.close();
                connectionMap.remove(c.getServiceURL());
            }
        }
    }

    public void setConnectorEnvironment(Map<String, ?> connectorEnvironment) {
        this.connectorEnvironment = connectorEnvironment;
    }

    private void startConnectionPruningThread() {
        ScheduledExecutorService pruningExecutor = NamedExecutors.newSingleThreadScheduledExecutor("JmxConnectionPruning");
        pruningExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                pruneByAge();
            }},
            30,
            30,
            TimeUnit.SECONDS
        );
    }

    public String toString() {
        return "DefaultJmxExecutorService, max connection idle period ms:" + maxConnectionIdlePeriod;
    }


    /**
     * Pooled Acquirable JMX connection
     * Once acquired, must be released before it can be obtained for further usage
     */
    static class JmxConnectionWrapperImpl implements JmxConnectionWrapper {

        private JMXConnector connector;
        private JMXServiceURL serviceURL;
        private Map<String, ?> connectorEnvironment;
        private MBeanServerConnection connection;
        private long lastUsageTime = System.currentTimeMillis();
        private volatile boolean closed;

        JmxConnectionWrapperImpl(JMXServiceURL serviceURL,  Map<String, ?> connectorEnvironment) throws IOException {
            this.serviceURL = serviceURL;
            this.connectorEnvironment = connectorEnvironment;
            openConnection();
        }

        private void openConnection() throws IOException {
            this.connector = JMXConnectorFactory.connect(serviceURL, connectorEnvironment);
            this.connection = connector.getMBeanServerConnection();
        }

        public JMXServiceURL getServiceURL() {
            return serviceURL;
        }

        public MBeanServerConnection getConnection() {
            lastUsageTime = System.currentTimeMillis();
            return connection;
        }

        public void close() {
            if ( ! closed ) {
                closed = true;
                try {
                    connector.close();
                } catch (IOException e) {
                    logMethods.logError("Failed to close JMX connection to " + serviceURL, e);
                }
            }
        }

        public long getAge() {
            return System.currentTimeMillis() - lastUsageTime;
        }

        public String toString() {
            return "JmxConnectionWrapper { " + serviceURL + " }";
        }
    }

    public static void setJmxConnectionCounter(Counter jmxConnectionCounter) {
        jmxConnectionCounter.incrementCount(DefaultJmxConnectionPool.jmxConnectionCounter.getCount()); //add any initial value

        DefaultJmxConnectionPool.jmxConnectionCounter = jmxConnectionCounter;
    }
}
