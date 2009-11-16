package com.od.jtimeseries.util.numeric;

import junit.framework.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06-Mar-2009
 * Time: 16:59:27
 */
public class NumericTests extends Assert {

    @Test
    public void testNumericsOfDifferentTypeCannotBeEqual() {
        //In this case, Double.MAX_VALUE cannot be represented as a Long
        //clearly, here the two are not equal
        DoubleNumeric d = new DoubleNumeric(Double.MAX_VALUE);
        LongNumeric l = new LongNumeric(new Double(Double.MAX_VALUE).longValue());
        assertFalse(d.equals(l));

        //two Numerics with different class are not equal - even if they both represent the same exact value
        d = new DoubleNumeric(10d);
        l = new LongNumeric(10);
        assertFalse(d.equals(l));

        Long longVal = new Long(10);
        Double doubleVal = new Double(10);
        assertFalse(longVal.equals(doubleVal));
    }
}
