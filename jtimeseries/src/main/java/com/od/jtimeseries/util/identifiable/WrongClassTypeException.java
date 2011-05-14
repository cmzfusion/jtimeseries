package com.od.jtimeseries.util.identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 10-Jun-2010
 * Time: 13:48:48
 *
 * Thrown when you try to get an Identifiable from a TimeSeriesContext by path, but the
 * matching instance by path has the wrong class type
 */
public class WrongClassTypeException extends RuntimeException {

    public WrongClassTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongClassTypeException(String message) {
        super(message);
    }
}
