package com.od.jtimeseries.util.numeric;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 01-Mar-2009
 * Time: 21:23:53
 * To change this template use File | Settings | File Templates.
 */
public class TestLongNumeric extends AbstractNumericTest {

    Numeric createNumeric(long value) {
        return LongNumeric.valueOf(value);
    }
}
