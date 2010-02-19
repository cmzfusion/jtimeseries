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
 * a value source, a capture and a time series within the context in one method call.
 *
 * An example is the easiest way to illustrate this:
 *
 * <pre>
 * e.g.
 * ValueRecorder vr = context.newValueRecorder("Memory", "Memory Usage", CaptureFunctions.MEAN(Time.mins(5));
 * vr.newValue(10);
 * vr.newValue(20);
 * ...
 * </pre>
 *
 * The above would create a ValueRecorder which can be used to store values, a TimedCapture to aggregate
 * the values every five minutes using the MEAN function, and TimeSeries which stores the mean values from the function.
 * If you wish to store raw values from the valueRecorder, without performing any aggregation, do not specify a CaptureFunction,
 * or use the special function CaptureFunctions.RAW_VALUES
 */
public interface ContextMetricCreator {

    /**
     * Create a valueRecorder, capture(s) and timeseries within this context
     * The timeseries will be created they if do not yet exist, otherwise the valueRecorder will be bound to the existing series
     * 
     * If no captureFunctions are specified, a single a capture and timeseries will be created to store the raw values from the 
     * valueRecorder. The timeseries will have the id and description provided.
     * 
     * Alternatively, if captureFunction(s) are specified, a TimedCapture and timeseries will be created for each function.
     * The TimedCapture uses the function to aggregate the values received from the valueRecorder, and stores the aggregate value 
     * into a timeseries periodically. (For example, the median value every 5 minutes). 
     * In this case, the id of the timeseries created will be derived from the supplied id and the choice of CaptureFunction
     * 
     * @param id, id for the time series to be created
     * @param description, description of the time series
     * @param captureFunctions, functions to aggregate values across a time period
     * @return new valueRecorder instance
     */
    ValueRecorder createValueRecorderSeries(String id, String description, CaptureFunction... captureFunctions);

    /**
     * Create a queueTimer, capture(s) and timeseries within this context
     * The timeseries will be created they if do not yet exist, otherwise the queueTimer will be bound to the existing series
     * 
     * If no captureFunctions are specified, a single a capture and timeseries will be created to store the raw values from the 
     * queueTimer. The timeseries will have the id and description provided.
     * 
     * Alternatively, if captureFunction(s) are specified, a TimedCapture and timeseries will be created for each function.
     * The TimedCapture uses the function to aggregate the values received from the queueTimer, and stores the aggregate value 
     * into a timeseries periodically. (For example, the median value every 5 minutes). 
     * In this case, the id of the timeseries created will be derived from the supplied id and the choice of CaptureFunction
     * 
     * @param id, base id for the time series to be created
     * @param description, description of the time series
     * @param captureFunctions, functions to aggregate values across a time period
     * @return new queueTimer instance
     */
    QueueTimer createQueueTimerSeries(String id, String description, CaptureFunction... captureFunctions);

    /**
     * Create a counter, capture(s) and timeseries within this context
     * The timeseries will be created they if do not yet exist, otherwise the counter will be bound to the existing series
     * 
     * If no captureFunctions are specified, a single a capture and timeseries will be created to store the raw values from the 
     * counter. The timeseries will have the id and description provided.
     * 
     * Alternatively, if captureFunction(s) are specified, a TimedCapture and timeseries will be created for each function.
     * The TimedCapture uses the function to aggregate the values received from the counter, and stores the aggregate value 
     * into a timeseries periodically. (For example, the median value every 5 minutes). 
     * In this case, the id of the timeseries created will be derived from the supplied id and the choice of CaptureFunction
     * 
     * @param id, base id for the time series to be created
     * @param description, description of the time series
     * @param captureFunctions, functions to aggregate values across a time period
     * @return new counter instance
     */
    Counter createCounterSeries(String id, String description, CaptureFunction... captureFunctions);

    /**
     * Create a eventTimer, capture(s) and timeseries within this context
     * The timeseries will be created they if do not yet exist, otherwise the eventTimer will be bound to the existing series
     * 
     * If no captureFunctions are specified, a single a capture and timeseries will be created to store the raw values from the 
     * eventTimer. The timeseries will have the id and description provided.
     * 
     * Alternatively, if captureFunction(s) are specified, a TimedCapture and timeseries will be created for each function.
     * The TimedCapture uses the function to aggregate the values received from the eventTimer, and stores the aggregate value 
     * into a timeseries periodically. (For example, the median value every 5 minutes). 
     * In this case, the id of the timeseries created will be derived from the supplied id and the choice of CaptureFunction
     * 
     * @param id, base id for the time series to be created
     * @param description, description of the time series
     * @param captureFunctions, functions to aggregate values across a time period
     * @return new eventTimer instance
     */
    EventTimer createEventTimerSeries(String id, String description, CaptureFunction... captureFunctions);

    /**
     * Create a timedValueSource, capture(s) and timeseries within this context
     * The timeseries will be created they if do not yet exist, otherwise the timedValueSource will be bound to the existing series
     * 
     * The timedValueSource periodically calls getValue() to obtain a value from the valueSupplier provided, and stores the value
     * into a timeseries with the id and description provided
     *
     * @param id, id for the timeseries to be created
     * @param description, description of the time series
     * @return new timedValueSource instance
     */
    TimedValueSource createValueSupplierSeries(String id, String description, ValueSupplier valueSupplier, TimePeriod timePeriod);

}
