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

import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.source.ValueSourceListener;
import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 27-Nov-2008
 * Time: 09:52:14
 */
public class DefaultValueRecorder extends AbstractValueSource implements ValueRecorder {

    public static final ValueRecorder NULL_VALUE_RECORDER = new DefaultValueRecorder("Dummy", "Dummy");

    public DefaultValueRecorder(String id, String description, ValueSourceListener... sourceDataListeners) {
        super(id, description, sourceDataListeners);
    }

    public void newValue(Numeric value) {
        newSourceValue(value);
    }

    public void newValue(long l) {
        newSourceValue(l);
    }

    public void newValue(double d) {
        newSourceValue(d);
    }

}
