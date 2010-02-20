/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
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

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-May-2009
 * Time: 07:30:42
 *
 * Represents header information stored in a timeseries file
 * Updated when a series is saved, or loaded
 */
public class FileHeader {

    //these properties will be stored in the serialized series files, careful if you change them!
    private static final String PATH_KEY = "CONTEXT_PATH";
    private static final String DESCRIPTION_KEY = "DESCRIPTION";

    private int headerLength = 512;  //default start length for header
    private Properties fileProperties = new Properties();
    private int seriesLength;
    private int currentHead;
    private int currentTail;
    private long mostRecentItemTimestamp = -1;

    public FileHeader() {
    }

    /**
     * @param seriesLength, maximum size for this series
     */
    public FileHeader(String path, String description, int seriesLength) {
        fileProperties.put(PATH_KEY, path);
        fileProperties.put(DESCRIPTION_KEY, description);
        this.seriesLength = seriesLength;
    }

    public String getPath() {
        return fileProperties.getProperty(PATH_KEY);
    }

    public void setContextPath(String contextPath) {
        fileProperties.setProperty(PATH_KEY, contextPath);
    }

    public Properties getFileProperties() {
        return fileProperties;
    }

    public void setFileProperties(Properties fileProperties) {
        this.fileProperties = fileProperties;
    }

    public int getHeaderLength() {
        return headerLength;
    }

    public void setHeaderLength(int length) {
        this.headerLength = length;
    }

    public void calculateHeaderLength(int requiredLength) {
        while(headerLength < requiredLength) {
            headerLength *= 2;
        }
    }

    /**
     * @return length of round robin series, which is the maximum size this series can obtain
     */
    public int getSeriesLength() {
        return seriesLength;
    }

    public void setSeriesLength(int seriesLength) {
        this.seriesLength = seriesLength;
    }

    public int getCurrentSize() {
        return calculateCurrentSize(seriesLength, currentHead, currentTail);
    }

    public int getCurrentHead() {
        return currentHead;
    }

    public void setCurrentHead(int currentHead) {
        this.currentHead = currentHead;
    }

    public long getMostRecentItemTimestamp() {
        return mostRecentItemTimestamp;
    }

    public void setMostRecentItemTimestamp(long mostRecentItemTimestamp) {
        this.mostRecentItemTimestamp = mostRecentItemTimestamp;
    }

    public int getCurrentTail() {
        return currentTail;
    }

    public void setCurrentTail(int currentTail) {
        this.currentTail = currentTail;
    }

    public String getDescription() {
        return fileProperties.getProperty(DESCRIPTION_KEY);
    }

    public void setDescription(String description) {
        fileProperties.setProperty(DESCRIPTION_KEY, description);
    }

    @Override
    public String toString() {
        return "FileHeader{" +
                "path =" + getPath() +
                '}';
    }

    /**
     * @return current series size, based on current head, tail and length
     */
    public static int calculateCurrentSize(int seriesLength, int currentHead, int currentTail) {
        return currentHead == -1 ? 0 :
                currentTail > currentHead ?
                    currentTail - currentHead :
                    currentTail + (seriesLength - currentHead);
    }
}
