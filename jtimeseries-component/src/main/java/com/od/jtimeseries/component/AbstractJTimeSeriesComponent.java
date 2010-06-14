package com.od.jtimeseries.component;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogMethodsFactory;
import com.od.jtimeseries.util.logging.LogUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Jun-2010
 * Time: 13:28:25
 */
public class AbstractJTimeSeriesComponent {

    protected static LogMethods logMethods = LogUtils.getLogMethods(AbstractJTimeSeriesComponent.class);
    protected static ApplicationContext ctx;

    protected static void initialize(Class logClass) {
        initializeLogging(logClass);
        initializeApplicationContext();
    }

    private static void initializeLogging(Class logClass) {
        //set the hostname as a system property so that it is available on startup to the spring context property placeholder configurer
        System.setProperty("hostname", getHostname(logClass));

        //First read the logging configuration and set this up before other classes are loaded, since other classes in JTimeSeriesServer will
        //initialize their static loggers when they are first loaded, and the logging subsystem needs to be set up first.
        ApplicationContext loggingContext = new ClassPathXmlApplicationContext("logContext.xml");
        configureLogging(loggingContext, logClass);

        logMethods = LogUtils.getLogMethods(logClass);
    }

    protected static LogMethods getLogMethods() {
        return logMethods;
    }

    protected static ApplicationContext getCtx() {
        return ctx;
    }

    private static void configureLogging(ApplicationContext loggingContext, Class logClass) {
        LogMethodsFactory f = (LogMethodsFactory)loggingContext.getBean("logMethodsFactory", LogMethodsFactory.class);
        boolean logMethodsOk = f.isUsable();
        if ( logMethodsOk ) {
            LogUtils.setLogMethodFactory(f);
        } else {
            LogUtils.getLogMethods(logClass).logInfo(
                    "Cannot write to directory for logfile path " + ((File)loggingContext.getBean("logFileDirectory")).getAbsolutePath() +
                    ". Will log to standard out"
            );
        }
    }

    private static String getHostname(Class logClass) {
        String result = "(Unknown Host)";
        try {
            result = InetAddress.getLocalHost().getHostName();
            int lastPeriod = result.indexOf(".");
            if ( lastPeriod > 0 ) {
                result = result.substring(0, lastPeriod - 1);
            }
        } catch (UnknownHostException e) {
            LogUtils.getLogMethods(logClass).logError("Failed to find hostname", e);
        }
        return result;
    }

    protected static void initializeApplicationContext() {
        logMethods.logInfo("Reading Spring Application Context");
        ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    }
}
