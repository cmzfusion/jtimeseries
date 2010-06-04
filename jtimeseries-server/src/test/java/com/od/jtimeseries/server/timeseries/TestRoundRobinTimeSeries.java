package com.od.jtimeseries.server.timeseries;

import com.od.jtimeseries.timeseries.impl.AbstractListTimeSeriesTest;
import com.od.jtimeseries.timeseries.impl.RoundRobinTimeSeries;
import com.od.jtimeseries.server.serialization.TestRoundRobinSerializer;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 19-May-2009
 * Time: 22:49:02
 * To change this template use File | Settings | File Templates.
 */
public class TestRoundRobinTimeSeries extends AbstractListTimeSeriesTest<RoundRobinTimeSeries> {

    public RoundRobinTimeSeries getTimeSeriesInstance() throws Exception {
        return new RoundRobinTimeSeries(10000);
    }

    //TODO this is not comprehensive and does not test event generation
    @Test
    public void testRoundRobin() {
        RoundRobinTimeSeries s = new RoundRobinTimeSeries(2);
        s.append(TestRoundRobinSerializer.createItemForTimestamp(1));
        s.append(TestRoundRobinSerializer.createItemForTimestamp(2));
        s.append(TestRoundRobinSerializer.createItemForTimestamp(3));
        assertEquals(2, s.size());

        assertEquals(2, s.get(0).getValue().longValue());
        assertEquals(3, s.get(1).getValue().longValue());

        s.addAll(Arrays.asList(
                TestRoundRobinSerializer.createItemForTimestamp(4),
                TestRoundRobinSerializer.createItemForTimestamp(5))
        );

        assertEquals(2, s.size());
        assertEquals(4, s.get(0).getValue().longValue());
        assertEquals(5, s.get(1).getValue().longValue());
    }

}
