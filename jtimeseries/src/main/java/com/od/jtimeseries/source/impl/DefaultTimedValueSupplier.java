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
package com.od.jtimeseries.source.impl;

import com.od.jtimeseries.source.TimedValueSupplier;
import com.od.jtimeseries.source.ValueSourceListener;
import com.od.jtimeseries.source.ValueSupplier;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 28-Nov-2009
 * Time: 19:08:41
 * To change this template use File | Settings | File Templates.
 */
public class DefaultTimedValueSupplier extends AbstractValueSource implements TimedValueSupplier {

    private TimePeriod timePeriod;
    private ValueSupplier valueSupplier;

    public DefaultTimedValueSupplier(String id, String description, ValueSupplier valueSupplier, TimePeriod timerPeriod, ValueSourceListener... sourceDataListeners) {
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
