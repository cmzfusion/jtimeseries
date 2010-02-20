package com.od.jtimeseries.server.timeseries;

import com.od.jtimeseries.timeseries.impl.AbstractListTimeSeriesTest;
import com.od.jtimeseries.timeseries.ListTimeSeries;
import com.od.jtimeseries.server.serialization.FileHeader;
import com.od.jtimeseries.server.serialization.RoundRobinSerializer;
import com.od.jtimeseries.server.serialization.TestRoundRobinSerializer;
import com.od.jtimeseries.server.timeseries.FilesystemTimeSeries;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 19-May-2009
 * Time: 10:11:54
 * To change this template use File | Settings | File Templates.
 */
public class TestFilesystemTimeSeries extends AbstractListTimeSeriesTest {

    private RoundRobinSerializer roundRobinSerializer;

    public void setUp() throws Exception {
        roundRobinSerializer = TestRoundRobinSerializer.createTestSerializer();
        super.setUp();
    }

    public ListTimeSeries getTimeSeriesInstance() throws Exception {
        TimeSeriesContext c = new DefaultTimeSeriesContext().createContextForPath("test");
        FilesystemTimeSeries s = new FilesystemTimeSeries(c, "id" + (int)(Math.random() * 100000000), "description", roundRobinSerializer, 10000, Time.seconds(10), Time.seconds(10));
        File file = roundRobinSerializer.getFile(s.getFileHeader());
        file.deleteOnExit();
        return s;
    }
}
