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
package com.od.jtimeseries.util;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 10/01/11
 * Time: 08:59
 */

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 14-Dec-2010
 * Time: 18:31:25
 *
 * An easy way to create Executors which name the threads, and configure other aspects of the threads created.
 * For some reason the factory methods on Executors don't give you this option
 */
public class NamedExecutors {

    private static LogMethods logMethods = LogUtils.getLogMethods(NamedExecutors.class);

    private static class LogOnErrorThreadGroup extends ThreadGroup {
        public LogOnErrorThreadGroup() {
            super("JTimeSeriesLoggingThreadGroup");
        }

        public void uncaughtException(Thread t, Throwable e) {
            super.uncaughtException(t, e);
            if ( ! (e instanceof ThreadDeath)) {
                logMethods.error("An Uncaught Exception Shut Down Thread " + t.getName(), e);
            }
        }
    }
    private static ThreadGroup threadDeathLoggingThreadGroup = new LogOnErrorThreadGroup();

    public static ThreadConfigurer DAEMON_THREAD_CONFIGURER = new ThreadConfigurer() {
        public void configureNewThread(Thread t) {
            t.setDaemon(true);
        }
    };

    public static ThreadConfigurer DEFAULT_THREAD_CONFIGURER = new ThreadConfigurer() {
        public void configureNewThread(Thread t) {
        }
    };

    public static ExecutorService newFixedThreadPool(String executorName, int nThreads) {
        return Executors.newFixedThreadPool(nThreads, new NamedThreadFactory(executorName + "-FixedThreadPool(" + nThreads + ")", DEFAULT_THREAD_CONFIGURER));
    }

    public static ExecutorService newSingleThreadExecutor(String executorName) {
        return Executors.newSingleThreadExecutor(new NamedThreadFactory(executorName + "-SingleThreadExecutor", DEFAULT_THREAD_CONFIGURER));
    }

    public static ExecutorService newCachedThreadPool(String executorName) {
        return Executors.newCachedThreadPool(new NamedThreadFactory(executorName + "-CachedThreadPool", DEFAULT_THREAD_CONFIGURER));
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String executorName) {
        return Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(executorName + "-SingleThreadScheduledExecutor", DEFAULT_THREAD_CONFIGURER));
    }

    public static ScheduledExecutorService newScheduledThreadPool(String executorName, int corePoolSize, ThreadConfigurer threadConfigurer) {
        return Executors.newScheduledThreadPool(corePoolSize, new NamedThreadFactory(executorName + "-ScheduledThreadPool(" + corePoolSize + ")", threadConfigurer));
    }

    public static ExecutorService newFixedThreadPool(String executorName, int nThreads, ThreadConfigurer threadConfigurer) {
        return Executors.newFixedThreadPool(nThreads, new NamedThreadFactory(executorName + "-FixedThreadPool(" + nThreads + ")", threadConfigurer));
    }

    public static ExecutorService newSingleThreadExecutor(String executorName, ThreadConfigurer threadConfigurer) {
        return Executors.newSingleThreadExecutor(new NamedThreadFactory(executorName + "-SingleThreadExecutor", threadConfigurer));
    }

    public static ExecutorService newCachedThreadPool(String executorName, ThreadConfigurer threadConfigurer) {
        return Executors.newCachedThreadPool(new NamedThreadFactory(executorName + "-CachedThreadPool", threadConfigurer));
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String executorName, ThreadConfigurer threadConfigurer) {
        return Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(executorName + "-SingleThreadScheduledExecutor", threadConfigurer));
    }

    public static ScheduledExecutorService newScheduledThreadPool(String executorName, int corePoolSize) {
        return Executors.newScheduledThreadPool(corePoolSize, new NamedThreadFactory(executorName + "-ScheduledThreadPool(" + corePoolSize + ")", DEFAULT_THREAD_CONFIGURER));
    }

    private static class NamedThreadFactory implements ThreadFactory {

        private ThreadGroup threadGroup;
        private AtomicInteger threadNumber;
        private String name;
        private ThreadConfigurer threadConfigurer;

        public NamedThreadFactory(String name, ThreadConfigurer threadConfigurer) {
            this.threadConfigurer = threadConfigurer;
            threadNumber = new AtomicInteger(0);
            this.name = name;
            threadGroup = threadDeathLoggingThreadGroup;
        }

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(threadGroup, r, new StringBuilder(name).append("-").append(threadNumber.getAndIncrement()).toString());
            threadConfigurer.configureNewThread(thread);
            return thread;
        }
    }

    public static interface ThreadConfigurer {
        void configureNewThread(Thread t);
    }

}
