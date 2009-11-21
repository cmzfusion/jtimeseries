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
package com.od.jtimeseries.server.util;

import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 29-May-2008
 * Time: 14:30:32
 *
 * Monitors a directory for file with a name matching a supplied pattern.
 * Orders files by modified time and deletes them oldest first if:
 *  - an optional max number of files is exceeded
 *  - an optional max cumulative size in bytes for all files is exceeded
 *  - an optional maximum age is exceeded
 */
public class FileReaper {

    private static final LogMethods log = LogUtils.getLogMethods(FileReaper.class);
    private static AtomicInteger id = new AtomicInteger();

    private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    private String name;
    private File parentDirectory;
    private int maxFileCount;
    private int maxCumulativeSize;
    private long maxAgeInMillis;
    private java.util.regex.Pattern pattern;

    /**
     * @param name - name to describe this reaper instance
     * @param parentDirectory - directory to look in for files to reap
     * @param fileSearchRegExp - seach expression to identify files to delete
     * @param maxFileCount - maximum number of files to keep, zero or -1 = no maximum
     * @param maxCumulativeSize - maximum cumulative size in bytes of files to keep, zero or -1 = no maximum
     * @param maxAgeInMillis - maximum age of file in millis, zero or -1 = no maximum
     */
    public FileReaper(String name, File parentDirectory, String fileSearchRegExp, int maxFileCount, int maxCumulativeSize, long maxAgeInMillis) {
        this.name = name;
        this.parentDirectory = parentDirectory;
        this.maxFileCount = maxFileCount;
        this.maxCumulativeSize = maxCumulativeSize;
        this.maxAgeInMillis = maxAgeInMillis;
        pattern = java.util.regex.Pattern.compile(fileSearchRegExp);
        nameTimerThread();
    }

    private void nameTimerThread() {
        timer.execute(new Runnable() {
            public void run() {
                Thread.currentThread().setName("FileReaperTimer " + id.getAndIncrement());
            }
        });
    }

    public void startReaper(int reapFrequencyMillis) {
        timer.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        reap();
                    }
                },
                reapFrequencyMillis,
                reapFrequencyMillis,
                TimeUnit.MILLISECONDS
        );
    }

    public void stopReaper() {
        timer.shutdown();
    }

    /**
     * mow those pesky files
     */
    public void reap() {
        if ( parentDirectory.canRead()) {

            File[] files = getFilesByModifiedDate();

            long currentFileLength = 0, cumulativeSize = 0, reapCount = 0, reapFail = 0, sizeDeletes = 0, ageDeletes = 0, countDeletes = 0, matches = 0;
            File currentFile;
            for ( int fileCount=0; fileCount < files.length; fileCount ++ ) {
                currentFile = files[fileCount];
                if ( pattern.matcher(currentFile.getName()).matches()) {
                    matches++;
                    log.logDebug("FileReaper " + name + "checking matching file " + currentFile);
                    currentFileLength = currentFile.length();
                    cumulativeSize += currentFileLength;
                    ReaperAction a = shouldDeleteFile(currentFile, fileCount, cumulativeSize);
                    if ( a != ReaperAction.NO_DELETE ) {
                        if ( currentFile.delete() ) {
                            reapCount++;
                            switch(a) {
                                case SIZE_DELETE : sizeDeletes ++; break;
                                case AGE_DELETE : ageDeletes ++; break;
                                case COUNT_DELETE : countDeletes ++ ; break;
                            }
                            //since we deleted the file, remove it from the cumulative size otherwise others
                            //may get deleted unnecessarily.
                            cumulativeSize -= currentFileLength;
                        } else {
                            reapFail++;
                        }

                    }
                }
            }

            log.logInfo("FileReaper " + name + " deleted " + reapCount + " files out of " + matches + " matching candidates, failed to delete " + reapFail);
            if ( reapCount > 0 )  {
                log.logInfo("FileReaper " + name + " deleted " + sizeDeletes + " files due to exceeding cumulative file size " + maxCumulativeSize + ", " + ageDeletes + " due to timestamp more than " + maxAgeInMillis + " millis old, and " + countDeletes + " due to the max count of " + maxFileCount + " being exceeded");
            }
        }
    }

    private File[] getFilesByModifiedDate() {
        File[] files = parentDirectory.listFiles();

        //sort by last modified date
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return ((Long)f2.lastModified()).compareTo(f1.lastModified());
            }
        });
        return files;
    }

    private ReaperAction shouldDeleteFile(File file, int currentFileIndex, long cumulativeSize) {
        ReaperAction result = ReaperAction.NO_DELETE;
        result = (isDeleteByCumulativeFileSize() && cumulativeSize > maxCumulativeSize) ? ReaperAction.SIZE_DELETE : result;
        result = isDeleteByMaxCount() && currentFileIndex >= maxFileCount ? ReaperAction.COUNT_DELETE : result;
        result = isDeleteByMaxAge() && System.currentTimeMillis() - file.lastModified() > maxAgeInMillis ? ReaperAction.AGE_DELETE : result;
        return result;
    }

    private boolean isDeleteByCumulativeFileSize() {
        return maxCumulativeSize > 0;
    }

    private boolean isDeleteByMaxAge() {
        return maxAgeInMillis > 0;
    }

    private boolean isDeleteByMaxCount() {
        return maxFileCount > 0;
    }

    private static enum ReaperAction {
        NO_DELETE,
        SIZE_DELETE,
        AGE_DELETE,
        COUNT_DELETE
    }

}
