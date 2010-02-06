package com.od.jtimeseries.timeseries.function.interpolation;

import junit.framework.Assert;
import com.od.jtimeseries.timeseries.ListTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeries;
import com.od.jtimeseries.util.numeric.LongNumeric;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 08-Mar-2009
 * Time: 01:01:15
 * To change this template use File | Settings | File Templates.
 */
public class LinearInterpolationTest extends Assert {

    @Test
    public void testLinearInterpolation() {
        ListTimeSeries t = new DefaultTimeSeries();
        t.add(new TimeSeriesItem(1000, LongNumeric.valueOf(1000)));
        t.add(new TimeSeriesItem(2000, LongNumeric.valueOf(2000)));

        LinearInterpolationFunction l = new LinearInterpolationFunction();

        TimeSeriesItem i = l.calculateInterpolatedValue(t, 1000, t.get(0), t.get(0));
        assertEquals(1000, i.getValue().longValue());

        i = l.calculateInterpolatedValue(t, 1500, t.get(0), t.get(1));
        assertEquals(1500, i.getValue().longValue());

        i = l.calculateInterpolatedValue(t, 1750, t.get(0), t.get(1));
        assertEquals(1750, i.getValue().longValue());

        t.add(new TimeSeriesItem(3000, LongNumeric.valueOf(3)));
        t.add(new TimeSeriesItem(4000, LongNumeric.valueOf(4)));
        i = l.calculateInterpolatedValue(t, 3500, t.get(2), t.get(3));
        assertEquals(3.5, i.getValue().doubleValue());
    }
}
