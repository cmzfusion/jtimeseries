/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
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

import com.od.jtimeseries.source.ValueSource;
import com.od.jtimeseries.source.ValueSourceListener;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;
import com.od.jtimeseries.util.numeric.Numeric;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 27-Nov-2008
 * Time: 09:47:52
 */
public abstract class AbstractValueSource extends IdentifiableBase implements ValueSource {

    private CopyOnWriteArrayList<ValueSourceListener> listeners = new CopyOnWriteArrayList<ValueSourceListener>();

    public AbstractValueSource(String id, String description, ValueSourceListener... listeners) {
        super(id, description);
        if ( listeners != null) {
            this.listeners.addAll(Arrays.asList(listeners));
        }
    }

    public synchronized void addValueListener(ValueSourceListener sourceDataListener) {
        listeners.add(sourceDataListener);
    }

    public synchronized void removeValueListener(ValueSourceListener sourceDataListener) {
        listeners.remove(sourceDataListener);
    }

    protected void newSourceValue(double sourceValue) {
        for (ValueSourceListener listener : listeners) {
            listener.newValue(sourceValue);
        }
    }

    protected void newSourceValue(long sourceValue) {
        for (ValueSourceListener listener : listeners) {
            listener.newValue(sourceValue);
        }
    }

    protected void newSourceValue(Numeric sourceValue) {
        for (ValueSourceListener listener : listeners) {
            listener.newValue(sourceValue);
        }
    }

    public String toString() {
        return getId() + " " + getDescription();
    }
}
