package com.od.jtimeseries.component.logging;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogMethodsFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 20/01/12
 * Time: 09:59
 * To change this template use File | Settings | File Templates.
 */
public class ApacheLog4jLogMethodsFactory implements LogMethodsFactory {

    private static final Map<Class, LogMethods>  logMethodsMap = new HashMap<Class, LogMethods>();

    public LogMethods getLogMethods(Class c) {
        synchronized (logMethodsMap) {
            LogMethods m = logMethodsMap.get(c);
            if ( m == null) {
                m = new ApacheLogMethods(c);
                logMethodsMap.put(c, m);
            }
            return m;
        }
    }

    public boolean isUsable() {
        return true;
    }

    private static class ApacheLogMethods implements LogMethods {

        Logger logger;

        public ApacheLogMethods(Class c) {
            this.logger = Logger.getLogger(c.getName());
        }

        public void logInfo(String s) {
            logger.info(s);
        }

        public void logDebug(String s) {
            logger.debug(s);
        }

        public void logDebug(String s, Throwable t) {
            logger.debug(s, t);
        }

        public void logWarning(String s) {
            logger.warn(s);
        }

        public void logWarning(String s, Throwable t) {
            logger.warn(s, t);
        }

        public void logError(String s) {
            logger.error(s);
        }

        public void logError(String s, Throwable t) {
            logger.error(s, t);
        }

        public void setLogLevel(LogLevel l) {
            logger.setLevel(getLevel(l));
        }

        private Level getLevel(LogLevel l) {
            switch (l) {
                case ERROR:
                    return Level.ERROR;
                case DEBUG:
                    return Level.DEBUG;
                case WARNING:
                    return Level.WARN;
                default :
                    return Level.INFO;
            }
        }

        public LogLevel getLogLevel() {
            Level l = logger.getLevel();
            if (Level.DEBUG.equals(l)) {
                return LogLevel.DEBUG;
            } else if (Level.WARN.equals(l)) {
                return LogLevel.WARNING;
            } else if (Level.ERROR.equals(l)) {
                return LogLevel.ERROR;
            }  else {
                return LogLevel.INFO;
            }
        }
    }
}