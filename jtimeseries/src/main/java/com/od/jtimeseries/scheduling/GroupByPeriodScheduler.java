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
package com.od.jtimeseries.scheduling;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 20-Jan-2009
 * Time: 13:59:32
 *
 * A scheduler which groups by scheduling period, so that all triggerables with the same period are executed togeter.
 *
 * This may mean that captures added or started after the scheduler is running do not trigger immediately, leading to a delay
 * before values start to be recorded. This is becuase other captures in the same group by capture period may already be running,
 * and the timer for that group may be in mid-period.
 */
public class GroupByPeriodScheduler extends AbstractScheduler {

    private Map<Long, TriggerableGroupTimerTask> tasksByPeriod = Collections.synchronizedMap(new HashMap<Long, TriggerableGroupTimerTask>());

    public GroupByPeriodScheduler(String id, String description) {
        this(id, description, 3);
    }

    public GroupByPeriodScheduler(String id, String description, int threadCount) {
        super(id, description, threadCount);
    }

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

    private void addToCaptureTimerTask(Triggerable t) {
        TriggerableGroupTimerTask task = tasksByPeriod.get(t.getTimePeriod().getLengthInMillis());
        if ( task == null ) {
            task = new TriggerableGroupTimerTask(t.getTimePeriod().getLengthInMillis());
            tasksByPeriod.put(t.getTimePeriod().getLengthInMillis(), task);
            if ( isStarted() ) {
                scheduleCaptureTask(task);
            }
        }
        task.addTimedCapture(t);
    }

    private void removeFromCaptureTimerTask(Triggerable t) {
        TriggerableGroupTimerTask task = tasksByPeriod.get(t.getTimePeriod().getLengthInMillis());
        task.removeTimedCapture(t);
    }

    private void scheduleCaptureTask(TriggerableGroupTimerTask t) {
        getScheduledExecutorService().scheduleAtFixedRate(t, 0, t.getPeriod(), TimeUnit.MILLISECONDS);
    }

    private class TriggerableGroupTimerTask implements Runnable  {

        private List<Triggerable> captures = Collections.synchronizedList(new ArrayList<Triggerable>());
        private Long period;

        public TriggerableGroupTimerTask(Long period) {
            this.period = period;
        }

        public void addTimedCapture(Triggerable t) {
            captures.add(t);
        }

        public void removeTimedCapture(Triggerable t) {
            captures.remove(t);
        }

        public Long getPeriod() {
            return period;
        }

        public void run() {
            long timestamp = System.currentTimeMillis();
            for (Triggerable t: captures) {
                t.trigger(timestamp);
            }
        }

        public int getCaptureCount() {
            return captures.size();
        }
    }

}
