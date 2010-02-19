package com.od.jtimeseries.context.impl;

import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.source.*;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.identifiable.Identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 28-Nov-2009
 * Time: 21:06:54
 *
 */
public interface ContextMetricCreator {

    ValueRecorder createValueRecorderSeries(Identifiable parent, String path, String id, String description, CaptureFunction... captureFunctions);

    QueueTimer createQueueTimerSeries(Identifiable parent, String path, String id, String description, CaptureFunction... captureFunctions);

    Counter createCounterSeries(Identifiable parent, String path, String id, String description, CaptureFunction... captureFunctions);

    EventTimer createEventTimerSeries(Identifiable parent, String path, String id, String description, CaptureFunction... captureFunctions);

    TimedValueSource createValueSupplierSeries(Identifiable parent, String path,String id, String description, ValueSupplier valueSupplier, TimePeriod timePeriod);

}
