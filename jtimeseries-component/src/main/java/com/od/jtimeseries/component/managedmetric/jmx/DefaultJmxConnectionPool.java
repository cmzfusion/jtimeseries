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
    private static Counter jmxConnectionCounter = new DefaultCounter("dummy counter", "");

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

        //acquired connection may already have been closed if another waiting thread acquired it first
        //and closed it for any reason, if this has happened, try again until we acquire a good connection
        while ( connection.isClosed()) {
            connection = getAcquirable(serviceUrl);
        }

        //now we have acquired a connection, but it may not yet have been opened
        //ensure connection is opened, if failed to open, mark closed, remove from pool and throw exception
        //we cannot do this when holding the lock to add connection to pool, since the long timeout
        //establishing the connection may cause all threads to be blocked
        try {
            connection.checkConnectionOpen();
        } catch (JmxConnectionWrapperImpl.JmxConnectionPoolException e) {
            removeFromPool(connection.getServiceURL(), connection);
            throw e;
        }
        return connection;
    }

    public void returnConnection(JmxConnectionWrapper connection) {
        super.returnAcquirable((JmxConnectionWrapperImpl)connection);
    }


    protected JmxConnectionWrapperImpl createAcquirable(JMXServiceURL key) throws IOException {
        jmxConnectionCounter.incrementCount();
        logMethods.logDebug("Creating JMX connection to service " + key);
        return new JmxConnectionWrapperImpl(key, connectorEnvironment);
    }


    //Remove any connections past the age limit
    //If after this there are still more connections than the limit, remove the connections from the end of the list
    //(these should be the least recently used)
    private synchronized void pruneByAge() {

        Map<JMXServiceURL,JmxConnectionWrapperImpl> snapshotAcquirables = new LinkedHashMap<JMXServiceURL, JmxConnectionWrapperImpl>(getAcquirables());
        JmxConnectionWrapperImpl c;

        for (Map.Entry<JMXServiceURL, JmxConnectionWrapperImpl> e : snapshotAcquirables.entrySet()) {
            c = e.getValue();
            if (c.getAge() > maxConnectionIdlePeriod) {
                logMethods.logDebug("Closing JMX connection " + c + " which is " + c.getAge() + " millis old");
                acquireAndRemoveFromPool(c.getServiceURL(), c);
            }
        }
    }

    protected void doRemoveAcquirable(JMXServiceURL k, JmxConnectionWrapperImpl connection) {
        jmxConnectionCounter.decrementCount();
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
        private volatile boolean opened;

        JmxConnectionWrapperImpl(JMXServiceURL serviceURL,  Map<String, ?> connectorEnvironment) throws IOException {
            this.serviceURL = serviceURL;
            this.connectorEnvironment = connectorEnvironment;
        }

        /**
         * If this connection has not been opened, open it
         */
        public void checkConnectionOpen() throws IOException {
            if ( ! opened) {
                opened = true;
                try {
                    logMethods.logDebug("Opening JMX connection to service " + serviceURL);
                    openConnection();
                } catch (Throwable t) {
                    closed = true;  //otherwise next acquirer may try to use it
                    throw new JmxConnectionPoolException(t);
                }
            }
        }

        private void openConnection() throws IOException {
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

        public boolean isClosed() {
            return closed;
        }

        private class JmxConnectionPoolException extends IOException {

            public JmxConnectionPoolException(Throwable t) {
                super(t);
            }
        }
    }

    public static void setJmxConnectionCounter(Counter jmxConnectionCounter) {
        jmxConnectionCounter.incrementCount(DefaultJmxConnectionPool.jmxConnectionCounter.getCount()); //add any initial value

        DefaultJmxConnectionPool.jmxConnectionCounter = jmxConnectionCounter;
    }
}
