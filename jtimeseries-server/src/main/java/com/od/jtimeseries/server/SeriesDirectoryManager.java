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
package com.od.jtimeseries.server;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.serialization.FileHeader;
import com.od.jtimeseries.server.serialization.RoundRobinSerializer;
import com.od.jtimeseries.server.serialization.SerializationException;
import com.od.jtimeseries.server.util.FileReaper;
import com.od.jtimeseries.server.util.TimeSeriesServerConfig;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.time.Time;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 20-May-2009
 * Time: 22:15:31
 * To change this template use File | Settings | File Templates.
 */
public class SeriesDirectoryManager {

    private LogMethods logMethods = LogUtils.getLogMethods(SeriesDirectoryManager.class);
    private File seriesDirectory;
    private RoundRobinSerializer roundRobinSerializer;
    private TimeSeriesContext rootContext;
    private String seriesFileSuffix;
    private int maxFileCount;
    private int maxDiskSpaceForSeriesMb;
    private int maxSeriesFileAgeDays;
    private int loadCount;
    private FileReaper reaper;

    public SeriesDirectoryManager(File seriesDirectory, RoundRobinSerializer roundRobinSerializer, TimeSeriesContext rootContext, String seriesFileSuffix, int maxFileCount, int maxDiskSpaceForSeriesMb, int maxSeriesFileAgeDays) {
        this.seriesDirectory = seriesDirectory;
        this.roundRobinSerializer = roundRobinSerializer;
        this.rootContext = rootContext;
        this.seriesFileSuffix = seriesFileSuffix;
        this.maxFileCount = maxFileCount;
        this.maxDiskSpaceForSeriesMb = maxDiskSpaceForSeriesMb;
        this.maxSeriesFileAgeDays = maxSeriesFileAgeDays;

        createFileReaper(seriesDirectory, seriesFileSuffix);
    }

    private void createFileReaper(File seriesDirectory, String seriesFileSuffix) {
        this.reaper = new FileReaper(
                "Timeseries File Reaper",
                seriesDirectory,
                ".*" + seriesFileSuffix,
                maxFileCount,
                maxDiskSpaceForSeriesMb * 1000000,
                Time.days(maxSeriesFileAgeDays).getLengthInMillis()
        );
    }

    public void loadExistingSeries() {
        File[] candidateFiles = getCandidateSeriesFiles();
        logMethods.logInfo("Found " + candidateFiles.length + " possible timeseries files, about to commence loading..");
        long startTime = System.currentTimeMillis();
        for (File f : candidateFiles) {
            if ( ! f.isDirectory()) {
                if ( f.canRead() ) {
                    loadTimeSeries(f);
                } else {
                    logMethods.logInfo("Cannot read time series file " + f + " - will skip loading this one");
                }
            }

        }
        long loadTime = System.currentTimeMillis() - startTime;
        logMethods.logInfo("Loaded " + loadCount + " series in " + loadTime + " milliseconds");

    }

    private void loadTimeSeries(File f) {
        try {
            FileHeader h = roundRobinSerializer.readHeader(f);
            logMethods.logInfo("Setting up series " + h.getContextPath() + " with current size " + h.getCurrentSize());

            //the type of time series which will be created depends on the TimeSeriesFactory set on the context
            //we are expecting FilesystemTimeSeries, but it may be something else
            rootContext.getOrCreateTimeSeriesForPath(h.getContextPath(), h.getDescription());
            loadCount++;
        } catch (SerializationException e) {
            logMethods.logError("Failed to read series file " + f + ", this series is possibly corrupted, and will not be loaded, please remove it", e);
        }
    }

    private File[] getCandidateSeriesFiles() {
        return seriesDirectory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(seriesFileSuffix);
            }
        });
    }

    public void removeOldTimeseriesFiles() {
        logMethods.logInfo("Removing old timeseries files");
        reaper.reap();
    }
}
