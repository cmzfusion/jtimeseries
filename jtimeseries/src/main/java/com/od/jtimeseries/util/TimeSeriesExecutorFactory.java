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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

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

    /**
     * Should return a single threaded executor if the guaranteed ordering of events
     * is to be preserved (the same thread must be used for all events fired by a single timeseries, thread
     * affinity by timeseries instance)
     * @param timeSeries
     */
    public static ExecutorService getExecutorForTimeSeriesEvents(Object timeSeries) {
        return executorSource.getExecutorForTimeSeriesEvents(timeSeries);
    }

    /**
     * @return the ExecutorService which should be used to read values from valueSuppliers
     * @param valueSupplier
     */
    public static ExecutorService getTimedValueSupplierExecutor(Object valueSupplier) {
        return executorSource.getTimedValueSupplierExecutor(valueSupplier);
    }

    /**
     * @return the ExecutorService which should be used for httpd queries
     * @param httpdInstance
     */
    public static ExecutorService getHttpdQueryExecutor(Object httpdInstance) {
        return executorSource.getHttpdQueryExecutor(httpdInstance);
    }

    /**
     * Should return a single threaded executor if the guaranteed ordering of events
     * is to be preserved (the same thread must be used for all events fired within a single identifiable tree,
     * / identifiable root, thread affinity by identifiable tree structure)
     * @param identifiable
     */
    public static ExecutorService getExecutorForIdentifiableTreeEvents(Object identifiable) {
        return executorSource.getExecutorForIdentifiableTreeEvents(identifiable);
    }

    /**
     * @return the ExecutorService which should be used for jmx queries
     * @param jmxMetric
     */
    public static ExecutorService getJmxMetricExecutor(Object jmxMetric) {
        return executorSource.getJmxMetricExecutor(jmxMetric);
    }

    /**
     * @return executor used to send datagram packets
     * @param publisherInstance
     */
    public static ScheduledExecutorService geUdpClientScheduledExecutor(Object publisherInstance) {
        return executorSource.geUdpClientScheduledExecutor(publisherInstance);
    }

    /**
     * Should return a single threaded executor if the guaranteed ordering of events
     * is to be preserved (the same thread must be used for all events fired by a single capture, thread
     * affinity by capture instance)
     * @param capture
     */
    public static ExecutorService getExecutorForCaptureEvents(Object capture) {
        return executorSource.getExecutorForCaptureEvents(capture);
    }

    /**
     * @return executor used to schedule and process outgoing UDP queue
     * @param publisherInstance
     */
    public static ScheduledExecutorService getUdpPublisherScheduledExecutor(Object publisherInstance) {
        return executorSource.getUdpPublisherScheduledExecutor(publisherInstance);
    }

    /**
     * @return the ScheduledExecutorService to be used by the schedulers which trigger timed capture, and other
     * triggerable events
     * @param scheduler
     */
    public static ScheduledExecutorService getCaptureSchedulingExecutor(Object scheduler) {
        return executorSource.getCaptureSchedulingExecutor(scheduler);
    }

    /**
     * Should return a single threaded executor if the guaranteed ordering for capturing to timeseries
     * is to be observed (the same thread must be used for all events fired by a single capture, thread
     * affinity by capture instance)
     * @param capture
     */
    public static ExecutorService getCaptureProcessingExecutor(Object capture) {
        return executorSource.getCaptureProcessingExecutor(capture);
    }

    public static ExecutorSource getExecutorSource() {
        return executorSource;
    }

    public static void setExecutorSource(ExecutorSource executorSource) {
        TimeSeriesExecutorFactory.executorSource = executorSource;
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
         * @return the ExecutorService which should be used for httpd queries
         */
        ExecutorService getHttpdQueryExecutor(Object httpdInstance);


        /**
         * @return the ExecutorService which should be used for jmx queries
         */
        ExecutorService getJmxMetricExecutor(Object jmxMetric);

        /**
         * @return the ExecutorService which should be used to read values from valueSuppliers
         */
        ExecutorService getTimedValueSupplierExecutor(Object valueSupplier);


        /**
         * @return executor used to schedule and process outgoing UDP queue
         */
        ScheduledExecutorService getUdpPublisherScheduledExecutor(Object publisherInstance);


        /**
         * @return executor used to send datagram packets
         */
        ScheduledExecutorService geUdpClientScheduledExecutor(Object publisherInstance);
        
        
        List<ExecutorService> getAllExecutors();
        
    }

    public static class DefaultExecutorSource implements ExecutorSource {

        private ExecutorService timeSeriesEventExecutor = NamedExecutors.newSingleThreadExecutor("JTS-TimeSeriesEvent");
        private ExecutorService captureEventExecutor = NamedExecutors.newSingleThreadExecutor("JTS-CaptureEvent");
        private ExecutorService identifiableTreeEventExecutor = NamedExecutors.newSingleThreadExecutor("JTS-IdentifiableTreeEvent");
        private ScheduledExecutorService captureSchedulingExecutor = NamedExecutors.newScheduledThreadPool("JTS-CaptureScheduling", 2);
        private ExecutorService captureProcessingExecutor = NamedExecutors.newFixedThreadPool("JTS-CaptureProcessing", 2);
        private ExecutorService valueSupplierExecutor = NamedExecutors.newFixedThreadPool("JTS-ValueSupplierProcessing", 2);
        private ExecutorService httpExecutor = NamedExecutors.newFixedThreadPool("JTS-HttpRequestProcessor", 3, NamedExecutors.DAEMON_THREAD_CONFIGURER);
        private ExecutorService jmxMetricExecutor = NamedExecutors.newFixedThreadPool("JTS-JmxMetricProcessor", 3);
        private ScheduledExecutorService udpPublisherService = NamedExecutors.newSingleThreadScheduledExecutor("JTS-UdpPublisher");
        private ScheduledExecutorService udpClientService = NamedExecutors.newSingleThreadScheduledExecutor("JTS-UdpClientr");


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

        public ExecutorService getTimedValueSupplierExecutor(Object valueSupplier) {
            return valueSupplierExecutor;
        }


        public ScheduledExecutorService getUdpPublisherScheduledExecutor(Object publisherInstance) {
            return udpPublisherService;
        }

        public ScheduledExecutorService geUdpClientScheduledExecutor(Object publisherInstance) {
            return udpClientService;
        }

        public List<ExecutorService> getAllExecutors() {
            List<ExecutorService> executorServiceList = new LinkedList<ExecutorService>();
            executorServiceList.add(captureProcessingExecutor);
            executorServiceList.add(captureSchedulingExecutor);
            executorServiceList.add(valueSupplierExecutor);
            executorServiceList.add(captureEventExecutor);
            executorServiceList.add(identifiableTreeEventExecutor);
            executorServiceList.add(timeSeriesEventExecutor);
            executorServiceList.add(jmxMetricExecutor);
            executorServiceList.add(httpExecutor);
            executorServiceList.add(udpPublisherService);
            executorServiceList.add(udpClientService);
            return executorServiceList;
        }
    }

    public static void shutdown() {
        for ( ExecutorService s : executorSource.getAllExecutors()) {
            abortDelayedTasks(s);
            s.shutdown();
        }
    }

    private static void abortDelayedTasks(ExecutorService s) {
        if ( s instanceof ScheduledThreadPoolExecutor) {
            ((ScheduledThreadPoolExecutor)s).setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        }
    }
}
