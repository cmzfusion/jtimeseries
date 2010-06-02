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
package com.od.jtimeseries.context;

import com.od.jtimeseries.capture.Capture;
import com.od.jtimeseries.scheduling.Scheduler;
import com.od.jtimeseries.scheduling.Triggerable;
import com.od.jtimeseries.source.ValueSource;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.util.identifiable.Identifiable;

import java.util.List;

/**
 * There is a many to many relationship between ValueSource and TimeSeries - the Capture is the mapping entity
 * between the two
 *
 * By far the most common case will be that a ValueSource provides values which are captured to one or more time series
 * (using one or more aggregate functions).
 *
 * It is permitted (although perhaps a less common requirement) to capture values from several value sources into the
 * same time series.
 *
 * All methods starting findFirst return the first entity found in the search which matches the parameters specified -
 * in the case that more than one entity matches the search parameters only the first matching instance is returned,
 * and this result is not guaranteed to be consistent on subsequent searches.
 */
public interface ContextQueries {

    QueryResult<IdentifiableTimeSeries> findTimeSeries(CaptureCriteria criteria);

    QueryResult<IdentifiableTimeSeries> findTimeSeries(ValueSource source);

    QueryResult<IdentifiableTimeSeries> findTimeSeries(String searchPattern);

    QueryResult<IdentifiableTimeSeries> findAllTimeSeries();


    QueryResult<Capture> findCaptures(String searchPattern);

    QueryResult<Capture> findCaptures(CaptureCriteria criteria);

    QueryResult<Capture> findCaptures(ValueSource valueSource);

    QueryResult<Capture> findCaptures(IdentifiableTimeSeries timeSeries);

    QueryResult<Capture> findAllCaptures();


    QueryResult<ValueSource> findValueSources(CaptureCriteria criteria);

    QueryResult<ValueSource> findValueSources(IdentifiableTimeSeries timeSeries);

    QueryResult<ValueSource> findValueSources(String searchPattern);

    QueryResult<ValueSource> findAllValueSources();


    QueryResult<Scheduler> findAllSchedulers();

    QueryResult<Scheduler> findSchedulers(String searchPattern);
    
    QueryResult<Scheduler> findSchedulers(Triggerable triggerable);


    <E> QueryResult<E> findAll(Class<E> assignableToClass);

    <E> QueryResult<E> findAll(String searchPattern, Class<E> assignableToClass);


    public static interface CaptureCriteria {
        boolean isMatchingCapture(Capture c);
    }


    public static interface QueryResult<E> {

        /**
         * @return the first item matching the query, or null if there are no matches
         */
        E getFirstMatch();

        List<E> getAllMatches();

        int getNumberOfMatches();

        boolean removeFromResults(E item);
    }

}
