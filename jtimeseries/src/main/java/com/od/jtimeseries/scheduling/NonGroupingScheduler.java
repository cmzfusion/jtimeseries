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
package com.od.jtimeseries.scheduling;

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
public class NonGroupingScheduler extends AbstractScheduler {

    private Map<Triggerable, TriggerableTimerTask> tasks = Collections.synchronizedMap(new HashMap<Triggerable, TriggerableTimerTask>());

    public NonGroupingScheduler() {
        this(3);
    }

    public NonGroupingScheduler(int threadCount) {
        super(threadCount);
    }

    public synchronized boolean addTriggerable(Triggerable t) {
        boolean added = super.addTriggerable(t);
        if ( added ) {
            scheduleCapture(t);
        }
        return added;
    }

    public synchronized boolean removeTriggerable(Triggerable t) {
        boolean removed = super.removeTriggerable(t);
        if ( removed ) {
            unscheduleCapture(t);
        }
        return removed;
    }

    public synchronized void doStart() {
        for ( Triggerable t : getTriggerables()) {
            scheduleCapture(t);
        }
    }

    public synchronized void doStop() {
        for ( Triggerable t : getTriggerables()) {
            unscheduleCapture(t);
        }
    }

    private synchronized void scheduleCapture(Triggerable t) {
        if ( isStarted() && ! tasks.containsKey(t)) {
            TriggerableTimerTask task = new TriggerableTimerTask(t);
            tasks.put(t, task);
            getScheduledExecutorService().scheduleAtFixedRate(
                task, 0, t.getTimePeriod().getLengthInMillis(), TimeUnit.MILLISECONDS
            );
        }
    }

    private void unscheduleCapture(Triggerable triggerable) {
        TriggerableTimerTask t = tasks.remove(triggerable);
        if ( t != null ) {
            t.cancel();
        }
    }

    private class TriggerableTimerTask implements Runnable {

        private volatile boolean cancelled;
        private Triggerable triggerable;

        private TriggerableTimerTask(Triggerable triggerable) {
            this.triggerable = triggerable;
        }

        public void run() {
            if ( ! cancelled ) {
                triggerCapture(triggerable);
            } else {
                throw new RuntimeException("Cancelled");
            }
        }

        private void triggerCapture(Triggerable t) {
            t.trigger(System.currentTimeMillis());
        }

        public void cancel() {
            this.cancelled = true;
        }
    }
}
