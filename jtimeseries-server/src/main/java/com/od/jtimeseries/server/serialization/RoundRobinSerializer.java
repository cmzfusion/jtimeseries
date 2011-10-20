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
package com.od.jtimeseries.server.serialization;

import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.source.impl.DefaultCounter;
import com.od.jtimeseries.source.impl.DefaultValueRecorder;
import com.od.jtimeseries.timeseries.IndexedTimeSeries;
import com.od.jtimeseries.timeseries.impl.RoundRobinTimeSeries;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;

import java.io.*;
import java.net.URLEncoder;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 17-May-2009
 * Time: 22:14:28
 *
 * Handles reading and writing time series data to/from the filesystem
 *
 * There is currently a single lock for both read and write operations
 * This does effectively hamstring performance by limiting both read/write to a single thread, but performance
 * is 'sufficient' at the moment. (our current production server is managing 12000 time series with 30s intervals and
 * there are no problems currently). Memory caching and delayed write for time series appends does help here.
 * Development time allowing it would be better to allow concurrent reads and concurrent writes on different files,
 * or perhaps make use of ReentrantReadWriteLock
 */
public class RoundRobinSerializer {

    private static final LogMethods logMethods = LogUtils.getLogMethods(RoundRobinSerializer.class);    

    //for testing, where we create dozens of serializers
    private static boolean shutdownHandlingDisabled;

    private static Counter fileAppendCounter = DefaultCounter.NULL_COUNTER;
    private static Counter fileRewriteCounter = DefaultCounter.NULL_COUNTER;
    private static Counter fileReadCounter = DefaultCounter.NULL_COUNTER;
    private static Counter fileHeaderReadCounter = DefaultCounter.NULL_COUNTER;
    private static Counter fileErrorCounter = DefaultCounter.NULL_COUNTER;
    private static ValueRecorder fileBytesWritten = DefaultValueRecorder.NULL_VALUE_RECORDER;
    private static ValueRecorder fileBytesRead = DefaultValueRecorder.NULL_VALUE_RECORDER;

    private final File rootDirectory;
    private final String timeSeriesFileSuffix;
    private final Object readWriteLock = new Object();
    private volatile boolean shutdown;
    private SerializerOperations serializerOperations = new SerializerOperations();

    public RoundRobinSerializer(File rootDirectory, String timeSeriesFileSuffix) {
        this.rootDirectory = rootDirectory;
        this.timeSeriesFileSuffix = timeSeriesFileSuffix;
        checkRootDirectory(rootDirectory);
        addShutdownHook();
    }

    private void checkRootDirectory(File rootDirectory) {
        if ( ! rootDirectory.canWrite()) {
            logMethods.logError("Timeseries Directory at " + rootDirectory.getAbsolutePath() + " does not exist or is not writable, cannot start server");
            throw new RuntimeException("Timeseries Directory at " + rootDirectory.getAbsolutePath() + " does not exist or is not writable, cannot start server");
        }
    }

    /**
     * Serialize the series to the file, and update the fileHeader
     */
    public void serialize(FileHeader fileHeader, RoundRobinTimeSeries t) throws SerializationException {
        fileRewriteCounter.incrementCount();
        synchronized (readWriteLock) {
            if ( ! shutdown ) {
                byte[] properties = fileHeader.getPropertiesAsByteArray();
                int headerBytesToWrite = properties.length + SerializerOperations.BYTES_IN_HEADER_START;
                int newHeaderLength = fileHeader.calculateNewHeaderLength(headerBytesToWrite);

                //head == -1 is a special convention to indicate the time series is empty
                int head = t.size() == 0 ? -1 : 0;
                int tail = t.size();
                fileHeader.updateHeaderFields(newHeaderLength, head, tail, t.getMaxSize(), t.getLatestTimestamp());

                File f = getFile(fileHeader);
                AuditedFileChannel b = null;
                RandomAccessFile r = null;
                try {
                    r = new RandomAccessFile(f, "rw");
                    b = new AuditedFileChannel(
                        r.getChannel(),
                        fileBytesWritten,
                        fileBytesRead
                    );

                    serializerOperations.writeHeader(fileHeader, properties, b);
                    serializerOperations.writeBody(t, b);
                } catch (Throwable e) {
                    logMethods.logError("Failed to write time series file " + f);
                    fileErrorCounter.incrementCount();
                    throw new SerializationException("Failed to write time series file " + f, e);
                } finally {
                    flushAndClose(f, b, r);
                }
            }
        }
    }

    public RoundRobinTimeSeries deserialize(FileHeader fileHeader) throws SerializationException {
        fileReadCounter.incrementCount();
        synchronized (readWriteLock) {
            File f = getFile(fileHeader);
            RandomAccessFile r = null;
            AuditedFileChannel c = null;
            try {
                r = new RandomAccessFile(f, "r");
                c = new AuditedFileChannel(r.getChannel(), fileBytesWritten, fileBytesRead);
                serializerOperations.readHeader(fileHeader, c);
                return serializerOperations.readBody(fileHeader, c);
            } catch (Throwable e) {
                fileErrorCounter.incrementCount();
                throw new SerializationException("Failed to deserialize file " + fileHeader, e);
            } finally {
                flushAndClose(f, c, r);
            }
        }
    }


    public FileHeader readHeader(File f) throws SerializationException {
        synchronized (readWriteLock) {
            FileHeader h = new FileHeader();
            doUpdateHeader(h, f);
            return h;
        }
    }

    public boolean fileExists(FileHeader fileHeader) {
        synchronized (readWriteLock) {
            boolean result = false;
            File f = null;
            try {
                f = getFile(fileHeader);
                result = f.exists();
            } catch (SerializationException e) {
                logMethods.logError("Error checking file exists " + f, e);
            }
            return result;
        }
    }

    /**
     * Update the fileHeader by reading the file header information from disk
     */
    public void updateHeader(FileHeader fileHeader) throws SerializationException {
        synchronized (readWriteLock) {
            File f = getFile(fileHeader);
            if ( ! f.exists()) {
                throw new SerializationException("File for header " + fileHeader + " does not exist");
            } else if ( ! f.canRead()) {
                throw new SerializationException("File for header " + fileHeader + " is not readable");
            }
            doUpdateHeader(fileHeader, f);
        }
    }

    /**
     * Append items to the timeseries file and update the header
     */
    public void append(FileHeader header, RoundRobinTimeSeries l) throws SerializationException {
        fileAppendCounter.incrementCount();
        synchronized (readWriteLock) {
            if ( ! shutdown && l.size() > 0) {
                File file = getFile(header);
                checkFileWriteable(file);
                RandomAccessFile r = null;
                AuditedFileChannel c = null;
                try {
                    r = new RandomAccessFile(file, "rw");
                    c = new AuditedFileChannel(r.getChannel(), fileBytesWritten, fileBytesRead);
                    serializerOperations.doAppend(header, l, c);
                } catch ( Throwable e) {
                    fileErrorCounter.incrementCount();
                    throw new SerializationException("Failed to append items to file " + header, e);
                } finally {
                    flushAndClose(file, c, r);
                }
            }
        }
    }

    public File getFile(FileHeader f) throws SerializationException {
        synchronized (readWriteLock) {
            if ( f.getPath() == null) {
                throw new SerializationException("Cannot get File for FileHeader with null context path");
            }

            try {
                String fileName = URLEncoder.encode(f.getPath(), "UTF-8") + timeSeriesFileSuffix;
                return new File(rootDirectory, fileName);
            } catch (UnsupportedEncodingException e) {
                throw new SerializationException("Failed to encode file name", e);
            }
        }
    }

    public File createFile(FileHeader fileHeader) throws SerializationException {
        synchronized (readWriteLock) {
            RoundRobinTimeSeries r = new RoundRobinTimeSeries(fileHeader.getSeriesMaxLength());
            serialize(fileHeader, r);
            return getFile(fileHeader);
        }
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    private void doUpdateHeader(FileHeader fileHeader, File f) throws SerializationException {
        fileHeaderReadCounter.incrementCount();

        RandomAccessFile r = null;
        AuditedFileChannel c = null;
        try {
            r = new RandomAccessFile(f, "r");
            c = new AuditedFileChannel(r.getChannel(), fileBytesWritten, fileBytesRead);
            serializerOperations.readHeader(fileHeader, c);
        } catch (Throwable e) {
            fileErrorCounter.incrementCount();
            throw new SerializationException("Failed to deserialize header " + fileHeader, e);
        } finally {
            flushAndClose(f, c, r);
        }
    }

    private void checkFileWriteable(File file) throws SerializationException {
        if ( ! file.canWrite()) {
            String error = "Cannot write to file " + file + ". This file no longer exists or is not writeable";
            logMethods.logError(error);
            throw new SerializationException(error);
        }
    }

    //this should ensure no files are corrupted on linux shutdown - although it most likely won't work on Windows, becuase of lack
    //of support for SIGTERM etc. Still, I have yet to see a corrupted file on either!
    private void addShutdownHook() {
        if ( ! shutdownHandlingDisabled) {
            Runtime.getRuntime().addShutdownHook( new Thread() {
                public void run() {
                    logMethods.logInfo("Shutdown Starting");
                    shutdownNow();
                    try {
                        Thread.sleep(250); //just in the hope that 250ms is enough for that log statement to make it into the logs
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logMethods.logInfo("Shutdown complete");

                }
            });
        }
    }

    private void shutdownNow() {
        synchronized (readWriteLock) {
            shutdown = true;
        }
    }

    /**
     * Safely close a RandomAccessFile and associated channel, forcing channel changes to disk
     */
    private void flushAndClose(File f, AuditedFileChannel c, RandomAccessFile r) {
        if ( c != null) {
            try {
                c.forceAndClose();
            } catch (IOException e) {
                logMethods.logError("Error closing file channel " + f, e);
            }
        }
        if ( r != null ) {
            try {
                r.close();
            } catch (IOException e) {
                logMethods.logError("Error closing file " + f, e);
            }
        }
    }

    public static void setShutdownHandlingDisabled(boolean shutdownHandlingDisabled) {
        RoundRobinSerializer.shutdownHandlingDisabled = shutdownHandlingDisabled;
    }

    public static void setFileAppendCounter(Counter fileAppendCounter) {
        RoundRobinSerializer.fileAppendCounter = fileAppendCounter;
    }

    public static void setFileRewriteCounter(Counter fileRewriteCounter) {
        RoundRobinSerializer.fileRewriteCounter = fileRewriteCounter;
    }

    public static void setFileReadCounter(Counter fileReaderCounter) {
        RoundRobinSerializer.fileReadCounter = fileReaderCounter;
    }

    public static void setFileHeaderReadCounter(Counter fileHeaderReadCounter) {
        RoundRobinSerializer.fileHeaderReadCounter = fileHeaderReadCounter;
    }

    public static void setFileErrorCounter(Counter fileErrorCounter) {
        RoundRobinSerializer.fileErrorCounter = fileErrorCounter;
    }

    public static void setFileBytesWritten(ValueRecorder v) {
        RoundRobinSerializer.fileBytesWritten = v;
    }

    public static void setFileBytesRead(ValueRecorder v) {
        RoundRobinSerializer.fileBytesRead = v;
    }

}
