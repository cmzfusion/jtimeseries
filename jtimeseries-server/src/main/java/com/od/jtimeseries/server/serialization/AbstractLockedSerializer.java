package com.od.jtimeseries.server.serialization;

import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.impl.DefaultCounter;
import com.od.jtimeseries.timeseries.impl.RoundRobinTimeSeries;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 08/11/11
 * Time: 18:49
 */
public abstract class AbstractLockedSerializer implements TimeSeriesSerializer {

    protected static Counter fileAppendCounter = DefaultCounter.NULL_COUNTER;
    protected static Counter fileRewriteCounter = DefaultCounter.NULL_COUNTER;
    protected static Counter fileReadCounter = DefaultCounter.NULL_COUNTER;
    protected static Counter fileHeaderReadCounter = DefaultCounter.NULL_COUNTER;
    protected static Counter fileErrorCounter = DefaultCounter.NULL_COUNTER;
    protected static Counter fileBytesWritten = DefaultCounter.NULL_COUNTER;
    protected static Counter fileBytesRead = DefaultCounter.NULL_COUNTER;

    protected final Object readWriteLock = new Object();
    protected volatile boolean shutdown;

    public static void setFileAppendCounter(Counter fileAppendCounter) {
        AbstractLockedSerializer.fileAppendCounter = fileAppendCounter;
    }

    public static void setFileRewriteCounter(Counter fileRewriteCounter) {
        AbstractLockedSerializer.fileRewriteCounter = fileRewriteCounter;
    }

    public static void setFileReadCounter(Counter fileReaderCounter) {
        AbstractLockedSerializer.fileReadCounter = fileReaderCounter;
    }

    public static void setFileHeaderReadCounter(Counter fileHeaderReadCounter) {
        AbstractLockedSerializer.fileHeaderReadCounter = fileHeaderReadCounter;
    }

    public static void setFileErrorCounter(Counter fileErrorCounter) {
        AbstractLockedSerializer.fileErrorCounter = fileErrorCounter;
    }

    public static void setFileBytesWritten(Counter v) {
        AbstractLockedSerializer.fileBytesWritten = v;
    }

    public static void setFileBytesRead(Counter v) {
        AbstractLockedSerializer.fileBytesRead = v;
    }

    /**
     * Serialize the series to the file, and update the fileHeader
     */
    @Override
    public void writeSeries(FileHeader fileHeader, RoundRobinTimeSeries t) throws SerializationException {
        try {
            fileHeader.writeLock().lock();
            synchronized (readWriteLock) {
                if (!shutdown) {
                    doWriteSeries(fileHeader, t);
                }
            }
        } finally {
            fileHeader.writeLock().unlock();
        }

    }

    protected abstract void doWriteSeries(FileHeader fileHeader, RoundRobinTimeSeries t) throws SerializationException;

    @Override
    public RoundRobinTimeSeries readSeries(FileHeader fileHeader) throws SerializationException {
        try {
            fileHeader.writeLock().lock();
            return doReadSeries(fileHeader);
        } finally {
            fileHeader.writeLock().unlock();
        }

    }

    protected abstract RoundRobinTimeSeries doReadSeries(FileHeader fileHeader) throws SerializationException;

    @Override
    public FileHeader readHeader(File f) throws SerializationException {
        synchronized (readWriteLock) {
            return doReadHeader(f);
        }
    }

    protected abstract FileHeader doReadHeader(File f) throws SerializationException;

    @Override
    public boolean fileExists(FileHeader fileHeader) {
        synchronized (readWriteLock) {
            return doFileExists(fileHeader);
        }
    }

    protected abstract boolean doFileExists(FileHeader fileHeader);

    /**
     * Update the fileHeader by reading the file header information from disk
     */
    @Override
    public void readHeader(FileHeader fileHeader) throws SerializationException {
        try {
            fileHeader.writeLock().lock();
            synchronized (readWriteLock) {
                doReadHeader(fileHeader);
            }
        } finally {
            fileHeader.writeLock().unlock();
        }

    }

    protected abstract void doReadHeader(FileHeader fileHeader) throws SerializationException;

    /**
     * Move the series file based on the new path, and update the path in header
     */
    @Override
    public void migratePath(FileHeader fileHeader, String newPath) throws SerializationException {
        try {
           fileHeader.writeLock().lock();
           synchronized (readWriteLock) {
                doMigratePath(fileHeader, newPath);
           }
        } finally {
           fileHeader.writeLock().unlock();
        }

    }

    protected abstract void doMigratePath(FileHeader header, String newPath) throws SerializationException;

    /**
     * Append items to the timeseries file and re-write the file header to reflect new start and end points
     * and any modified header properties
     */
    @Override
    public void appendToSeries(FileHeader header, RoundRobinTimeSeries l) throws SerializationException {
        try {
            header.writeLock().lock();
            synchronized (readWriteLock) {
                doAppendToSeries(header, l);
            }
        } finally {
            header.writeLock().unlock();
        }

    }

    protected abstract void doAppendToSeries(FileHeader header, RoundRobinTimeSeries l) throws SerializationException;

    @Override
    public File getFile(FileHeader f) throws SerializationException {
        try {
            f.readLock().lock();
            synchronized (readWriteLock) {
                return doGetFile(f);
            }
        } finally {
            f.readLock().unlock();
        }

    }

    protected abstract File doGetFile(FileHeader f) throws SerializationException;

    @Override
    public File createFile(FileHeader fileHeader) throws SerializationException {
        try {
            fileHeader.writeLock().lock();
            synchronized (readWriteLock) {
                return doCreateFile(fileHeader);
            }
        } finally {
            fileHeader.writeLock().unlock();
        }

    }

    protected abstract File doCreateFile(FileHeader fileHeader) throws SerializationException;

    @Override
    public void writeHeaderProperties(FileHeader header) throws SerializationException {
        try {
            header.writeLock().lock();
            synchronized (readWriteLock) {
                doWriteHeaderProperties(header);
            }
        } finally {
            header.writeLock().unlock();
        }
    }

    protected abstract void doWriteHeaderProperties(FileHeader header) throws SerializationException;
}
