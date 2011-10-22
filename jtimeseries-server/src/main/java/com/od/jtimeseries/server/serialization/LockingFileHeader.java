package com.od.jtimeseries.server.serialization;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 22/10/11
 * Time: 17:01
 *
 * Locking for FileHeader methods
 */
public abstract class LockingFileHeader {

    protected ReadWriteLock headerLock = new ReentrantReadWriteLock();

    public final String getPath() {
        try {
            headerLock.readLock().lock();
            return doGetPath();
        } finally {
            headerLock.readLock().unlock();
        }
    }

    protected abstract String doGetPath();

    public Properties getSeriesProperties() {
        try {
           headerLock.readLock().lock();
            return doGetSnapshot();
        } finally {
            headerLock.readLock().unlock();
        }
    }

    protected abstract Properties doGetSnapshot();

    void setSeriesProperties(byte[] serializedProperties) throws IOException {
        try {
            headerLock.writeLock().lock();
            doSetSeriesProperties(serializedProperties);
        } finally {
            headerLock.writeLock().unlock();
        }
    }

    protected abstract void doSetSeriesProperties(byte[] serializedProperties) throws IOException;

    public String setSeriesProperty(String key, String value) {
        try {
            headerLock.writeLock().lock();
            return doSetProperty(key, value);
        } finally {
            headerLock.writeLock().unlock();
        }
    }

    protected abstract String doSetProperty(String key, String value);

    public String getSeriesProperty(String key) {
        try {
            headerLock.readLock().lock();
            return doGetSeriesProperty(key);
        } finally {
            headerLock.readLock().unlock();
        }
    }

    protected abstract String doGetSeriesProperty(String key);

    public String removeSeriesProperty(String key) {
       try {
           headerLock.readLock().lock();
           return doRemoveSeriesProperty(key);
       } finally {
           headerLock.readLock().unlock();
       }
    }

    protected abstract String doRemoveSeriesProperty(String key);

    public int getHeaderLength() {
        try {
            headerLock.readLock().lock();
            return doGetHeaderLength();
        } finally {
            headerLock.readLock().unlock();
        }
    }

    protected abstract int doGetHeaderLength();

    public int calculateNewHeaderLength(int requiredLength) {
        try {
            headerLock.readLock().lock();
            return doCalculateNewHeaderLength(requiredLength);
        } finally {
            headerLock.readLock().unlock();
        }
    }

    protected abstract int doCalculateNewHeaderLength(int requiredLength);


    /**
     * @return length of round robin series, which is the maximum size this series can obtain
     */
    public int getSeriesMaxLength() {
        try {
            headerLock.readLock().lock();
            return doGetSeriesMaxLength();
        } finally {
            headerLock.readLock().unlock();
        }
    }

    protected abstract int doGetSeriesMaxLength();

    public int getCurrentSeriesSize() {
        try {
            headerLock.readLock().lock();
            return doGetCurrentSeriesSize();
        } finally {
            headerLock.readLock().unlock();
        }
    }

    protected abstract int doGetCurrentSeriesSize();

    public int getCurrentHead() {
        try {
            headerLock.readLock().lock();
            return doGetCurrentHead();
        } finally {
            headerLock.readLock().unlock();
        }

    }

    protected abstract int doGetCurrentHead();

    public long getMostRecentItemTimestamp() {
        try {
            headerLock.readLock().lock();
            return doGetMostRecentTimestamp();
        } finally {
            headerLock.readLock().unlock();
        }

    }

    protected abstract long doGetMostRecentTimestamp();

    public int getCurrentTail() {
        try {
            headerLock.readLock().lock();
            return doGetCurrentTail();
        } finally {
            headerLock.readLock().unlock();
        }

    }

    protected abstract int doGetCurrentTail();

    public String getDescription() {
        try {
            headerLock.readLock().lock();
            return doGetDescription();
        } finally {
            headerLock.readLock().unlock();
        }

    }

    protected abstract String doGetDescription();

    public byte[] getPropertiesAsByteArray() throws SerializationException {
        try {
            headerLock.readLock().lock();
            return doGetPropertiesAsByteArray();
        } finally {
            headerLock.readLock().unlock();
        }

    }

    protected abstract byte[] doGetPropertiesAsByteArray() throws SerializationException;

    /**
     * @return true, if metadata/properties for this series has changed, and the header needs to be rewritten
     */
    public boolean isPropertiesRewriteRequired() {
        try {
            headerLock.readLock().lock();
            return doIsPropertiesRewriteRequired();
        } finally {
            headerLock.readLock().unlock();
        }

    }

    protected abstract boolean doIsPropertiesRewriteRequired();


    //update header fields to match the filesystem header, should only be called from RoundRobinSerializer
    void updateHeaderFields(int newHeaderLength, int head, int tail, int seriesMaxLength, long latestTimestamp) {
        try {
            headerLock.writeLock().lock();
            doUpdateHeaderFields(newHeaderLength, head, tail, seriesMaxLength, latestTimestamp);
        } finally {
            headerLock.writeLock().unlock();
        }

    }

    abstract void doUpdateHeaderFields(int newHeaderLength, int head, int tail, int seriesMaxLength, long latestTimestamp);


    public Lock readLock() {
        return headerLock.readLock();
    }

    public Lock writeLock() {
        return headerLock.writeLock();
    }
    
}
