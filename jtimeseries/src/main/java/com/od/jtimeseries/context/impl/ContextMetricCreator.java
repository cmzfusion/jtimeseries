package com.od.jtimeseries.context.impl;

import com.od.jtimeseries.source.*;
import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 28-Nov-2009
 * Time: 21:06:54
 *
 * TimeSeriesContext has several convenience methods such as createValueRecorder() which allow the user to create
 * a value source, a capture (timed or otherwise) and a time series within the context in one method call.
 *
 * e.g. context.newValueRecorder("Memory", "Memory Usage", CaptureFunction.MEAN(Time.mins(5));
 *
 * This would create in the context a ValueRecorder instance, a TimedCapture to aggregate the values across a five minute period,
 * and TimeSeries to store the mean value every five minutes.
 */
public interface ContextMetricCreator {

    ValueRecorder createValueRecorderSeries(String id, String description, CaptureFunction... captureFunctions);

    QueueTimer createQueueTimerSeries(String id, String description, CaptureFunction... captureFunctions);

    Counter createCounterSeries(String id, String description, CaptureFunction... captureFunctions);

    EventTimer createEventTimerSeries(String id, String description, CaptureFunction... captureFunctions);

    TimedValueSupplier createValueSupplierSeries(String id, String description, ValueSupplier valueSupplier, TimePeriod timePeriod);

}
