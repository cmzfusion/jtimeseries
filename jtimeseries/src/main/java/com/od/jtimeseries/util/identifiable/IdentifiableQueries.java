package com.od.jtimeseries.util.identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 08/01/11
 * Time: 23:16
 */
public interface IdentifiableQueries {

    <E extends Identifiable> QueryResult<E> findAll(Class<E> assignableToClass);

    <E extends Identifiable> QueryResult<E> findAll(String searchPattern, Class<E> assignableToClass);
}
