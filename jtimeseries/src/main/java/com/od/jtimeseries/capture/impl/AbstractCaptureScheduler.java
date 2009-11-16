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

import com.od.jtimeseries.capture.Capture;
import com.od.jtimeseries.capture.CaptureScheduler;
import com.od.jtimeseries.capture.TimedCapture;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19-Feb-2009
 * Time: 16:13:01
 */
public abstract class AbstractCaptureScheduler extends IdentifiableBase implements CaptureScheduler {

    private Set<TimedCapture> captures = Collections.synchronizedSet(new HashSet<TimedCapture>());
    private boolean isStarted;
    private ScheduledExecutorService captureTimer;
    private int threadCount;

    public AbstractCaptureScheduler(String id, String description, int threadCount) {
        super(id, description);
        this.threadCount = threadCount;
    }

    public synchronized boolean addCapture(TimedCapture c) {
        return captures.add(c);
    }

    public synchronized boolean removeCapture(TimedCapture c) {
        return captures.remove(c);
    }

    public boolean containsCapture(Capture c) {
        return captures.contains(c);
    }

    public synchronized List<TimedCapture> getCaptures() {
        return new ArrayList<TimedCapture>(captures);
    }

    public synchronized boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public synchronized void start() {
        if ( ! isStarted() ) {
            setStarted(true);
            captureTimer = createExecutor();
            doStart();
        }
    }

    protected ScheduledExecutorService createExecutor() {
        return Executors.newScheduledThreadPool(threadCount);
    }

    protected abstract void doStart();

    public synchronized void stop() {
        if ( isStarted() ) {
            doStop();
            setStarted(false);
        }
    }

    protected void doStop() {
        //Doing the cancel this way, the cancel task is guaranteed to be the last one to run
        //otherwise we may be mid way through a capture task when the cancel takes place
        getScheduledExecutorService().schedule(new CancelTimerTask(), 0, TimeUnit.SECONDS);
    }

    protected ScheduledExecutorService getScheduledExecutorService() {
        return captureTimer;
    }

    private class CancelTimerTask implements Runnable {
        public void run() {
            synchronized ( AbstractCaptureScheduler.this ) {
                setStarted(false);
                getScheduledExecutorService().shutdownNow();
            }
        }
    }
}
