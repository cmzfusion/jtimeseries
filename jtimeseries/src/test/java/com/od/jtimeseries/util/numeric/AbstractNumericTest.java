package com.od.jtimeseries.util.numeric;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 01-Mar-2009
 * Time: 21:15:15
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractNumericTest extends Assert {

    abstract Numeric createNumeric(long value);

    @Test
    public void testEquals() {
        for ( int loop=0; loop<(Math.random() * 50); loop++) {
            long testValue = (long)(Math.random() * Long.MAX_VALUE);
            Numeric l = createNumeric(testValue);
            Numeric l2 = createNumeric(testValue);
            assertEquals(l, l2);
            assertEquals(l.hashCode(), l2.hashCode());

            l = createNumeric(-testValue);
            l2 = createNumeric(-testValue);
            assertEquals(l, l2);
            assertEquals(l.hashCode(), l2.hashCode());
        }

    }


}
