package com.od.jtimeseries.server.timeseries;

import com.od.jtimeseries.server.serialization.TestRoundRobinSerializer;
import com.od.jtimeseries.timeseries.impl.RoundRobinTimeSeries;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 19-May-2009
 * Time: 22:49:02
 * To change this template use File | Settings | File Templates.
 */
public class TestRoundRobinTimeSeries extends TestCase {

    public RoundRobinTimeSeries getTimeSeriesInstance() throws Exception {
        return new RoundRobinTimeSeries(10000);
    }

    //TODO this is not comprehensive and does not test event generation
    @Test
    public void testRoundRobin() {
        RoundRobinTimeSeries s = new RoundRobinTimeSeries(2);
        s.addItem(TestRoundRobinSerializer.createItemForTimestamp(1));
        s.addItem(TestRoundRobinSerializer.createItemForTimestamp(2));
        s.addItem(TestRoundRobinSerializer.createItemForTimestamp(3));
        assertEquals(2, s.size());

        assertEquals(2, s.getItem(0).getValue().longValue());
        assertEquals(3, s.getItem(1).getValue().longValue());

        s.addItem(TestRoundRobinSerializer.createItemForTimestamp(4));
        s.addItem(TestRoundRobinSerializer.createItemForTimestamp(5));

        assertEquals(2, s.size());
        assertEquals(4, s.getItem(0).getValue().longValue());
        assertEquals(5, s.getItem(1).getValue().longValue());
    }

}
