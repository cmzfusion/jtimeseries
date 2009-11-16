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
 */
public class FileHeader {

    private static final String CONTEXT_PATH_KEY = "CONTEXT_PATH";
    private static final String DESCRIPTION_KEY = "DESCRIPTION";

    private int headerLength = 512;  //default start length for header
    private Properties fileProperties = new Properties();
    private int seriesLength;
    private int currentHead;
    private int currentTail;

    public FileHeader() {
    }

    /**
     * Create a FileHeader which is a copy of the supplied header,
     * performing a clone of the header information
     */
    public FileHeader(FileHeader f) {
        this.headerLength = f.getHeaderLength();
        this.seriesLength = f.getSeriesLength();
        this.currentHead = f.getCurrentHead();
        this.currentTail = f.getCurrentSize();
        this.fileProperties.putAll(f.getFileProperties());
    }

    public FileHeader(String contextPath, String description, int seriesLength) {
        fileProperties.put(CONTEXT_PATH_KEY, contextPath);
        fileProperties.put(DESCRIPTION_KEY, description);
        this.seriesLength = seriesLength;
    }

    public String getContextPath() {
        return fileProperties.getProperty(CONTEXT_PATH_KEY);
    }

    public void setContextPath(String contextPath) {
        fileProperties.setProperty(CONTEXT_PATH_KEY, contextPath);
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

    public int getSeriesLength() {
        return seriesLength;
    }

    public void setSeriesLength(int seriesLength) {
        this.seriesLength = seriesLength;
    }

    public int getCurrentSize() {
        return RoundRobinSerializer.getCurrentSize(seriesLength, currentHead, currentTail);
    }

    public int getCurrentHead() {
        return currentHead;
    }

    public void setCurrentHead(int currentHead) {
        this.currentHead = currentHead;
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
                "contextPath =" + getContextPath() +
                '}';
    }
}
