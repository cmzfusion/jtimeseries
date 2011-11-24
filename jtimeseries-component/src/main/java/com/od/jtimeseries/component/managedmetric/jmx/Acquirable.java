package com.od.jtimeseries.component.managedmetric.jmx;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24/11/11
 * Time: 21:43
 */
interface Acquirable {

     void acquire();

     void release();
}
