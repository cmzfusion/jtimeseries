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
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 08/01/11
* Time: 22:54
*/
public interface QueryResult<E extends Identifiable> {

    public static final QueryResult EMPTY_RESULT = new QueryResult() {
        public Identifiable getFirstMatch() {
            return null;
        }

        public List getAllMatches() {
            return Collections.emptyList();
        }

        public int getNumberOfMatches() {
            return 0;
        }

        public boolean removeFromResults(Identifiable item) {
            return false;
        }
    };

    /**
     * @return the first item matching the query, or null if there are no matches
     */
    E getFirstMatch();

    List<E> getAllMatches();

    int getNumberOfMatches();

    boolean removeFromResults(E item);
}
