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
package com.od.jtimeseries.ui.util;

import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.EventTimer;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 27-Nov-2008
 * Time: 16:11:01
 */
public class BenchmarkingRepaintManager extends RepaintManager {

    private EventTimer timer;
    private Counter count;

    public static final String REPAINT_EVENT_DURATION_METRIC_ID = "Repaint Event Duration";
    public static final String NUMBER_OF_REPAINT_EVENTS_METRIC_ID = "Number of Repaint Events";

    public BenchmarkingRepaintManager(EventTimer timer, Counter count) {
        this.timer = timer;
        this.count = count;
    }

    public BenchmarkingRepaintManager(TimeSeriesContext timeSeriesContext) {
        this(timeSeriesContext, Time.seconds(30));
    }

    public BenchmarkingRepaintManager(TimeSeriesContext timeSeriesContext, TimePeriod timePeriod) {
        createDefaultSources(timeSeriesContext, timePeriod);
    }

    private void createDefaultSources(TimeSeriesContext context, TimePeriod timePeriod) {
        timer = context.newEventTimer(
                REPAINT_EVENT_DURATION_METRIC_ID,
                "Length of time taken by each screen repainting operation in ms - lower is better. " +
                        "Anything more than 50ms will result in noticible sluggishness.",
                CaptureFunctions.MEAN(timePeriod),
                CaptureFunctions.MAX(timePeriod)
        );

        count = context.newCounter(
                NUMBER_OF_REPAINT_EVENTS_METRIC_ID,
                "Number of repaint events",
                CaptureFunctions.MEAN_COUNT(Time.seconds(1), timePeriod)
        );
    }

    public void paintDirtyRegions() {
        timer.startEventTimer();
        count.incrementCount();
        super.paintDirtyRegions();
        timer.stopEventTimer();
    }
}
