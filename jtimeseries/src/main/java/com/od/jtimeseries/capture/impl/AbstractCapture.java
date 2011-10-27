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
package com.od.jtimeseries.capture.impl;

import com.od.jtimeseries.capture.Capture;
import com.od.jtimeseries.capture.CaptureListener;
import com.od.jtimeseries.capture.CaptureState;
import com.od.jtimeseries.source.ValueSource;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.util.TimeSeriesExecutorFactory;
import com.od.jtimeseries.identifiable.IdentifiableBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06-Feb-2009
 * Time: 11:24:32
 */
public abstract class AbstractCapture extends IdentifiableBase implements Capture {

    private final List<CaptureListener> captureListeners = Collections.synchronizedList(new ArrayList<CaptureListener>());
    private IdentifiableTimeSeries timeSeries;
    private ValueSource source;
    private CaptureState currentState = CaptureState.STOPPED;

    public AbstractCapture(String id, String description, IdentifiableTimeSeries timeSeries, ValueSource source) {
        super(id, description);
        this.timeSeries = timeSeries;
        this.source = source;
    }

    public void addCaptureListener(CaptureListener l) {
        captureListeners.add(l);
    }

    public void removeCaptureListener(CaptureListener l) {
        captureListeners.remove(l);
    }

    //Calling this will change the state and place a state change event on the event processing queue
    //Calling this while holding a state change lock is expected - this will guarantee the order of events
    //and doing the actual event processing on a subthread should gurantee we don't hold the lock for long
    protected void changeStateAndFireEvent(final CaptureState newState) {
        final CaptureState oldState = currentState;
        currentState = newState;
        Executor e = TimeSeriesExecutorFactory.getExecutorForCaptureEvents(this);
        e.execute(new Runnable() {
            public void run() {
                CaptureListener[] snapshot = getListenerSnapshot();
                for ( CaptureListener l : snapshot) {
                    l.captureStateChanged(AbstractCapture.this, oldState, newState);
                }
            }
        });
    }

    //Calling this will change the state and place a state change event on the event processing queue
    //Calling this while holding a state change lock is expected - this will guarantee the order of events
    //and doing the actual event processing on a subthread should gurantee we don't hold the lock for long
    protected void fireTriggerEvent() {
        Executor e = TimeSeriesExecutorFactory.getExecutorForCaptureEvents(this);
        e.execute(new Runnable() {
            public void run() {
                CaptureListener[] snapshot = getListenerSnapshot();
                for ( CaptureListener l : snapshot) {
                    l.captureTriggered(AbstractCapture.this);
                }
            }
        });
    }

    private CaptureListener[] getListenerSnapshot() {
        CaptureListener[] snapshot;
        synchronized (captureListeners) {
            snapshot = captureListeners.toArray(new CaptureListener[captureListeners.size()]);
        }
        return snapshot;
    }

    public IdentifiableTimeSeries getTimeSeries() {
        return timeSeries;
    }

    public ValueSource getValueSource() {
        return source;
    }

    public CaptureState getState() {
        return currentState;
    }
}
