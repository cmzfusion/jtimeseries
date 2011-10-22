package com.od.jtimeseries.server.serialization;

import com.od.jtimeseries.timeseries.DefaultTimeSeriesItem;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.impl.RoundRobinTimeSeries;
import com.od.jtimeseries.util.numeric.DoubleNumeric;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 20/10/11
 * Time: 11:43
 */
class SerializerOperations {

    static final int PROPERTIES_OFFSET = 72;
    static final String VERSION_STRING = "TSVERSION001";
    static final int VERSION_STRING_LENGTH = 12;
    static final int MAX_LENGTH_OFFSET = VERSION_STRING_LENGTH + 4;
    static final int HEADER_SECTION_2_OFFSET = 20;

    /**
     *  Write the in memory header information to the series file, c must be at position zero
     */
    void writeHeader(FileHeader fileHeader, byte[] properties, AuditedFileChannel c) throws IOException {
        writeHeaderSectionOne(fileHeader, c);
        writeHeaderSectionTwo(fileHeader, true, properties, c);
    }

    //properties which are not changed in an 'append'
    private void writeHeaderSectionOne(FileHeader fileHeader, AuditedFileChannel c) throws IOException {
        c.position(0);
        ByteBuffer b = ByteBuffer.allocate(HEADER_SECTION_2_OFFSET);
        b.put(VERSION_STRING.getBytes()); //add a version description, to support future versioning
        b.putInt(fileHeader.getHeaderLength());  //offset where data will start
        b.putInt(fileHeader.getSeriesMaxLength());
        c.writeCompletely(b);
    }

    //properties which can be changed in an 'append', the current head and tail, and optionally the properties section
    private void writeHeaderSectionTwo(FileHeader fileHeader, boolean writeProperties, byte[] properties, AuditedFileChannel c) throws IOException {
        c.position(HEADER_SECTION_2_OFFSET);
        int toWrite = writeProperties ? (fileHeader.getHeaderLength() - HEADER_SECTION_2_OFFSET) : 16;
        ByteBuffer b = ByteBuffer.allocate(toWrite);
        b.putInt(fileHeader.getCurrentHead());  //start index in rr structure
        b.putInt(fileHeader.getCurrentTail());
        b.putLong(fileHeader.getMostRecentItemTimestamp());
        if ( writeProperties) {
            b.putInt(properties.length);
            //the next 32 bytes are currently undefined, left open for future use
            for ( int loop=0; loop<8; loop++) {
                b.putInt(-1);
            }

            //Header Properties
            b.put(properties);
            int bytesWritten = properties.length + PROPERTIES_OFFSET;
            int paddingBytes = fileHeader.getHeaderLength() - bytesWritten;
            byte[] padding = new byte[paddingBytes];
            b.put(padding);
        }
        c.writeCompletely(b);
    }

    /**
     *  Write the in memory time series data to the series file, c must be at start of series data offset
     */
    void writeBody(RoundRobinTimeSeries t, AuditedFileChannel c) throws IOException {
        ByteBuffer b = ByteBuffer.allocate(t.size() * 16);
        for ( TimeSeriesItem i : t) {
            writeItem(b, i);
        }
        b.position(0);
        c.writeCompletely(b);
    }

    private void writeItem(ByteBuffer b, TimeSeriesItem i) throws IOException {
        b.putLong(i.getTimestamp());
        b.putDouble(i.getValue().doubleValue());
    }

    /**
     * Read time series items from file body, c must be positioned at start of body section
     */
    RoundRobinTimeSeries readBody(FileHeader fileHeader, AuditedFileChannel c) throws IOException {
        c.position(fileHeader.getHeaderLength());
        ByteBuffer b = ByteBuffer.allocate((int)(c.size() - c.position()));
        c.readCompletely(b);

        RoundRobinTimeSeries series = new RoundRobinTimeSeries(fileHeader.getSeriesMaxLength());
        if ( fileHeader.getCurrentHead() != -1) {  //file is not empty
            int itemsRead = 0;
            List<TimeSeriesItem> tailItems = new ArrayList<TimeSeriesItem>();
            if ( fileHeader.getCurrentTail() <= fileHeader.getCurrentHead()) {
                for ( int loop=0; loop < fileHeader.getCurrentTail(); loop++) {
                    tailItems.add(readItem(b));
                }
                itemsRead = fileHeader.getCurrentTail();
            }

            int itemsToSkip = fileHeader.getCurrentHead() - itemsRead;
            skipItems(b, itemsToSkip);

            int itemsToRead = fileHeader.getCurrentTail() > fileHeader.getCurrentHead() ?
                    fileHeader.getCurrentTail() - fileHeader.getCurrentHead() :
                    fileHeader.getSeriesMaxLength() - fileHeader.getCurrentHead();

            //here we read the items into a local list first, then add them all at once
            //this is to avoid triggering an insert event for each time series item when we add them to the series
            List<TimeSeriesItem> itemsToAdd = new ArrayList<TimeSeriesItem>();
            for ( int loop=0; loop < itemsToRead; loop++) {
                itemsToAdd.add(readItem(b));
            }

            itemsToAdd.addAll(tailItems);
            //quicker to new up a series with the initial items than add each
            series = new RoundRobinTimeSeries(itemsToAdd, fileHeader.getSeriesMaxLength());
        }
        return series;
    }

    private DefaultTimeSeriesItem readItem(ByteBuffer b) throws IOException {
        return new DefaultTimeSeriesItem(b.getLong(), DoubleNumeric.valueOf(b.getDouble()));
    }


    /**
     * Read time series header information, updating fileHeader in memory
     */
    void readHeader(FileHeader fileHeader, AuditedFileChannel c) throws IOException {
        c.position(0);
        ByteBuffer b = ByteBuffer.allocate(MAX_LENGTH_OFFSET);
        c.readCompletely(b);
        b.position(0);

        byte[] versionBytes = new byte[VERSION_STRING_LENGTH];
        b.get(versionBytes);//readBytes(fileHeader, (AuditedInputStream) d, VERSION_STRING_LENGTH);
        String versionString = new String(versionBytes, "UTF-8");  //one byte per character, ASCII only
        if ( ! versionString.equals(VERSION_STRING)) {
            throw new IOException("Wrong timeseries file version, expecting version " + VERSION_STRING + " but was " + versionString);
        }
        int headerLength = b.getInt();

        b = ByteBuffer.allocate(headerLength - MAX_LENGTH_OFFSET);
        c.readCompletely(b);
        b.position(0);

        int seriesMaxLength = b.getInt();
        int currentHead = b.getInt();
        int currentTail = b.getInt();
        long mostRecentTimestamp = b.getLong();
        int propertiesLength = b.getInt();
        b.position(b.position() + 32); //skip the currently undefined bytes

        byte[] propertyBytes = new byte[propertiesLength];
        b.get(propertyBytes);

        //skip to end of header section
        int bytesToSkip = headerLength - (propertiesLength + PROPERTIES_OFFSET);
        b.position(b.position() + bytesToSkip);

        //now update header in memory
        fileHeader.setSeriesProperties(propertyBytes);
        fileHeader.updateHeaderFields(headerLength, currentHead, currentTail, seriesMaxLength, mostRecentTimestamp);
    }

    /**
     * Append items to filesystem, updating fileHeader in memory with new series length, head and tail offset
     */
    void doAppend(FileHeader header, RoundRobinTimeSeries toAppend, boolean writeProperties, byte[] properties, AuditedFileChannel c) throws IOException {
        if ( toAppend.size() > 0) {

            //work out new size, new head and tail offsets
            int currentSize = header.getCurrentSeriesSize();
            int newSize = Math.min(currentSize + toAppend.size(), header.getSeriesMaxLength());

            int currentHead = header.getCurrentHead();
            if ( currentHead == -1 ) {
                currentHead = 0; //handle empty series
            }
            int headAdjust = Math.max(0, toAppend.size() - (newSize - currentSize));
            int newHead = (currentHead + headAdjust) % header.getSeriesMaxLength();
            int newTail = (header.getCurrentTail() + toAppend.size()) % header.getSeriesMaxLength();

            ByteBuffer appendBuffer = ByteBuffer.allocate(toAppend.size() * 16);
            for (TimeSeriesItem i : toAppend) {
                writeItem(appendBuffer, i);
            }

            int bytesToWriteAtEnd = Math.min(appendBuffer.capacity(), (header.getSeriesMaxLength() - header.getCurrentTail()) * 16);
            int bytesToWriteAtStart = appendBuffer.capacity() - bytesToWriteAtEnd;

            byte[] appendArray = appendBuffer.array();
            if ( bytesToWriteAtStart > 0) {
                ByteBuffer startBuffer = ByteBuffer.allocate(bytesToWriteAtStart);
                startBuffer.put(appendArray, appendArray.length - startBuffer.capacity(), startBuffer.capacity());
                c.position(header.getHeaderLength());
                c.writeCompletely(startBuffer);
            }

            if ( bytesToWriteAtEnd > 0 ) {
                ByteBuffer endBuffer = ByteBuffer.allocate(bytesToWriteAtEnd);
                endBuffer.put(appendArray, 0, endBuffer.capacity());
                c.position(header.getHeaderLength() + (header.getCurrentTail() * 16));
                c.writeCompletely(endBuffer);
            }

            long newLastTimestamp = toAppend.getItem(toAppend.size() - 1).getTimestamp();
            header.updateHeaderFields(header.getHeaderLength(), newHead, newTail, header.getSeriesMaxLength(), newLastTimestamp);
        }

        if ( toAppend.size() > 0 || writeProperties ) {
            c.position(HEADER_SECTION_2_OFFSET);
            writeHeaderSectionTwo(header, writeProperties, properties, c);
        }
    }

    private void skipItems(ByteBuffer b, int itemsToSkip) {
        b.position(b.position() + (itemsToSkip * 16));
    }
}
