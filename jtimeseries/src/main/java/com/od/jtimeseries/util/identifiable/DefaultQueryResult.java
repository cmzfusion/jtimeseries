package com.od.jtimeseries.util.identifiable;

import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.QueryResult;

import java.util.LinkedList;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 08/01/11
* Time: 22:59
*/
public class DefaultQueryResult<E extends Identifiable> implements QueryResult<E> {
    private List<E> results;

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
