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
import com.od.jtimeseries.timeseries.DefaultTimeSeriesItem;
import com.od.jtimeseries.timeseries.IndexedTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.impl.RoundRobinTimeSeries;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.numeric.DoubleNumeric;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

    private static final String VERSION_STRING = "TSVERSION001";
    private static final int VERSION_STRING_LENGTH = 12;
    private static final int CURRENT_HEAD_OFFSET = 20;
    private static final int BYTES_IN_HEADER_START = 72;

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
                byte[] properties = getBytesForProperties(fileHeader);
                int requiredHeaderLength = properties.length + BYTES_IN_HEADER_START;
                fileHeader.calculateHeaderLength(requiredHeaderLength);
        
                //head == -1 is a special convention to indicate the time series is empty
                int head = t.size() == 0 ? -1 : 0;
                fileHeader.setCurrentHead(head);
                fileHeader.setCurrentTail(t.size());
                fileHeader.setSeriesLength(t.getMaxSize());
                fileHeader.setMostRecentItemTimestamp(t.getLatestTimestamp());

                File f = getFile(fileHeader);
                AuditedOutputStream b = null;
                try {
                    b = new AuditedOutputStream(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f))));

                    //BYTES_IN_HEADER_START  (70 bytes)
                    b.writeBytes(VERSION_STRING); //add a version description, to support future versioning
                    b.writeInt(fileHeader.getHeaderLength());  //offset where data will start
                    b.writeInt(fileHeader.getSeriesLength());
                    b.writeInt(fileHeader.getCurrentHead());  //start index in rr structure
                    b.writeInt(fileHeader.getCurrentTail());
                    b.writeLong(fileHeader.getMostRecentItemTimestamp());
                    b.writeInt(properties.length);
                    //the next 32 bytes are currently undefined, left open for future use
                    for ( int loop=0; loop<8; loop++) {
                        b.writeInt(-1);
                    }

                    //Header Properties
                    b.write(properties);
                    byte[] padding = new byte[fileHeader.getHeaderLength() - requiredHeaderLength];
                    b.write(padding);
                    for ( TimeSeriesItem i : t) {
                        b.writeLong(i.getTimestamp());
                        b.writeDouble(i.getValue().doubleValue());
                    }
                } catch (Throwable e) {
                    logMethods.logError("Failed to write time series file " + f);
                    fileErrorCounter.incrementCount();
                    throw new SerializationException("Failed to write time series file " + f, e);
                } finally {
                    if ( b != null) {
                        try {
                            b.flush();
                            b.close();
                        } catch (IOException e) {
                            logMethods.logError("Error closing file " + f, e);
                        }
                    }
                }
            }
        }
    }

    public RoundRobinTimeSeries deserialize(FileHeader fileHeader) throws SerializationException {
        fileReadCounter.incrementCount();
        synchronized (readWriteLock) {
            File f = getFile(fileHeader);
            AuditedInputStream d = null;
            try {
                d = new AuditedInputStream(new DataInputStream(new BufferedInputStream(new FileInputStream(f))));
                readHeader(fileHeader, d);
                return readSeriesData(fileHeader, d);
            } catch (Throwable e) {
                fileErrorCounter.incrementCount();
                throw new SerializationException("Failed to deserialize file " + fileHeader, e);
            } finally {
                if ( d != null) {
                    try {
                        d.close();
                    } catch (IOException e) {
                        logMethods.logError("Error closing file " + f, e);
                    }
                }
            }
        }
    }

    public FileHeader readHeader(File f) throws SerializationException {
        fileHeaderReadCounter.incrementCount();
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
    public void append(FileHeader header, IndexedTimeSeries l) throws SerializationException {
        fileAppendCounter.incrementCount();
        synchronized (readWriteLock) {
            if ( ! shutdown && l.size() > 0) {
                File file = getFile(header);
                checkFileWriteable(file);
                AuditedRandomAccessWriter r = null;
                try {
                    r = new AuditedRandomAccessWriter(new RandomAccessFile(file, "rw"));
                    r.seek(VERSION_STRING_LENGTH);
                    int headerLength = r.readInt();
                    int seriesLength = r.readInt();
                    int currentHead = r.readInt();
                    int currentTail = r.readInt();

                    int currentSize = FileHeader.calculateCurrentSize(seriesLength, currentHead, currentTail);

                    currentHead = Math.max(currentHead, 0); //manage empty file (head==-1)

                    int newSize = Math.min(currentSize + l.size(), seriesLength);
                    int headAdjust = l.size() - (newSize - currentSize);
                    int newHead = (currentHead + headAdjust) % seriesLength;
                    int newTail = (currentTail + l.size()) % seriesLength;

                    r.seek(CURRENT_HEAD_OFFSET);
                    r.writeInt(newHead);
                    r.writeInt(newTail);
                    long newLastTimestamp = l.getItem(l.size() - 1).getTimestamp();
                    r.writeLong(newLastTimestamp);

                    //now update the header with the new values
                    header.setCurrentHead(newHead);
                    header.setCurrentTail(newTail);
                    header.setMostRecentItemTimestamp(newLastTimestamp);

                    r.seek(headerLength + (currentTail * 16));
                    int currentIndex = currentTail;
                    for ( TimeSeriesItem i : l) {
                        if ( currentIndex == seriesLength ) {
                            currentIndex = 0;
                            r.seek(headerLength);
                        }
                        r.writeLong(i.getTimestamp());
                        r.writeDouble(i.getValue().doubleValue());
                        currentIndex ++;
                    }
                } catch ( Throwable e) {
                    fileErrorCounter.incrementCount();
                    throw new SerializationException("Failed to append items to file " + header, e);
                } finally {
                    try {
                        if ( r != null) {
                            r.close();
                        }
                    } catch(IOException e) {
                        logMethods.logError("Error closing file on append " + header, e);
                    }
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
            RoundRobinTimeSeries r = new RoundRobinTimeSeries(fileHeader.getSeriesLength());
            serialize(fileHeader, r);
            return getFile(fileHeader);
        }
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    private void doUpdateHeader(FileHeader fileHeader, File f) throws SerializationException {
        AuditedInputStream d = null;
        try {
            d = new AuditedInputStream(new DataInputStream(new FileInputStream(f)));
            readHeader(fileHeader, d);
        } catch (Throwable e) {
            fileErrorCounter.incrementCount();
            throw new SerializationException("Failed to deserialize header " + fileHeader, e);
        } finally {
            if ( d != null) {
                try {
                    d.close();
                } catch (IOException e) {
                    logMethods.logError("Error closing file on update header " + f, e);
                }
            }
        }
    }

    private void checkFileWriteable(File file) throws SerializationException {
        if ( ! file.canWrite()) {
            String error = "Cannot write to file " + file + ". This file no longer exists or is not writeable";
            logMethods.logError(error);
            throw new SerializationException(error);
        }
    }


    private RoundRobinTimeSeries readSeriesData(FileHeader fileHeader, AuditedInputStream d) throws IOException {
        RoundRobinTimeSeries series = new RoundRobinTimeSeries(fileHeader.getSeriesLength());
        if ( fileHeader.getCurrentHead() != -1) {  //file is not empty
            int itemsRead = 0;
            List<TimeSeriesItem> tailItems = new ArrayList<TimeSeriesItem>();
            if ( fileHeader.getCurrentTail() <= fileHeader.getCurrentHead()) {
                for ( int loop=0; loop < fileHeader.getCurrentTail(); loop++) {
                    tailItems.add(new DefaultTimeSeriesItem(d.readLong(), DoubleNumeric.valueOf(d.readDouble())));
                }
                itemsRead = fileHeader.getCurrentTail();
            }

            int itemsToSkip = fileHeader.getCurrentHead() - itemsRead;
            skipItems(d, itemsToSkip, fileHeader);

            int itemsToRead = fileHeader.getCurrentTail() > fileHeader.getCurrentHead() ?
                    fileHeader.getCurrentTail() - fileHeader.getCurrentHead() :
                    fileHeader.getSeriesLength() - fileHeader.getCurrentHead();

            //here we read the items into a local list first, then add them all at once
            //this is to avoid triggering an insert event for each time series item when we add them to the series
            List<TimeSeriesItem> itemsToAdd = new ArrayList<TimeSeriesItem>();
            for ( int loop=0; loop < itemsToRead; loop++) {
                itemsToAdd.add(new DefaultTimeSeriesItem(d.readLong(), DoubleNumeric.valueOf(d.readDouble())));
            }

            itemsToAdd.addAll(tailItems);
            //quicker to new up a series with the initial items than add each
            series = new RoundRobinTimeSeries(itemsToAdd, fileHeader.getSeriesLength());
        }
        return series;
    }


    private void readHeader(FileHeader fileHeader, AuditedInputStream d) throws IOException {
        readAndCheckVersion(fileHeader, d);
        fileHeader.setHeaderLength(d.readInt());
        fileHeader.setSeriesLength(d.readInt());
        fileHeader.setCurrentHead(d.readInt());
        fileHeader.setCurrentTail(d.readInt());
        fileHeader.setMostRecentItemTimestamp(d.readLong());
        int propertiesLength = d.readInt();
        skipBytes(d, fileHeader, 32); //skip the currently undefined bytes
        fileHeader.setFileProperties(readProperties(fileHeader, d, propertiesLength));

        //skip to end of header section
        skipBytes(d, fileHeader, fileHeader.getHeaderLength() - (propertiesLength + BYTES_IN_HEADER_START));
    }

    private void readAndCheckVersion(FileHeader fileHeader, AuditedInputStream d) throws IOException {
        byte[] bytes = readBytes(fileHeader, d, VERSION_STRING_LENGTH);
        String versionString = new String(bytes, "UTF-8");  //one byte per character, ASCII only
        if ( ! versionString.equals(VERSION_STRING)) {
            throw new IOException("Wrong timeseries file version, expecting version " + VERSION_STRING + " but was " + versionString);
        }
    }

    private void skipItems(AuditedInputStream d, int itemsToSkip, FileHeader fileHeader) throws IOException {
        int bytesToSkip = itemsToSkip * 16;
        skipBytes(d, fileHeader, bytesToSkip);
    }

    private void skipBytes(AuditedInputStream d, FileHeader fileHeader, int bytesToSkip) throws IOException {
        // Read in the bytes
        int offset = 0;
        long numRead = 0;
        while (offset < bytesToSkip
               && (numRead=d.skip(bytesToSkip-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytesToSkip) {
            throw new IOException("Failed to skip " + bytesToSkip + " bytes in file " + fileHeader);
        }
    }

    private Properties readProperties(FileHeader fileHeader, AuditedInputStream d, int propertiesLength) throws IOException {
        byte[] bytes = readBytes(fileHeader, d, propertiesLength);
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Properties p = new Properties();
        p.load(bis);
        return p;
    }

    private byte[] readBytes(FileHeader fileHeader, AuditedInputStream d, int numberOfBytesToRead) throws IOException {
        byte[] bytes = new byte[numberOfBytesToRead];
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=d.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + fileHeader);
        }
        return bytes;
    }

    private byte[] getBytesForProperties(FileHeader f) throws SerializationException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        try {
            f.getFileProperties().store(bos, "TimeSeries");
        } catch (IOException ioe) {
            throw new SerializationException("Failed to serialize properties", ioe);
        }
        return bos.toByteArray();
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

    private class AuditedOutputStream {

        private DataOutputStream dataOutputStream;
        private int bytesWritten = 0;

        public AuditedOutputStream(DataOutputStream dataOutputStream) {
            this.dataOutputStream = dataOutputStream;
        }

        public void writeInt(int v) throws IOException {
            dataOutputStream.writeInt(v);
            bytesWritten += 4;
        }

        public void write(byte[] b) throws IOException {
            dataOutputStream.write(b);
            bytesWritten += b.length;
        }

        public void writeBytes(String s) throws IOException {
            dataOutputStream.writeBytes(s);
            bytesWritten += s.length();
        }

        public void writeLong(long v) throws IOException {
            dataOutputStream.writeLong(v);
            bytesWritten += 8;
        }

        public void writeDouble(double v) throws IOException {
            dataOutputStream.writeDouble(v);
            bytesWritten += 8;
        }

        public void flush() throws IOException {
            dataOutputStream.flush();
        }

        public void close() throws IOException {
            fileBytesWritten.newValue(bytesWritten);
            dataOutputStream.close();
        }
    }

    private class AuditedInputStream {

        private DataInputStream dataInputStream;
        private int bytesRead;

        public AuditedInputStream(DataInputStream dataInputStream) {
            this.dataInputStream = dataInputStream;
        }

        public long readLong() throws IOException {
            bytesRead += 8;
            return dataInputStream.readLong();
        }

        public double readDouble() throws IOException {
            bytesRead += 8;
            return dataInputStream.readDouble();
        }

        public int readInt() throws IOException {
            bytesRead += 4;
            return dataInputStream.readInt();
        }

        public long skip(long n) throws IOException {
            long skipped = dataInputStream.skip(n);
            bytesRead += skipped;   //treat this as a read, probably the underlying input stream does read but discards
            return skipped;
        }

        public int read(byte[] bytes, int offset, int i) throws IOException {
            int read = dataInputStream.read(bytes, offset, i);
            bytesRead += read;
            return read;
        }

        public void close() throws IOException {
            fileBytesRead.newValue(bytesRead);
            dataInputStream.close();
        }
    }

    private class AuditedRandomAccessWriter {

        private RandomAccessFile rw;
        private int bytesRead;
        private int bytesWritten;

        public AuditedRandomAccessWriter(RandomAccessFile rw) {
            this.rw = rw;
        }

        public void seek(int bytes) throws IOException {
            rw.seek(bytes);
        }

        public int readInt() throws IOException {
            bytesRead += 4;
            return rw.readInt();
        }

        public void writeInt(int i) throws IOException {
            bytesWritten += 4;
            rw.writeInt(i);
        }

        public void writeLong(long l) throws IOException {
            bytesWritten += 8;
            rw.writeLong(l);
        }

        public void writeDouble(double v) throws IOException {
            bytesWritten += 8;
            rw.writeDouble(v);
        }

        public void close() throws IOException {
            fileBytesWritten.newValue(bytesWritten);
            fileBytesRead.newValue(bytesRead);
            rw.close();
        }
    }
}
