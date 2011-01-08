package com.od.jtimeseries.util.identifiable;

import com.od.jtimeseries.util.identifiable.Identifiable;

import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 08/01/11
* Time: 22:54
*/
public interface QueryResult<E extends Identifiable> {

    /**
     * @return the first item matching the query, or null if there are no matches
     */
    E getFirstMatch();

    List<E> getAllMatches();

    int getNumberOfMatches();

    boolean removeFromResults(E item);
}
