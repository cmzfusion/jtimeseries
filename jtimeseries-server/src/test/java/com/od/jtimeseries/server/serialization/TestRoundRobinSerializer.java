package com.od.jtimeseries.server.serialization;

import junit.framework.TestCase;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.ListTimeSeries;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.server.serialization.FileHeader;
import com.od.jtimeseries.server.serialization.SerializationException;
import com.od.jtimeseries.server.serialization.RoundRobinSerializer;
import com.od.jtimeseries.server.timeseries.RoundRobinTimeSeries;
import com.od.jtimeseries.server.timeseries.FilesystemTimeSeries;
import com.od.jtimeseries.server.util.ServerProperties;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 17-May-2009
 * Time: 23:46:40
 * To change this template use File | Settings | File Templates.
 */
public class TestRoundRobinSerializer extends TestCase {

    private RoundRobinSerializer serializer;
    private FileHeader f = new FileHeader("test.path", "test series", 10000);
    private boolean deleteScheduled;

    public void setUp() throws SerializationException {
        serializer = createTestSerializer();
        RoundRobinTimeSeries r = createTestSeries();
        serializer.serialize(f, r);

        serializer.getFile(f).deleteOnExit();
        serializer.getRootDirectory().deleteOnExit();        
    }

    public void testDeserialization() throws SerializationException {
        RoundRobinTimeSeries d = serializer.deserialize(f);
        RoundRobinTimeSeries r = createTestSeries();
        assertEquals(r, d);
    }

    public void testReadHeader() throws SerializationException {
        serializer.updateHeader(f);
        assertEquals(512, f.getHeaderLength());
    }

    public void testWriteEmptyFile() throws SerializationException {
        RoundRobinTimeSeries s = new RoundRobinTimeSeries(5);
        serializer.serialize(f, s);
        assertEquals(-1, f.getCurrentHead());

        serializer.updateHeader(f);
        assertEquals(-1, f.getCurrentHead());

        List<TimeSeriesItem> l = new ArrayList<TimeSeriesItem>();
        addNewItemsForTimestamps(l,1,2,3);
        serializer.append(f, l);
        assertEquals(0, f.getCurrentHead());

        RoundRobinTimeSeries d = serializer.deserialize(f);
        assertEquals(l, d);
    }

    public void testAppend() throws SerializationException {
        List<TimeSeriesItem> l = new ArrayList<TimeSeriesItem>();
        addNewItemsForTimestamps(l, 5, 6);

        serializer.append(f, l);
        assertEquals(0, f.getCurrentHead());
        assertEquals(6, f.getCurrentTail());

        serializer.updateHeader(f);
        assertEquals(0, f.getCurrentHead());
        assertEquals(6, f.getCurrentTail());

        RoundRobinTimeSeries d = serializer.deserialize(f);
        RoundRobinTimeSeries c = createTestSeries();
        addNewItemsForTimestamps(c, 5, 6);
        assertEquals(c, d);
    }

    public void testAppend2() throws SerializationException {
        //test wrap around - 8 should end up at position zero
        List<TimeSeriesItem> l = new ArrayList<TimeSeriesItem>();
        addNewItemsForTimestamps(l,5,6,7,8);

        serializer.append(f, l);
        assertEquals(1, f.getCurrentHead());
        assertEquals(1, f.getCurrentTail());

        serializer.updateHeader(f);
        assertEquals(1, f.getCurrentHead());
        assertEquals(1, f.getCurrentTail());

        RoundRobinTimeSeries d = serializer.deserialize(f);
        RoundRobinTimeSeries c = createTestSeries();
        addNewItemsForTimestamps(c,5,6,7,8);
        assertEquals(c, d);

        //rr strucure is now full, both head and tail advance when we add
        l.clear();
        l.add(createItemForTimestamp(9));
        serializer.append(f, l);

        assertEquals(2, f.getCurrentHead());
        assertEquals(2, f.getCurrentTail());

        d = serializer.deserialize(f);
        c.append(createItemForTimestamp(9));
        assertEquals(c, d);

        //try adding a random number of values which may exceed the max capacity
        //we should just wrap around however many times required and end up with the last 7 items
        int toAdd = (int) (Math.random() * 100);
        long[] itemsForTimestamps = new long[toAdd];
        for ( int loop=0; loop < toAdd; loop++) {
            itemsForTimestamps[loop] = loop + 10;
        }

        l.clear();
        addNewItemsForTimestamps(l, itemsForTimestamps);
        serializer.append(f, l);
        d = serializer.deserialize(f);
        addNewItemsForTimestamps(c, itemsForTimestamps);
        assertEquals(c, d);
    }

    public void testFilesystemTimeSeries() throws SerializationException {
        FilesystemTimeSeries series = new FilesystemTimeSeries("id", "description", serializer, f, Time.seconds(10), Time.seconds(10));
        assertEquals(4, series.size());
        assertEquals(4, series.get(3).longValue());

        addNewItemsForTimestamps(series, 5,6);
        assertEquals(6, series.size());
        series.flush();
        assertEquals(6, series.get(5).longValue());

        ListTimeSeries s = serializer.deserialize(f);
        assertEquals(6, s.get(5).longValue());

        series.removeAll(Arrays.asList(createItemForTimestamp(3), createItemForTimestamp(4)));
        assertEquals(4, series.size());
        series.flush();

        s = serializer.deserialize(f);
        assertEquals(4, s.size());
        List<TimeSeriesItem> l = new ArrayList<TimeSeriesItem>();
        addNewItemsForTimestamps(l, 1,2,5,6);
        assertEquals(l, s);
    }

    //usually we use a write behind cache to write several updates in one go so this is the worst case scenario
    public void testAppendSpeed() throws SerializationException {
        long startTime = System.currentTimeMillis();
        for ( int loop=0; loop < 1000; loop++) {
            serializer.append(f, Collections.singletonList(new TimeSeriesItem(loop, new DoubleNumeric(loop))));
        }
        long endTime = System.currentTimeMillis();
        assertEquals(7, f.getCurrentSize());
        System.out.println("Time to append to 1000 series " + (endTime - startTime));
        //expected more like < 200
        assertTrue("Test append speed", (endTime - startTime) < 2000);
    }

    private RoundRobinTimeSeries createTestSeries() {
        RoundRobinTimeSeries r = new RoundRobinTimeSeries(7);
        addNewItemsForTimestamps(r,1,2,3,4);
        return r;
    }

    public static RoundRobinSerializer createTestSerializer() throws SerializationException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"), "testtimeseries");
        if ( ! tmpDir.isDirectory()) {
            if ( ! tmpDir.mkdir() ) {
                fail("Failed to create temporary timeseries directory");
                throw new SerializationException("Failed to create test dir");
            }
        }
        tmpDir.deleteOnExit();
        return new RoundRobinSerializer(tmpDir, ServerProperties.DEFAULT_TIMESERIES_SUFFIX);
    }

    private void addNewItemsForTimestamps(List<TimeSeriesItem> s, long... timestampsForItems) {
        for (long timestampsForItem : timestampsForItems) {
            s.add(createItemForTimestamp(timestampsForItem));
        }
    }

    public static TimeSeriesItem createItemForTimestamp(long timestampsForItem) {
        return new TimeSeriesItem(timestampsForItem,  new DoubleNumeric(timestampsForItem));
    }


}
