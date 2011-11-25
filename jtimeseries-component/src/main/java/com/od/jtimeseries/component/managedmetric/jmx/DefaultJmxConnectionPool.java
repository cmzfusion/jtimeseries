package com.od.jtimeseries.component.managedmetric.jmx;

import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24/11/11
 * Time: 21:48
 *
 * A default service to pool JMX connections
 *
 * This service maintains a map of connections by JmxServiceURL
 * There is an upper limit on the number of permitted connections, and a maximum connection idle period
 * after which a pooled connection will be closed
 *
 * If the permitted connection count is reached, subsequent requests for connections will cause the
 * oldest established connection to be closed
 */
public class DefaultJmxConnectionPool extends AbstractKeyedAcquirablePool<JMXServiceURL, DefaultJmxConnectionPool.JmxConnectionWrapperImpl> implements JmxConnectionPool {

    private static final LogMethods logMethods = LogUtils.getLogMethods(DefaultJmxConnectionPool.class);

    private int maxConnectionIdlePeriod = 1800000;  //0.5 hours
    private Map<String, ?> connectorEnvironment;

    public DefaultJmxConnectionPool(int connectionLimit, int maxConnectionIdlePeriodMillis) {
        super(connectionLimit);
        this.maxConnectionIdlePeriod = maxConnectionIdlePeriodMillis;
        startConnectionPruningThread();
    }

    public JmxConnectionWrapper getConnection(JMXServiceURL serviceUrl) throws Exception {
        logMethods.logDebug("Getting JMX connection to service " + serviceUrl);
        JmxConnectionWrapperImpl connection = getAcquirable(serviceUrl);
        while ( connection.isClosed()) {
            connection = getAcquirable(serviceUrl);
        }
        return connection;
    }

    public void returnConnection(JmxConnectionWrapper connection) {
        super.returnAcquirable((JmxConnectionWrapperImpl)connection);
    }


    protected JmxConnectionWrapperImpl createAcquirable(JMXServiceURL key) throws IOException {
        logMethods.logDebug("Creating JMX connection to service " + key);
        return new JmxConnectionWrapperImpl(key, connectorEnvironment);
    }


    //Remove any connections past the age limit
    //If after this there are still more connections than the limit, remove the connections from the end of the list
    //(these should be the least recently used)
    private synchronized void pruneByAge() {

        Map<JMXServiceURL,JmxConnectionWrapperImpl> snapshotAcquirables = new LinkedHashMap<JMXServiceURL, JmxConnectionWrapperImpl>(getAcquirables());
        JmxConnectionWrapperImpl currentConnection;

        for (Map.Entry<JMXServiceURL, JmxConnectionWrapperImpl> e : snapshotAcquirables.entrySet()) {
            currentConnection = e.getValue();
            if (currentConnection.getAge() > maxConnectionIdlePeriod) {
                removeAcquirable(currentConnection.getServiceURL(), currentConnection);
            }
        }
    }

    protected void doRemoveAcquirable(JMXServiceURL k, JmxConnectionWrapperImpl connection) {
        logMethods.logDebug("Closing JMX connection " + connection + " which is " + connection.getAge() + " millis old");
        connection.close();
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
        return "DefaultJmxExecutorService, max connections: " + getMaxPoolSize() + " max age ms:" + maxConnectionIdlePeriod;
    }


    /**
     * Pooled Acquirable JMX connection
     * Once acquired, must be released before it can be obtained for further usage
     */
    static class JmxConnectionWrapperImpl implements JmxConnectionWrapper, Acquirable {

        private JMXConnector connector;
        private JMXServiceURL serviceURL;
        private Map<String, ?> connectorEnvironment;
        private MBeanServerConnection connection;
        private long lastUsageTime;
        private Semaphore semaphore = new Semaphore(1, true);
        private volatile boolean closed;

        JmxConnectionWrapperImpl(JMXServiceURL serviceURL,  Map<String, ?> connectorEnvironment) throws IOException {
            this.serviceURL = serviceURL;
            this.connectorEnvironment = connectorEnvironment;
            openConnection(serviceURL);
        }

        private void openConnection(JMXServiceURL serviceURL) throws IOException {
            this.connector = JMXConnectorFactory.connect(serviceURL, connectorEnvironment);
            this.connection = connector.getMBeanServerConnection();
        }

        public void acquire() {
            semaphore.acquireUninterruptibly();
        }

        public void release() {
            semaphore.release();
        }

        public JMXServiceURL getServiceURL() {
            return serviceURL;
        }

        public MBeanServerConnection getConnection() {
            lastUsageTime = System.currentTimeMillis();
            return connection;
        }

        public void close() {
            closed = true;
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

        public boolean isClosed() {
            return closed;
        }
    }


}
