package com.od.jtimeseries.identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 17/01/12
 * Time: 13:04
 * To change this template use File | Settings | File Templates.
 */
public interface FindCriteria<E> {

    public static final FindCriteria FIND_ALL = new FindCriteria() {
        public boolean matchesCriteria(Object identifiable) {
            return true;
        }
    };

    boolean matchesCriteria(E identifiable);

}
