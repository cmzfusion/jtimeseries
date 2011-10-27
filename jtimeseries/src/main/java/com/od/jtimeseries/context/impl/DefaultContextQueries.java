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
package com.od.jtimeseries.context.impl;

import com.od.jtimeseries.capture.Capture;
import com.od.jtimeseries.capture.ValueSourceCapture;
import com.od.jtimeseries.context.ContextQueries;
import com.od.jtimeseries.identifiable.DefaultIdentifiableQueries;
import com.od.jtimeseries.identifiable.DefaultQueryResult;
import com.od.jtimeseries.identifiable.QueryResult;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.source.ValueSource;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.scheduling.Scheduler;
import com.od.jtimeseries.scheduling.Triggerable;

import java.util.ArrayList;
import java.util.List;

public class DefaultContextQueries extends DefaultIdentifiableQueries implements ContextQueries {

    private TimeSeriesContext timeSeriesContext;

    public DefaultContextQueries(TimeSeriesContext timeSeriesContext) {
        super(timeSeriesContext);
        this.timeSeriesContext = timeSeriesContext;
    }

    public QueryResult<IdentifiableTimeSeries> findTimeSeries(final CaptureCriteria criteria) {
        return new DefaultQueryResult<IdentifiableTimeSeries>(
            new QueryByCapture<IdentifiableTimeSeries>() {

                IdentifiableTimeSeries getResult(Capture c) {
                    return c.getTimeSeries();
                }

                boolean meetsCriteria(Capture c) {
                    return criteria.isMatchingCapture(c);
                }
            }.findByCaptures()
        );
    }

    public QueryResult<IdentifiableTimeSeries> findTimeSeries(final ValueSource source) {
        return new DefaultQueryResult<IdentifiableTimeSeries>(
            new QueryByCapture<IdentifiableTimeSeries>() {

                IdentifiableTimeSeries getResult(Capture c) {
                    return c.getTimeSeries();
                }

                boolean meetsCriteria(Capture c) {
                    return (c instanceof ValueSourceCapture &&
                           ((ValueSourceCapture)c).getValueSource() == source);
                }
            }.findByCaptures()
        );
    }

    public QueryResult<IdentifiableTimeSeries> findTimeSeries(String searchPattern) {
        return new DefaultQueryResult<IdentifiableTimeSeries>(
            findAllMatchingSearchPattern(searchPattern, findAllTimeSeries().getAllMatches())
        );
    }

    public QueryResult<IdentifiableTimeSeries> findAllTimeSeries() {
        List<IdentifiableTimeSeries> timeSeries = new ArrayList<IdentifiableTimeSeries>();
        addAllIdentifiableMatchingClassRecursive(timeSeries,  timeSeriesContext, IdentifiableTimeSeries.class);
        return new DefaultQueryResult<IdentifiableTimeSeries>(timeSeries);
    }

    public QueryResult<Capture> findCaptures(String searchPattern) {
        return new DefaultQueryResult<Capture>(
            findAllMatchingSearchPattern(searchPattern, findAllCaptures().getAllMatches())
        );
    }

    public QueryResult<Capture> findCaptures(final CaptureCriteria criteria) {
        return new DefaultQueryResult<Capture>(
            new QueryByCapture<Capture>() {

                Capture getResult(Capture c) {
                    return c;
                }

                boolean meetsCriteria(Capture c) {
                    return criteria.isMatchingCapture(c);
                }
            }.findByCaptures()
        );
    }

    public QueryResult<Capture> findCaptures(final ValueSource valueSource) {
        return new DefaultQueryResult<Capture>(
            new QueryByCapture<Capture>() {

                Capture getResult(Capture c) {
                    return c;
                }

                boolean meetsCriteria(Capture c) {
                    return (c instanceof ValueSourceCapture &&
                           ((ValueSourceCapture)c).getValueSource() == valueSource);
                }
            }.findByCaptures()
        );
    }

    public QueryResult<Capture> findCaptures(final IdentifiableTimeSeries timeSeries) {
         return new DefaultQueryResult<Capture>(
            new QueryByCapture<Capture>() {

                Capture getResult(Capture c) {
                    return c;
                }

                boolean meetsCriteria(Capture c) {
                    return c.getTimeSeries() == timeSeries;
                }
            }.findByCaptures()
        );
    }

    public QueryResult<Capture> findAllCaptures() {
        List<Capture> captures = new ArrayList<Capture>();
        addAllIdentifiableMatchingClassRecursive(captures, timeSeriesContext, Capture.class);
        return new DefaultQueryResult<Capture>(captures);
    }

    public QueryResult<ValueSource> findValueSources(final CaptureCriteria criteria) {
        return new DefaultQueryResult<ValueSource>(
            new QueryByCapture<ValueSource>() {

                ValueSource getResult(Capture c) {
                    return c instanceof ValueSourceCapture ? ((ValueSourceCapture)c).getValueSource() : null;
                }

                boolean meetsCriteria(Capture c) {
                    return criteria.isMatchingCapture(c);
                }
            }.findByCaptures()
        );
    }

    public QueryResult<ValueSource> findValueSources(final IdentifiableTimeSeries timeSeries) {
        return new DefaultQueryResult<ValueSource>(
            new QueryByCapture<ValueSource>() {

                ValueSource getResult(Capture c) {
                    return c instanceof ValueSourceCapture ? ((ValueSourceCapture)c).getValueSource() : null;
                }

                boolean meetsCriteria(Capture c) {
                    return c.getTimeSeries() == timeSeries;
                }
            }.findByCaptures()
        );
    }

    public QueryResult<ValueSource> findValueSources(String searchPattern) {
        return new DefaultQueryResult<ValueSource>(
            findAllMatchingSearchPattern(searchPattern, findAllValueSources().getAllMatches())
        );
    }

    public QueryResult<ValueSource> findAllValueSources() {
        List<ValueSource> valueSources = new ArrayList<ValueSource>();
        addAllIdentifiableMatchingClassRecursive(valueSources, timeSeriesContext, ValueSource.class);
        return new DefaultQueryResult<ValueSource>(valueSources);
    }

    public QueryResult<Scheduler> findAllSchedulers() {
        List<Scheduler> schedulers = new ArrayList<Scheduler>();
        addAllIdentifiableMatchingClassRecursive(schedulers, timeSeriesContext, Scheduler.class);
        return new DefaultQueryResult<Scheduler>(schedulers);
    }

    public QueryResult<Scheduler> findSchedulers(String searchPattern) {
        return new DefaultQueryResult<Scheduler>(
            findAllMatchingSearchPattern(searchPattern, findAllSchedulers().getAllMatches())
        );
    }

    public QueryResult<Scheduler> findSchedulers(Triggerable capture) {
        QueryResult<Scheduler> result = findAllSchedulers();
        for (Scheduler c : result.getAllMatches()) {
            if ( ! c.containsTriggerable(capture) ) {
                result.removeFromResults(c);
            }
        }
        return result;
    }

    private abstract class QueryByCapture<E extends Identifiable> {

        public List<E> findByCaptures() {
            List<E> identifiables = new ArrayList<E>();
            List<Capture> captures = findAllCaptures().getAllMatches();
            E result;
            for ( Capture c : captures) {
                if ( meetsCriteria(c)) {
                    result = getResult(c);
                    if ( result != null) {
                        identifiables.add(result);
                    }
                }
            }
            return identifiables;
        }

        abstract E getResult(Capture c);

        abstract boolean meetsCriteria(Capture c);
    }

}
