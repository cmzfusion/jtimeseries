package com.od.jtimeseries.scheduling;

import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.identifiable.Identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 27-Nov-2009
 * Time: 21:49:25
 * To change this template use File | Settings | File Templates.
 */
public interface Triggerable extends Identifiable {

    TimePeriod getTimePeriod();

    /**
     * Called by the scheduler when the Capture is in STARTING or STARTED state
     */
    void trigger(long timestamp);
}
