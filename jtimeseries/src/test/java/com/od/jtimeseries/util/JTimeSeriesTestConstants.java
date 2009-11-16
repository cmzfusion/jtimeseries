package com.od.jtimeseries.util;

import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.time.Time;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2009
 * Time: 23:40:21
 * To change this template use File | Settings | File Templates.
 */
public class JTimeSeriesTestConstants {

    public static final float TOLERANCE_MULTIPLIER = 3;
    public static final int TIMER_INACCURACY_MS = (int)(20 * TOLERANCE_MULTIPLIER);

    /**
     * When starting the scheduler, data capture is triggered on the schedulers thread
     *
     * Capture does not start until the first trigger takes place
     * It is not entirely deterministic when this will be.
     *
     * If we need to record values during the first scheduled period for a test, the best we can do at the moment
     * is to wait a certain amount of time for the first trigger to take place.
     *
     * TODO
     * I may add a listener callback to monitor the trigger event to improve this
     */
    public static final TimePeriod periodToWaitForFirstScheduledTimePeriodToStart = Time.millisecond((int)(20 * TOLERANCE_MULTIPLIER));
}
