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

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 20-Jan-2009
 * Time: 13:59:32
 *
 * A scheduler which groups by scheduling period, so that all triggerables with the same period are executed together.
 *
 * This may mean that captures added or started after the scheduler is running do not trigger immediately, leading to a delay
 * before values start to be recorded. This is because other captures in the same group by capture period may already be running,
 * and the timer for that group may be in mid-period.
 */
public class GroupByPeriodScheduler extends AbstractScheduler {

    private static LogMethods logMethods = LogUtils.getLogMethods(GroupByPeriodScheduler.class);
    private Map<Long, TriggerableGroupTimerTask> tasksByPeriod = Collections.synchronizedMap(new HashMap<Long, TriggerableGroupTimerTask>());

    public synchronized boolean addTriggerable(Triggerable t) {
        boolean added = super.addTriggerable(t);
        if ( added) {
            addToCaptureTimerTask(t);
        }
        return added;
    }

    public synchronized boolean removeTriggerable(Triggerable t) {
        boolean removed = super.removeTriggerable(t);
        if ( removed) {
            removeFromCaptureTimerTask(t);
        }
        return removed;
    }

    protected void doStart() {
        for ( TriggerableGroupTimerTask t : tasksByPeriod.values()) {
            scheduleCaptureTask(t);
        }
    }

    protected void doStop() {
        for ( TriggerableGroupTimerTask t : tasksByPeriod.values()) {
            t.cancel();
        }
    }

    private void addToCaptureTimerTask(Triggerable t) {
        TriggerableGroupTimerTask task = tasksByPeriod.get(t.getTimePeriod().getLengthInMillis());
        if ( task == null ) {
            task = new TriggerableGroupTimerTask(t.getTimePeriod().getLengthInMillis());
            tasksByPeriod.put(t.getTimePeriod().getLengthInMillis(), task);
            task.addTriggerable(t);
            if ( isStarted() ) {
                scheduleCaptureTask(task);
            }
        } else {
            task.addTriggerable(t);
        }
    }

    private void removeFromCaptureTimerTask(Triggerable t) {
        TriggerableGroupTimerTask task = tasksByPeriod.get(t.getTimePeriod().getLengthInMillis());
        task.removeTriggerable(t);
        if ( task.getTriggerableCount() == 0) {
            task.cancel();
            tasksByPeriod.remove(t.getTimePeriod());
        }
    }

    private void scheduleCaptureTask(TriggerableGroupTimerTask t) {
        Future f = getScheduledExecutorService().scheduleAtFixedRate(t, 0, t.getPeriod(), TimeUnit.MILLISECONDS);
        t.setFuture(f);
    }

    private class TriggerableGroupTimerTask implements Runnable  {

        private List<Triggerable> triggerables = Collections.synchronizedList(new ArrayList<Triggerable>());
        private Long period;
        private volatile Future future;

        public TriggerableGroupTimerTask(Long period) {
            this.period = period;
        }

        public void addTriggerable(Triggerable t) {
            triggerables.add(t);
        }

        public void removeTriggerable(Triggerable t) {
            triggerables.remove(t);
        }

        public Long getPeriod() {
            return period;
        }

        public void run() {
            long timestamp = System.currentTimeMillis();

            List<Triggerable> snapshot;
            synchronized (triggerables) {
                snapshot = new ArrayList<Triggerable>(triggerables);
            }

            for (Triggerable t: snapshot) {
                //triggerable may be provided by application classes
                //need to handle this callback very carefully, catch all errors
                try {
                    t.trigger(timestamp);
                } catch (Throwable e) {
                    logMethods.error("Error on trigger() for triggerable " + t, e);
                }
            }
        }

        public void setFuture(Future future) {
            this.future = future;
        }

        public void cancel() {
            if ( future != null && ! future.isCancelled()) {
                future.cancel(false);
            }
        }

        public int getTriggerableCount() {
            return triggerables.size();
        }
    }

}
