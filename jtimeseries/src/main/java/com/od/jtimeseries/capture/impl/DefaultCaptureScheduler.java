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
package com.od.jtimeseries.capture.impl;

import com.od.jtimeseries.capture.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19-Feb-2009
 * Time: 16:06:24
 *
 * A scheduler which triggers each capture using a separate timer task
 *
 * Captures are not grouped by capture period, e.g. two captures with a capture period of 30s will be
 * triggered at different times, depending on when they or the scheduler were stopped/started
 */
public class DefaultCaptureScheduler extends AbstractCaptureScheduler {

    private Map<TimedCapture, CaptureTimerTask> tasks = Collections.synchronizedMap(new HashMap<TimedCapture,CaptureTimerTask>());
    private CaptureListener schedulingCaptureListener = new SchedulingCaptureListener();

    public DefaultCaptureScheduler(String id, String description) {
        this(id, description, 1);
    }

    public DefaultCaptureScheduler(String id, String description, int threadCount) {
        super(id, description, threadCount);
    }

    public synchronized boolean addCapture(TimedCapture c) {
        boolean added = super.addCapture(c);
        if ( added ) {
            scheduleCapture(c);
            c.addCaptureListener(schedulingCaptureListener);
        }
        return added;
    }

    public synchronized boolean removeCapture(TimedCapture c) {
        boolean removed = super.removeCapture(c);
        if ( removed ) {
            unscheduleCapture(c);
            c.removeCaptureListener(schedulingCaptureListener);
        }
        return removed;
    }

    public synchronized void doStart() {
        for ( TimedCapture c : getCaptures()) {
            scheduleCapture(c);
        }
    }

    public synchronized void doStop() {
        for ( TimedCapture c : getCaptures()) {
            unscheduleCapture(c);
        }
    }

    private synchronized void scheduleCapture(TimedCapture c) {
        if ( isStarted() && (c.getState() == CaptureState.STARTED || c.getState() == CaptureState.STARTING && ! tasks.containsKey(c))) {
            CaptureTimerTask t = new CaptureTimerTask(c);
            tasks.put(c, t);
            getScheduledExecutorService().scheduleAtFixedRate(
                    t, 0, c.getCapturePeriodInMilliseconds(), TimeUnit.MILLISECONDS
            );
        }
    }

    private void unscheduleCapture(TimedCapture c) {
        CaptureTimerTask t = tasks.remove(c);
        if ( t != null ) {
            t.cancel();
        }
    }

    private class SchedulingCaptureListener extends CaptureAdapter {

        public void captureStateChanged(Capture source, CaptureState oldState, CaptureState newState) {
            if ( newState == CaptureState.STARTING) {
                scheduleCapture((TimedCapture)source);
            } else if ( newState == CaptureState.STOPPED) {
                unscheduleCapture((TimedCapture)source);
            }
        }
    }

    private class CaptureTimerTask implements Runnable {

        private volatile boolean cancelled;
        private TimedCapture capture;

        private CaptureTimerTask(TimedCapture capture) {
            this.capture = capture;
        }

        public void run() {
            if ( ! cancelled ) {
                if ( capture.getState() == CaptureState.STARTED || capture.getState() == CaptureState.STARTING) {
                    triggerCapture(capture);
                }
            } else {
                throw new RuntimeException("Cancelled");
            }
        }

        private void triggerCapture(TimedCapture c) {
            c.triggerCapture(System.currentTimeMillis());
        }

        public void cancel() {
            this.cancelled = true;
        }
    }
}
