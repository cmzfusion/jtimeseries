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

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24-Feb-2009
 * Time: 17:53:04
 *
 * Controls the Executors/threads used for various library level tasks, such as event propagation
 */
public class TimeSeriesExecutorFactory {

    private static volatile ExecutorSource executorSource = new DefaultExecutorSource();

    public static Executor getExecutorForTimeSeriesEvents(Object timeSeries) {
        return executorSource.getExecutorForTimeSeriesEvents(timeSeries);
    }

    public static Executor getExecutorForCaptureEvents(Object capture) {
        return executorSource.getExecutorForCaptureEvents(capture);
    }

    public static Executor getExecutorForIdentifiableTreeEvents(Object identifiable) {
        return executorSource.getExecutorForIdentifiableTreeEvents(identifiable);
    }

    public static ExecutorService getCaptureProcessingExecutor(Object capture) {
        return executorSource.getCaptureProcessingExecutor(capture);
    }

    public static ScheduledExecutorService getCaptureSchedulingExecutor(Object scheduler) {
        return executorSource.getCaptureSchedulingExecutor(scheduler);
    }

    public static ExecutorSource getExecutorSource() {
        return executorSource;
    }

    public static void setExecutorSource(ExecutorSource executorSource) {
        TimeSeriesExecutorFactory.executorSource = executorSource;
    }

    public static Executor getHttpdQueryExecutor(Object httpdInstance) {
        return executorSource.getHttpdQueryExecutor(httpdInstance);
    }

    public static Executor getJmxMetricExecutor(Object jmxMetric) {
        return executorSource.getJmxMetricExecutor(jmxMetric);
    }

    public static interface ExecutorSource {

        /**
         * Should return a single threaded executor if the guaranteed ordering of events
         * is to be preserved (the same thread must be used for all events fired by a single timeseries, thread
         * affinity by timeseries instance)
         */
        ExecutorService getExecutorForTimeSeriesEvents(Object timeSeries);

        /**
         * Should return a single threaded executor if the guaranteed ordering of events
         * is to be preserved (the same thread must be used for all events fired by a single capture, thread
         * affinity by capture instance)
         */
        ExecutorService getExecutorForCaptureEvents(Object capture);

        /**
         * Should return a single threaded executor if the guaranteed ordering of events
         * is to be preserved (the same thread must be used for all events fired within a single identifiable tree,
         * / identifiable root, thread affinity by identifiable tree structure)
         */
        ExecutorService getExecutorForIdentifiableTreeEvents(Object identifiable);

        /**
         * Should return a single threaded executor if the guaranteed ordering for capturing to timeseries
         * is to be observed (the same thread must be used for all events fired by a single capture, thread
         * affinity by capture instance)
         */
        ExecutorService getCaptureProcessingExecutor(Object capture);

        /**
         * @return the ScheduledExecutorService to be used by the schedulers which trigger timed capture, and other
         * triggerable events
         */
        ScheduledExecutorService getCaptureSchedulingExecutor(Object scheduler);

        /**
         * @return the ExecutoService which should be used for httpd queries
         */
        ExecutorService getHttpdQueryExecutor(Object httpdInstance);


        Executor getJmxMetricExecutor(Object jmxMetric);
    }

    public static class DefaultExecutorSource implements ExecutorSource {

        private ExecutorService timeSeriesEventExecutor = NamedExecutors.newSingleThreadExecutor("TimeSeriesEvent");
        private ExecutorService captureEventExecutor = NamedExecutors.newSingleThreadExecutor("CaptureEvent");
        private ExecutorService identifiableTreeEventExecutor = NamedExecutors.newSingleThreadExecutor("IdentifiableTreeEvent");
        private ScheduledExecutorService captureSchedulingExecutor = NamedExecutors.newScheduledThreadPool("CaptureScheduling", 2);
        private ExecutorService captureProcessingExecutor = NamedExecutors.newSingleThreadExecutor("CaptureProcessing");
        private ExecutorService httpExecutor = NamedExecutors.newFixedThreadPool("HttpRequestProcessor", 3, NamedExecutors.DAEMON_THREAD_CONFIGURER);
        private ExecutorService jmxMetricExecutor = NamedExecutors.newFixedThreadPool("JmxMetricProcessor", 3);

        public ExecutorService getExecutorForTimeSeriesEvents(Object timeSeries) {
            return timeSeriesEventExecutor;
        }

        public ExecutorService getExecutorForCaptureEvents(Object capture) {
            return captureEventExecutor;
        }

        public ExecutorService getExecutorForIdentifiableTreeEvents(Object identifiable) {
            return identifiableTreeEventExecutor;
        }

        public ExecutorService getCaptureProcessingExecutor(Object capture) {
            return captureProcessingExecutor;
        }

        public ScheduledExecutorService getCaptureSchedulingExecutor(Object scheduler) {
            return captureSchedulingExecutor;
        }

        public ExecutorService getHttpdQueryExecutor(Object httpdInstance) {
            return httpExecutor;
        }

        public ExecutorService getJmxMetricExecutor(Object jmxMetric) {
            return jmxMetricExecutor;
        }

    }
}
