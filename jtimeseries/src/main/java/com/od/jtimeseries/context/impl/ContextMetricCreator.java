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
 * a value source, a capture (timed or otherwise) and a time series within the context in one method call. A ContextMetricCreator
 * handles the creation and wiring together of all of those items.
 */
public interface ContextMetricCreator {

    ValueRecorder createValueRecorder(String id, String description, CaptureFunction... captureFunctions);

    QueueTimer createQueueTimer(String id, String description, CaptureFunction... captureFunctions);

    Counter createCounter(String id, String description, CaptureFunction... captureFunctions);

    EventTimer createEventTimer(String id, String description, CaptureFunction... captureFunctions);

    TimedValueSource createTimedValueSource(String id, String description, ValueSupplier valueSupplier, TimePeriod timePeriod);

}
