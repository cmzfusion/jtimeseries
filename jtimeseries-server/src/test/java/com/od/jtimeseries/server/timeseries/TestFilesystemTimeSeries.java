package com.od.jtimeseries.server.timeseries;

import com.od.jtimeseries.timeseries.impl.AbstractListTimeSeriesTest;
import com.od.jtimeseries.server.serialization.RoundRobinSerializer;
import com.od.jtimeseries.server.serialization.TestRoundRobinSerializer;
import com.od.jtimeseries.server.timeseries.FilesystemTimeSeries;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;

import java.io.File;

import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 19-May-2009
 * Time: 10:11:54
 * To change this template use File | Settings | File Templates.
 */
public class TestFilesystemTimeSeries extends AbstractListTimeSeriesTest<FilesystemTimeSeries> {

    private RoundRobinSerializer roundRobinSerializer;

    public void setUp() throws Exception {
        roundRobinSerializer = TestRoundRobinSerializer.createTestSerializer();
        super.setUp();
    }

    public FilesystemTimeSeries getTimeSeriesInstance() throws Exception {
        TimeSeriesContext c = new DefaultTimeSeriesContext().createContextForPath("test");
        FilesystemTimeSeries s = new FilesystemTimeSeries(c, "id" + (int)(Math.random() * 100000000), "description", roundRobinSerializer, 10000, Time.seconds(10), Time.seconds(10));
        File file = roundRobinSerializer.getFile(s.getFileHeader());
        file.deleteOnExit();
        return s;
    }


    @Test
    public void testWriteBehindCache() {
        FilesystemTimeSeries series = getTimeSeries();
        assertEquals(0, series.getFileHeader().getCurrentSize());

        //size taken from the soft reference round robin series in memory, but series on disk and file header not yet updated
        series.add(createItemWithTimestamp(1));
        assertEquals(1, series.size());
        assertEquals(0, series.getFileHeader().getCurrentSize());

        //we have only appended an item, this can go in the append list, no need to reserialize the whole series
        assertEquals(1, series.getCacheAppendListSize());
        assertFalse(series.isSeriesInWriteCache());

        //trigger gc of the main round robin series in memory, the write behind cache values still remain
        series.triggerGarbageCollection();
        assertTrue(series.isSeriesCollected());
        assertEquals(0, series.getFileHeader().getCurrentSize()); //file header not yet update, file not yet changed
        //the appended item still in the append list
        assertEquals(1, series.getCacheAppendListSize());

        //size taken from the values in the write behind cache since the round robin series collected
        //file header not yet updated, no need to load the series back into memory to calc the size
        series.append(createItemWithTimestamp(2));
        assertEquals(2, series.getCacheAppendListSize());
        assertEquals(2, series.size());
        assertEquals(0, series.getFileHeader().getCurrentSize());
        assertTrue(series.isSeriesCollected());

        //flush the write behind cache
        series.flush();
        assertTrue(series.isCacheFlushed());
        assertTrue(series.isSeriesCollected());
        assertEquals(2, series.getFileHeader().getCurrentSize());  //now the header has been updated on the flush/append, so size is updated in the header
        assertEquals(2, series.size());

        series.add(createItemWithTimestamp(4));  //after this we have one item in our write behind cache
        assertEquals(3, series.size());

        //collect the main series
        series.triggerGarbageCollection();
        assertTrue(series.isSeriesCollected());
        assertEquals(1, series.getCacheAppendListSize());

        //doing an insert should trigger derserialization because we can't use the in memory append list in the cache
        //any values in the local append cache should be appended to the deserialized series and the cache cleared
        //before the insert takes place, so we end up with an up to date series in memory
        series.add(2, createItemWithTimestamp(3));
        assertFalse(series.isSeriesCollected());
        assertTrue(series.isSeriesInWriteCache());

        series.triggerGarbageCollection();  //series soft ref is now collected, but since it was changed, still referenced from write behind cache
        assertTrue(series.isSeriesCollected());
        assertTrue(series.isSeriesInWriteCache());
        assertEquals(4, series.size());
        assertEquals(1, series.get(0).getTimestamp());
        assertEquals(2, series.get(1).getTimestamp());
        assertEquals(3, series.get(2).getTimestamp());
        assertEquals(4, series.get(3).getTimestamp());
        assertTrue(series.isSeriesCollected()); //to read these values we could still access the series via the cache

        series.flush();
        assertTrue(series.isSeriesCollected());
        assertFalse(series.isSeriesInWriteCache()); //now series neither soft referenced nor in cache, but header updated
        assertEquals(4, series.getFileHeader().getCurrentSize());
        assertEquals(4, series.size());
        assertTrue(series.isSeriesCollected());  //size read from the header, no need to deserialize

        assertEquals(1, series.get(0).getTimestamp());
        assertEquals(2, series.get(1).getTimestamp());
        assertEquals(3, series.get(2).getTimestamp());
        assertEquals(4, series.get(3).getTimestamp());
        assertFalse(series.isSeriesCollected());  //to read these values again we had to deserialize

    }


}
