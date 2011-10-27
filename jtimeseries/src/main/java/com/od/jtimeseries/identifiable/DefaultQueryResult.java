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
package com.od.jtimeseries.identifiable;

import java.util.Collections;
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
        this.results = results;
    }

    public E getFirstMatch() {
        return results.size() > 0 ? results.get(0) : null;
    }

    public List<E> getAllMatches() {
        return results.size() == 0 ? Collections.<E>emptyList() : new LinkedList<E>(results);
    }

    public int getNumberOfMatches() {
        return results.size();
    }

    public boolean removeFromResults(E result) {
        return results.remove(result);
    }
}
