package com.od.jtimeseries.context;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 29-Nov-2009
* Time: 16:18:09
*
* Thrown when you try to add an item to a context which has the same id as another item already present.
*/
public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String message) {
        super(message);
    }
}
