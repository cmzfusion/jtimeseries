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
package com.od.jtimeseries.component.logging;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogMethodsFactory;
import com.od.jtimeseries.util.logging.StandardOutputLogMethods;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 25-Jun-2009
 * Time: 11:22:07
 */
public class JavaUtilLoggingLogMethodsFactory implements LogMethodsFactory{

    private final Map<String, LogMethods> logMethodsByName =
        Collections.synchronizedMap(new HashMap<String, LogMethods>());

    private File logFile;
    private int logFileSizeBytes;
    private int logFileCount;
    private FileHandler fileHandler;
    private Level logLevel;
    private StandardOutputLogMethods.LogLevel timeseriesLibraryLogLevel;

    public JavaUtilLoggingLogMethodsFactory(File logFile, String logLevel, int logFileSizeBytes, int logFileCount) {
        this.logFile = logFile;
        this.logFileSizeBytes = logFileSizeBytes;
        this.logFileCount = logFileCount;
        setLevel(logLevel);
        createHandler();
    }

    public boolean isUsable() {
        return ( logFile.getParentFile().canWrite() && ! logFile.isDirectory() && (! logFile.exists() || logFile.canWrite()));
    }

    public File getLogFile() {
        return logFile;
    }

    private void setLevel(String logLevel) {
        setJavaUtilLoggingLogLevel(logLevel);
        setTimeseriesLibraryLogLevel(logLevel);
    }

    private void setTimeseriesLibraryLogLevel(String logLevel) {
        timeseriesLibraryLogLevel = LogMethods.LogLevel.getLogLevel(logLevel);
    }

    private void setJavaUtilLoggingLogLevel(String logLevel) {
        //support 'debug' as a log level - translate to the silly FINEST level
        if ( "DEBUG".equalsIgnoreCase(logLevel)) {
            logLevel = "FINEST";
        }

        try {
            this.logLevel = Level.parse(logLevel);
        } catch ( IllegalArgumentException iae) {
            this.logLevel = Level.INFO;
            System.err.println(
                "Failed to set log level " + logLevel + " this log level is not available. Will use INFO level. " +
                "See the log levels in java.util.logging.Level for the available levels, which include INFO, SEVERE, WARNING, ALL"
            );
        }
    }

    private void createHandler() {
        if ( logFile != null ) {
            try {
                fileHandler = new FileHandler(
                    logFile.getAbsolutePath(),
                    logFileSizeBytes,
                    logFileCount,
                    true
                );
                fileHandler.setFormatter(new TimeSeriesLogFormatter());
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to create FileHandler for path " + logFile.getAbsolutePath());
            }
        }
    }

    public LogMethods getLogMethods(Class c) {
        synchronized (logMethodsByName) {
            LogMethods result = logMethodsByName.get(c.getName());
            if ( result == null) {
                result = new JavaUtilLoggingLogMethod(c.getName());
                logMethodsByName.put(c.getName(), result);
            }
            return result;
        }
    }

    private class JavaUtilLoggingLogMethod implements LogMethods {

        private Logger logger;
        private LogMethods standardOutMethods = new StandardOutputLogMethods();

        public JavaUtilLoggingLogMethod(String className) {
            logger = Logger.getLogger(className);
            logger.setLevel(logLevel);
            if ( fileHandler != null) {
                logger.addHandler(fileHandler);
                logger.setUseParentHandlers(false);
            }
            setLogLevel(timeseriesLibraryLogLevel);
        }

        public void info(String s) {
            s = addThreadDetails(s);
            standardOutMethods.info(s);
            logger.log(Level.INFO, s);
        }

        public boolean isInfoEnabled() {
            return logger.isLoggable(Level.INFO);
        }

        public void debug(String s) {
            s = addThreadDetails(s);
            standardOutMethods.debug(s);
            logger.log(Level.FINEST, s);
        }

        public boolean isDebugEnabled() {
            return logger.isLoggable(Level.FINEST);
        }

        public void debug(String s, Throwable t) {
            s = addThreadDetails(s);
            standardOutMethods.debug(s, t);
            logger.log(Level.FINEST, s, t);
        }

        public void warn(String s) {
            s = addThreadDetails(s);
            standardOutMethods.warn(s);
            logger.log(Level.WARNING, s);
        }

        public boolean isWarnEnabled() {
            return logger.isLoggable(Level.WARNING);
        }

        public void logWarning(String s, Throwable t) {
            s = addThreadDetails(s);
            standardOutMethods.logWarning(s, t);
            logger.log(Level.WARNING, s, t);
        }

        public void error(String s) {
            s = addThreadDetails(s);
            standardOutMethods.error(s);
            logger.log(Level.SEVERE, s);
        }

        public void error(String s, Throwable t) {
            s = addThreadDetails(s);
            standardOutMethods.error(s, t);
            logger.log(Level.SEVERE, s, t);
        }

        public void setLogLevel(LogLevel l) {
            standardOutMethods.setLogLevel(l);
        }

        public LogLevel getLogLevel() {
            return standardOutMethods.getLogLevel();
        }
        
        private String addThreadDetails(String s) {
            return "{" + Thread.currentThread().getName() + "} " + " " + s;
        }
    }

    public static class TimeSeriesLogFormatter extends Formatter {

        Date dat = new Date();
        private final static String format = "{0,date} {0,time}";
        private MessageFormat formatter;

        private Object args[] = new Object[1];
        private String lineSeparator = System.getProperty("line.separator");

        /**
         * Format the given LogRecord.
         *
         * @param record the log record to be formatted.
         * @return a formatted log record
         */
        public synchronized String format(LogRecord record) {
            StringBuffer sb = new StringBuffer();
            // Minimize memory allocations here.
            dat.setTime(record.getMillis());
            args[0] = dat;
            StringBuffer text = new StringBuffer();
            if (formatter == null) {
                formatter = new MessageFormat(format);
            }
            formatter.format(args, text, null);
            sb.append(text);
            sb.append(" ");

            String loggerName = record.getLoggerName();
            int index = loggerName.lastIndexOf(".");
            if ( index != -1 && loggerName.length() > index) {
                loggerName = loggerName.substring(index + 1);
            }
            sb.append(loggerName);
            sb.append(" ");

            String message = formatMessage(record);
            sb.append(record.getLevel().getLocalizedName());
            sb.append(": ");
            sb.append(message);
            sb.append(lineSeparator);
            if (record.getThrown() != null) {
                try {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    record.getThrown().printStackTrace(pw);
                    pw.close();
                    sb.append(sw.toString());
                } catch (Exception ex) {
                }
            }
            return sb.toString();
        }

    }
}
