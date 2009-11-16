package com.od.jtimeseries.util.numeric;

import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 01-Mar-2009
 * Time: 21:24:21
 * To change this template use File | Settings | File Templates.
 */
public class TestDoubleNumeric extends AbstractNumericTest{

    Numeric createNumeric(long value) {
        return new DoubleNumeric((double)value);
    }

}
