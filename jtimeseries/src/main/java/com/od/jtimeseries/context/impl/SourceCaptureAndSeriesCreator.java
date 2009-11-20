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
package com.od.jtimeseries.context.impl;

import com.od.jtimeseries.capture.Capture;
import com.od.jtimeseries.capture.ValueSourceCapture;
import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.source.*;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.util.identifiable.Identifiable;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 05-Nov-2009
 * Time: 16:59:43
 *
 * TimeSeriesContext has several convenience methods such as createValueRecorder() which allow the user to create
 * a value source, a capture (timed or otherwise) and a time series within the context in one method call. This
 * class handles the creation of all of those items.
 */
class SourceCaptureAndSeriesCreator {

    private TimeSeriesContext timeSeriesContext;
    protected static AtomicLong captureUniqueId = new AtomicLong(0);

    //the user passes in just one id, which will be used for the timeseries, but the value source and
    //capture also require ids. We create these by adding a prefix to the timeseries id supplied.
    private static String SOURCE_PREFIX = "Source_";
    private static String CAPTURE_PREFIX = "Capture_";

    public SourceCaptureAndSeriesCreator(TimeSeriesContext timeSeriesContext) {
        this.timeSeriesContext = timeSeriesContext;
    }

    public ValueRecorder createValueRecorder(String id, String description, CaptureFunction... captureFunctions) {
        ValueRecorder v = timeSeriesContext.getValueSourceFactory().createValueRecorder(SOURCE_PREFIX + id, description);
        createSeriesAndCapturesForSource(id, description, v, captureFunctions);
        return v;
    }

    public QueueTimer createQueueTimer(String id, String description, CaptureFunction... captureFunctions) {
        QueueTimer q = timeSeriesContext.getValueSourceFactory().createQueueTimer(SOURCE_PREFIX + id, description);
        createSeriesAndCapturesForSource(id, description, q, captureFunctions);
        return q;
    }

    public Counter createCounter(String id, String description, CaptureFunction... captureFunctions) {
        Counter c = timeSeriesContext.getValueSourceFactory().createCounter(SOURCE_PREFIX + id, description);
        createSeriesAndCapturesForSource(id, description, c, captureFunctions);
        return c;
    }

    public EventTimer createEventTimer(String id, String description, CaptureFunction... captureFunctions) {
        EventTimer m = timeSeriesContext.getValueSourceFactory().createEventTimer(SOURCE_PREFIX + id, description);
        createSeriesAndCapturesForSource(id, description, m, captureFunctions);
        return m;
    }

    private void createSeriesAndCapturesForSource(String id, String description, ValueSource source, CaptureFunction... captureFunctions) {
        List<Capture> captures = new ArrayList<Capture>();
        if (captureFunctions.length == 0) {
            IdentifiableTimeSeries series = creatRawValuesTimeSeries(id, description);
            String captureId = getNextCaptureId(id);
            captures.add(timeSeriesContext.getCaptureFactory().createCapture(captureId, source, series));
        } else {
            for (CaptureFunction captureFunction : captureFunctions) {
                IdentifiableTimeSeries series = createTimeSeries(id, description, captureFunction);
                String captureId = getNextCaptureId(id);
                captures.add(timeSeriesContext.getCaptureFactory().createTimedCapture(captureId, source, series, captureFunction));
            }
        }
        addIdentifiables(captures);
    }

    private String getNextCaptureId(String id) {
        return CAPTURE_PREFIX + id + "_" + captureUniqueId.incrementAndGet();
    }


    //just in case the same Identifiable implements both Capture and ListTimeSeries for example,
    //add the set of identifiables from the capture
    private void addIdentifiables(List<Capture> captures) {
        Set<Identifiable> s = new HashSet<Identifiable>();
        for (Capture c : captures) {
            s.addAll(getAllIdentifiables(c));
        }

        for (Identifiable i : s) {
            timeSeriesContext.addChild(i);
        }
    }


    private Collection<Identifiable> getAllIdentifiables(Capture capture) {
        Set<Identifiable> s = new HashSet<Identifiable>();
        s.add(capture);
        s.add(capture.getTimeSeries());
        if (capture instanceof ValueSourceCapture) {
            s.add(((ValueSourceCapture) capture).getValueSource());
        }
        return s;
    }

    private IdentifiableTimeSeries createTimeSeries(String id, String description, CaptureFunction f) {
        return timeSeriesContext.getTimeSeriesFactory().createTimeSeries(
                timeSeriesContext.getContextPath(),
                id + " " + f.getDescription(),
                description + " " + f.getDescription() + " every " + f.getCapturePeriod()
        );
    }

    private IdentifiableTimeSeries creatRawValuesTimeSeries(String id, String description) {
        return timeSeriesContext.getTimeSeriesFactory().createTimeSeries(
                timeSeriesContext.getContextPath(),
                id,
                description
        );
    }

}
