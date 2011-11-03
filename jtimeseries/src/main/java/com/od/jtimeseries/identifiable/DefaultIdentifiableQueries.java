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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 08/01/11
 * Time: 23:08
 */
public class DefaultIdentifiableQueries implements IdentifiableQueries {

    private Identifiable identifiable;

    public DefaultIdentifiableQueries(Identifiable identifiable) {
        this.identifiable = identifiable;
    }

    public <E extends Identifiable> QueryResult<E> findAll(Class<E> assignableToClass) {
        QueryResult<E> result = QueryResult.EMPTY_RESULT;
        if ( identifiable.getChildCount() > 0) {
             List<E> children = new ArrayList<E>();
             addAllIdentifiableMatchingClassRecursive(children, identifiable, assignableToClass);
             result = new DefaultQueryResult<E>(children);
        }
        return result;
    }

    public <E extends Identifiable> QueryResult<E> findAll(String searchPattern, Class<E> assignableToClass) {
        QueryResult<E> result = QueryResult.EMPTY_RESULT;
        if ( identifiable.getChildCount() > 0) {
            result = new DefaultQueryResult<E>(
                findAllMatchingSearchPattern(searchPattern, findAll(assignableToClass).getAllMatches())
            );
        }
        return result;
    }

    protected <E extends Identifiable> List<E> findAllMatchingSearchPattern(String searchPattern, List<E> identifiables) {
        Pattern p = Pattern.compile(searchPattern);
        List<E> result = new ArrayList<E>();
        for ( E i : identifiables) {
            if ( p.matcher((i).getPath()).find() ) {
                result.add(i);
            }
        }
        return result;
    }

    protected <E extends Identifiable> void addAllIdentifiableMatchingClassRecursive(List<E> l, Identifiable identifiable, Class<E> clazz) {
        for ( Identifiable i : identifiable.getChildren()) {
            if ( clazz.isAssignableFrom(i.getClass())) {
                l.add((E)i);
            }
            addAllIdentifiableMatchingClassRecursive(l, i, clazz);
        }
    }

}
