package com.od.jtimeseries.util.identifiable;

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
        List<E> children = new ArrayList<E>();
        addAllIdentifiableMatchingClassRecursive(children, identifiable, assignableToClass);
        return new DefaultQueryResult<E>(children);
    }

    public <E extends Identifiable> QueryResult<E> findAll(String searchPattern, Class<E> assignableToClass) {
        return new DefaultQueryResult<E>(
            findAllMatchingSearchPattern(searchPattern, findAll(assignableToClass).getAllMatches())
        );
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
