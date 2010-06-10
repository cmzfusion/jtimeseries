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

import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.capture.TimedCapture;
import com.od.jtimeseries.capture.Capture;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-Dec-2008
 * Time: 15:16:47
 */
public interface Scheduler extends Identifiable {

    /**
     * All Scheduler should use this ID, to make sure only one Scheduler can exist per context
     */
    public static final String ID = "Scheduler";

    boolean addTriggerable(Triggerable c);

    boolean removeTriggerable(Triggerable c);

    /**
     * @return true, if t implements Triggerable and is currently managed by the scheduler
     */
    boolean containsTriggerable(Object t);

    /**
     * @return a snapshot of the captures managed by this scheduler
     */
    List<Triggerable> getTriggerables();

    boolean isStarted();

    void start();

    void stop();
}
