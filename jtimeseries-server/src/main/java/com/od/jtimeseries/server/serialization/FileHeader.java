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

import com.od.jtimeseries.util.identifiable.PathParser;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-May-2009
 * Time: 07:30:42
 *
 * Represents header information stored in a timeseries file
 *
 * When a timeseries is saved to disk or loaded, the in memory header information is brought up to date
 * so that it should match the information contained in the file header on disk.
 *
 * The information in memory should always match the information on disk, apart from the seriesProperties
 * which may contain in memory changes which are waiting to be written
 */
public class FileHeader {

    private static final LogMethods logMethods = LogUtils.getLogMethods(FileHeader.class);
    //these properties will be stored in the serialized series files, careful if you change them!
    private static final String PATH_KEY = "CONTEXT_PATH";
    private static final String DESCRIPTION_KEY = "DESCRIPTION";
    private static int MAX_PROPERTY_LENGTH = 1024;

    private volatile int headerLength = 512;  //default start length for header
    private volatile int seriesMaxLength;
    private volatile int currentHead;
    private volatile int currentTail;
    private volatile long mostRecentItemTimestamp = -1;

    private SeriesProperties seriesProperties = new SeriesProperties();

    public FileHeader() {}

    /**
     * @param seriesMaxLength, maximum size for this series
     */
    public FileHeader(String path, String description, int seriesMaxLength) {
        seriesProperties.setProperty(PATH_KEY, path);
        seriesProperties.setProperty(DESCRIPTION_KEY, description);
        this.seriesMaxLength = seriesMaxLength;
    }

    public String getPath() {
        return seriesProperties.getProperty(PATH_KEY);
    }

    public Properties getSeriesProperties() {
       return seriesProperties.getSnapshot();
    }

    void resetSeriesProperties(byte[] serializedProperties) throws IOException {
        seriesProperties.reset(serializedProperties);
    }

    public String setSeriesProperty(String key, String value) {
        return seriesProperties.setProperty(key, value);
    }

    public String getSeriesProperty(String key) {
        return seriesProperties.getProperty(key);
    }

    public int getHeaderLength() {
        return headerLength;
    }

    public int calculateNewHeaderLength(int requiredLength) {
        int h = this.headerLength;
        while(h < requiredLength) {
            h *= 2;
        }
        return h;
    }

    /**
     * @return length of round robin series, which is the maximum size this series can obtain
     */
    public int getSeriesMaxLength() {
        return seriesMaxLength;
    }

    public int getCurrentSeriesSize() {
        return currentHead == -1 ? 0 :
                currentTail > currentHead ?
                    currentTail - currentHead :
                    currentTail + (seriesMaxLength - currentHead);
    }

    public int getCurrentHead() {
        return currentHead;
    }

    public long getMostRecentItemTimestamp() {
        return mostRecentItemTimestamp;
    }

    public int getCurrentTail() {
        return currentTail;
    }

    public String getDescription() {
        return seriesProperties.getProperty(DESCRIPTION_KEY);
    }

    public byte[] getPropertiesAsByteArray() throws SerializationException {
        return seriesProperties.getPropertiesAsByteArray();
    }

    /**
     * @return true, if metadata/properties for this series has changed, and the header needs to be rewritten
     */
    public boolean isPropertiesRewriteRequired() {
        return seriesProperties.isChanged();
    }

    @Override
    public String toString() {
        return "FileHeader{" +
                "path =" + getPath() +
                '}';
    }

    //this is being set from Spring applicationContext.xml
    public static void setMaxPropertyLength(String propertyLength) {
        MAX_PROPERTY_LENGTH = Integer.valueOf(propertyLength);
    }

    public String getId() {
        return PathParser.lastNode(getPath());
    }

    //update header fields to match the filesystem header, should only be called from RoundRobinSerializer
    void updateHeaderFields(int newHeaderLength, int head, int tail, int seriesMaxLength, long latestTimestamp) {
        this.headerLength = newHeaderLength;
        this.currentHead = head;
        this.currentTail = tail;
        this.seriesMaxLength = seriesMaxLength;
        this.mostRecentItemTimestamp = latestTimestamp;
    }

    /**
     * Properties class which keeps a changed flag.
     * Can be reset with the current set of properties loaded from file header on disk
     */
    private class SeriesProperties {

        private Properties wrappedProperties = new Properties();

        //true, if the series properties in memory have changed, and the header properties information needs to be rewritten
        private boolean seriesPropertiesChanged;

        public synchronized void reset(byte[] serializedProperties) throws IOException {
            wrappedProperties.clear();
            Properties p = getProperties(serializedProperties);
            wrappedProperties.putAll(p);
            seriesPropertiesChanged = false;
        }

        private Properties getProperties(byte[] bytes) throws IOException {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            Properties p = new Properties();
            p.load(bis);
            return p;
        }

        public synchronized String setProperty(String key, String value) {
            String result = null;
            if ( key.length() > MAX_PROPERTY_LENGTH || value.length() > MAX_PROPERTY_LENGTH) {
                logMethods.logWarning("Cannot persist timeseries property with key or value length > " + MAX_PROPERTY_LENGTH +
                    ", start of key " + key.substring(0, Math.min(124, key.length())));
            } else {
                result = (String) wrappedProperties.setProperty(key, value);
                seriesPropertiesChanged = true;
            }
            return result;
        }

        public synchronized boolean isChanged() {
            return seriesPropertiesChanged;
        }

        public synchronized String getProperty(String key) {
            return wrappedProperties.getProperty(key);
        }

        public synchronized Properties getSnapshot() {
            return new Properties(wrappedProperties);
        }

        public synchronized byte[] getPropertiesAsByteArray() throws SerializationException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            try {
                wrappedProperties.store(bos, "TimeSeries");
            } catch (IOException ioe) {
                throw new SerializationException("Failed to serialize properties", ioe);
            }
            return bos.toByteArray();
        }
    }
}
