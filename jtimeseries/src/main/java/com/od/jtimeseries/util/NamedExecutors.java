package com.od.jtimeseries.util;

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
 * An easy way to create Executors which name the threads
 */
public class NamedExecutors {

    public static ExecutorService newFixedThreadPool(String executorName, int nThreads) {
        return Executors.newFixedThreadPool(nThreads, new NamedThreadFactory(executorName + "-FixedThreadPool(" + nThreads + ")"));
    }

    public static ExecutorService newSingleThreadExecutor(String executorName) {
        return Executors.newSingleThreadExecutor(new NamedThreadFactory(executorName + "-SingleThreadExecutor"));
    }

    public static ExecutorService newCachedThreadPool(String executorName) {
        return Executors.newCachedThreadPool(new NamedThreadFactory(executorName + "-CachedThreadPool"));
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String executorName) {
        return Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(executorName + "-SingleThreadScheduledExecutor"));
    }

    public static ScheduledExecutorService newScheduledThreadPool(String executorName, int corePoolSize) {
        return Executors.newScheduledThreadPool(corePoolSize, new NamedThreadFactory(executorName + "-ScheduledThreadPool(" + corePoolSize + ")"));
    }

    private static class NamedThreadFactory implements ThreadFactory {

        private ThreadGroup threadGroup;
        private AtomicInteger threadNumber;
        private String name;

        public NamedThreadFactory(String name) {
            threadNumber = new AtomicInteger(0);
            this.name = name;
            threadGroup = new ThreadGroup(name);
        }

        public Thread newThread(Runnable r) {
            return new Thread(threadGroup, r, new StringBuilder(name).append("-").append(threadNumber.getAndIncrement()).toString());
        }

    }

}
