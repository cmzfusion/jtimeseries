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
import com.od.jtimeseries.context.ContextQueries;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.source.ValueSource;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.scheduling.Scheduler;
import com.od.jtimeseries.scheduling.Triggerable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class DefaultContextQueries implements ContextQueries {

    private TimeSeriesContext timeSeriesContext;

    public DefaultContextQueries(TimeSeriesContext timeSeriesContext) {
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

    public <E extends Identifiable> QueryResult<E> findAllChildren(Class<E> assignableToClass) {
        List<E> children = new ArrayList<E>();
        addAllIdentifiableMatchingClassRecursive(children, timeSeriesContext, assignableToClass);
        return new DefaultQueryResult<E>(children);
    }

    public <E extends Identifiable> QueryResult<E> findAllChildren(String searchPattern, Class<E> assignableToClass) {
        return new DefaultQueryResult<E>(
            findAllMatchingSearchPattern(searchPattern, findAllChildren(assignableToClass).getAllMatches())
        );
    }

    private <E extends Identifiable> List<E> findAllMatchingSearchPattern(String searchPattern, List<E> identifiables) {
        Pattern p = Pattern.compile(searchPattern);
        List<E> result = new ArrayList<E>();
        for ( E i : identifiables) {
            if ( p.matcher(i.getPath()).find() ) {
                result.add(i);
            }
        }
        return result;
    }

    private <E> void addAllIdentifiableMatchingClassRecursive(List<E> valueSources, Identifiable identifiable, Class<E> clazz) {
        for ( Identifiable i : identifiable.getChildren()) {
            if ( clazz.isAssignableFrom(i.getClass())) {
                valueSources.add((E)i);
            }
            addAllIdentifiableMatchingClassRecursive(valueSources, i, clazz);
        }
    }

    private abstract class QueryByCapture<E> {

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

    private static class DefaultQueryResult<E> implements QueryResult<E> {
        private List<E> results;

        public DefaultQueryResult(E result) {
            this.results = new LinkedList<E>();
            results.add(result);
        }

        public DefaultQueryResult(List<E> results) {
            this.results = new LinkedList<E>(results);
        }

        public E getFirstMatch() {
            return results.size() > 0 ? results.get(0) : null;
        }

        public List<E> getAllMatches() {
            return new LinkedList<E>(results);
        }

        public int getNumberOfMatches() {
            return results.size();
        }

        public boolean removeFromResults(E result) {
            return results.remove(result);
        }
    }
}
