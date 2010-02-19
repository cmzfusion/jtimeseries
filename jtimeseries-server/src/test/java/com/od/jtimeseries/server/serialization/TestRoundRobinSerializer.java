package com.od.jtimeseries.server.serialization;

import com.od.jtimeseries.server.timeseries.FilesystemTimeSeries;
import com.od.jtimeseries.server.util.ServerDefaults;
import com.od.jtimeseries.timeseries.ListTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.time.Time;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 17-May-2009
 * Time: 23:46:40
 * To change this template use File | Settings | File Templates.
 */
public class TestRoundRobinSerializer extends TestCase {

    private RoundRobinSerializer serializer;
    private FileHeader fileHeader = new FileHeader("test.path", "test series", 10000);
    private boolean deleteScheduled;

    public void setUp() throws SerializationException {
        serializer = createTestSerializer();
        RoundRobinTimeSeries r = createTestSeries();
        serializer.serialize(fileHeader, r);

        serializer.getFile(fileHeader).deleteOnExit();
        serializer.getRootDirectory().deleteOnExit();        
    }

    public void testDeserialization() throws SerializationException {
        RoundRobinTimeSeries d = serializer.deserialize(fileHeader);
        RoundRobinTimeSeries r = createTestSeries();
        assertEquals(r, d);
    }

    public void testReadHeader() throws SerializationException {
        serializer.updateHeader(fileHeader);
        assertEquals(512, fileHeader.getHeaderLength());
    }

    public void testWriteEmptyFile() throws SerializationException {
        RoundRobinTimeSeries s = new RoundRobinTimeSeries(5);
        serializer.serialize(fileHeader, s);
        assertEquals(-1, fileHeader.getCurrentHead());

        serializer.updateHeader(fileHeader);
        assertEquals(-1, fileHeader.getCurrentHead());

        List<TimeSeriesItem> l = new ArrayList<TimeSeriesItem>();
        addNewItemsForTimestamps(l,1,2,3);
        serializer.append(fileHeader, l);
        assertEquals(0, fileHeader.getCurrentHead());

        RoundRobinTimeSeries d = serializer.deserialize(fileHeader);
        assertEquals(l, d);
    }

    public void testAppend() throws SerializationException {
        List<TimeSeriesItem> l = new ArrayList<TimeSeriesItem>();
        addNewItemsForTimestamps(l, 5, 6);

        serializer.append(fileHeader, l);
        assertEquals(0, fileHeader.getCurrentHead());
        assertEquals(6, fileHeader.getCurrentTail());

        serializer.updateHeader(fileHeader);
        assertEquals(0, fileHeader.getCurrentHead());
        assertEquals(6, fileHeader.getCurrentTail());

        RoundRobinTimeSeries d = serializer.deserialize(fileHeader);
        RoundRobinTimeSeries c = createTestSeries();
        addNewItemsForTimestamps(c, 5, 6);
        assertEquals(c, d);
    }

    public void testAppend2() throws SerializationException {
        //test wrap around - 8 should end up at position zero
        List<TimeSeriesItem> l = new ArrayList<TimeSeriesItem>();
        addNewItemsForTimestamps(l,5,6,7,8);

        serializer.append(fileHeader, l);
        assertEquals(1, fileHeader.getCurrentHead());
        assertEquals(1, fileHeader.getCurrentTail());

        serializer.updateHeader(fileHeader);
        assertEquals(1, fileHeader.getCurrentHead());
        assertEquals(1, fileHeader.getCurrentTail());

        RoundRobinTimeSeries d = serializer.deserialize(fileHeader);
        RoundRobinTimeSeries c = createTestSeries();
        addNewItemsForTimestamps(c,5,6,7,8);
        assertEquals(c, d);

        //rr strucure is now full, both head and tail advance when we add
        l.clear();
        l.add(createItemForTimestamp(9));
        serializer.append(fileHeader, l);

        assertEquals(2, fileHeader.getCurrentHead());
        assertEquals(2, fileHeader.getCurrentTail());

        d = serializer.deserialize(fileHeader);
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
        serializer.append(fileHeader, l);
        d = serializer.deserialize(fileHeader);
        addNewItemsForTimestamps(c, itemsForTimestamps);
        assertEquals(c, d);
    }

    public void testFilesystemTimeSeries() throws SerializationException {
        FilesystemTimeSeries series = new FilesystemTimeSeries("id", "description", serializer, fileHeader, Time.seconds(10), Time.seconds(10));
        assertEquals(4, series.size());
        assertEquals(4, series.get(3).longValue());

        addNewItemsForTimestamps(series, 5,6);
        assertEquals(6, series.size());
        series.flush();
        assertEquals(6, series.get(5).longValue());

        ListTimeSeries s = serializer.deserialize(fileHeader);
        assertEquals(6, s.get(5).longValue());

        series.removeAll(Arrays.asList(createItemForTimestamp(3), createItemForTimestamp(4)));
        assertEquals(4, series.size());
        series.flush();

        s = serializer.deserialize(fileHeader);
        assertEquals(4, s.size());
        List<TimeSeriesItem> l = new ArrayList<TimeSeriesItem>();
        addNewItemsForTimestamps(l, 1,2,5,6);
        assertEquals(l, s);
    }

    //usually we use a write behind cache to write several updates in one go so this is the worst case scenario
    public void testAppendSpeed() throws SerializationException {
        long startTime = System.currentTimeMillis();
        for ( int loop=0; loop < 1000; loop++) {
            serializer.append(fileHeader, Collections.singletonList(new TimeSeriesItem(loop, DoubleNumeric.valueOf(loop))));
        }
        long endTime = System.currentTimeMillis();
        assertEquals(7, fileHeader.getCurrentSize());
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
        return new RoundRobinSerializer(tmpDir, ServerDefaults.DEFAULT_TIMESERIES_SUFFIX);
    }

    private void addNewItemsForTimestamps(List<TimeSeriesItem> s, long... timestampsForItems) {
        for (long timestampsForItem : timestampsForItems) {
            s.add(createItemForTimestamp(timestampsForItem));
        }
    }

    public static TimeSeriesItem createItemForTimestamp(long timestampsForItem) {
        return new TimeSeriesItem(timestampsForItem,  DoubleNumeric.valueOf(timestampsForItem));
    }


}
