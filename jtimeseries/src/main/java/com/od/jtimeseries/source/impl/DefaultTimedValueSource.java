package com.od.jtimeseries.source.impl;

import com.od.jtimeseries.source.TimedValueSource;
import com.od.jtimeseries.source.ValueSourceListener;
import com.od.jtimeseries.source.ValueSupplier;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 28-Nov-2009
 * Time: 19:08:41
 * To change this template use File | Settings | File Templates.
 */
public class DefaultTimedValueSource extends AbstractValueSource implements TimedValueSource {

    private TimePeriod timePeriod;
    private ValueSupplier valueSupplier;

    public DefaultTimedValueSource(String id, String description, ValueSupplier valueSupplier, TimePeriod timerPeriod, ValueSourceListener... sourceDataListeners) {
        super(id, description, sourceDataListeners);
        this.valueSupplier = valueSupplier;
        this.timePeriod = timerPeriod;
    }

    public TimePeriod getTimePeriod() {
        return timePeriod;
    }

    public void trigger(long timestamp) {
        Numeric value = valueSupplier.getValue();
        if ( value != null) {
            newSourceValue(value);
        }
    }
}
