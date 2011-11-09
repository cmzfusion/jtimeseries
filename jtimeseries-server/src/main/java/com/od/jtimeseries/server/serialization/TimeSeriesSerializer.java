package com.od.jtimeseries.server.serialization;

import com.od.jtimeseries.timeseries.impl.RoundRobinTimeSeries;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 08/11/11
 * Time: 18:45
 */
public interface TimeSeriesSerializer {

    void writeSeries(FileHeader fileHeader, RoundRobinTimeSeries t) throws SerializationException;

    RoundRobinTimeSeries readSeries(FileHeader fileHeader) throws SerializationException;

    FileHeader readHeader(File f) throws SerializationException;

    boolean fileExists(FileHeader fileHeader);

    void readHeader(FileHeader fileHeader) throws SerializationException;

    void migratePath(FileHeader header, String newPath) throws SerializationException;

    void appendToSeries(FileHeader header, RoundRobinTimeSeries l) throws SerializationException;

    File getFile(FileHeader f) throws SerializationException;

    File createFile(FileHeader fileHeader) throws SerializationException;

    File getRootDirectory();

    /**
     * Write to disk any changed properties in header
     */
    void writeHeaderProperties(FileHeader header) throws SerializationException;
}
