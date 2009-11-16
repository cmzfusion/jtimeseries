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

import com.od.jtimeseries.capture.CaptureState;
import com.od.jtimeseries.capture.TimedCapture;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 20-Jan-2009
 * Time: 13:59:32
 *
 * A scheduler which groups captures by capture period, so that all captures with the same period are executed togeter.
 *
 * This may mean that captures added or started after the scheduler is running do not trigger immediately, leading to a delay
 * before values start to be recorded. This is becuase other captures in the same group by capture period may already be running,
 * and the timer for that group may be in mid-period.
 */
public class GroupByCapturePeriodScheduler extends AbstractCaptureScheduler {

    private Map<Long, CaptureTimerTask> tasksByPeriod = Collections.synchronizedMap(new HashMap<Long,CaptureTimerTask>());

    public GroupByCapturePeriodScheduler(String id, String description) {
        this(id, description, 1);
    }

    public GroupByCapturePeriodScheduler(String id, String description, int threadCount) {
        super(id, description, threadCount);
    }

    public synchronized boolean addCapture(TimedCapture c) {
        boolean added = super.addCapture(c);
        if ( added) {
            addToCaptureTimerTask(c);
        }
        return added;
    }

    public synchronized boolean removeCapture(TimedCapture c) {
        boolean removed = super.removeCapture(c);
        if ( removed) {
            removeFromCaptureTimerTask(c);
        }
        return removed;
    }

    protected void doStart() {
        for ( CaptureTimerTask t : tasksByPeriod.values()) {
            scheduleCaptureTask(t);
        }
    }

    private void addToCaptureTimerTask(TimedCapture c) {
        CaptureTimerTask t = tasksByPeriod.get(c.getCapturePeriodInMilliseconds());
        if ( t == null ) {
            t = new CaptureTimerTask(c.getCapturePeriodInMilliseconds());
            tasksByPeriod.put(c.getCapturePeriodInMilliseconds(), t);
            if ( isStarted() ) {
                scheduleCaptureTask(t);
            }
        }
        t.addTimedCapture(c);
    }

    private void removeFromCaptureTimerTask(TimedCapture c) {
        CaptureTimerTask t = tasksByPeriod.get(c.getCapturePeriodInMilliseconds());
        t.removeTimedCapture(c);
    }

    private void scheduleCaptureTask(CaptureTimerTask t) {
        getScheduledExecutorService().scheduleAtFixedRate(t, 0, t.getPeriod(), TimeUnit.MILLISECONDS);
    }

    private class CaptureTimerTask implements Runnable  {

        private List<TimedCapture> captures = Collections.synchronizedList(new ArrayList<TimedCapture>());
        private Long period;

        public CaptureTimerTask(Long period) {
            this.period = period;
        }

        public void addTimedCapture(TimedCapture t) {
            captures.add(t);
        }

        public void removeTimedCapture(TimedCapture t) {
            captures.remove(t);
        }

        public Long getPeriod() {
            return period;
        }

        public void run() {
            long timestamp = System.currentTimeMillis();
            for (TimedCapture t: captures) {
                if ( t.getState() == CaptureState.STARTED || t.getState() == CaptureState.STARTING) {
                    t.triggerCapture(timestamp);
                }
            }
        }

        public int getCaptureCount() {
            return captures.size();
        }
    }

}
