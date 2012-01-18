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

import com.od.jtimeseries.identifiable.IdentifiableBase;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.TimeSeriesExecutorFactory;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19-Feb-2009
 * Time: 16:13:01
 */
public abstract class AbstractScheduler extends IdentifiableBase implements Scheduler {

    private final Set<Triggerable> triggerables = Collections.synchronizedSet(new HashSet<Triggerable>());
    private final ScheduledExecutorService captureExecutor = TimeSeriesExecutorFactory.getCaptureSchedulingExecutor(this);
    private boolean isStarted;

    public AbstractScheduler() {
        super(ID, ID);
        setDescription(getClass().getName());
    }

    public synchronized boolean addTriggerable(Triggerable c) {
        return triggerables.add(c);
    }

    public synchronized boolean removeTriggerable(Triggerable c) {
        return triggerables.remove(c);
    }

    public boolean containsTriggerable(Object c) {
        return c instanceof Triggerable && triggerables.contains((Triggerable)c);
    }

    public synchronized List<Triggerable> getTriggerables() {
        return new ArrayList<Triggerable>(triggerables);
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
            doStart();
        }
    }

    protected abstract void doStart();

    public synchronized void stop() {
        if ( isStarted() ) {
            doStop();
            setStarted(false);
        }
    }

    protected abstract void doStop();

    protected ScheduledExecutorService getScheduledExecutorService() {
        return captureExecutor;
    }
}
