package com.od.jtimeseries.timeseries.util;

import com.od.jtimeseries.timeseries.*;
import com.od.jtimeseries.util.numeric.DoubleNumeric;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/08/11
 * Time: 22:56
 *
 * Many utility methods on SeriesUtils attempt to get items by index from the source timeSeries, which
 * would be very inefficient for non-indexed series. Better support for non-indexed series types may be
 * added in the future.
 *
 * Take care that you use SeriesUtils methods only with IndexedTimeSeries, unless you are sure the
 * utility method in question does not require random access to items
 */
public class SeriesUtils {

    public static int binarySearch(TimeSeries series, TimeSeriesItem key, Comparator<? super TimeSeriesItem> c) {
        int low = 0;
        int high = series.size()-1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            TimeSeriesItem midVal = series.getItem(mid);
            int cmp = c.compare(midVal, key);

            if (cmp < 0)
            low = mid + 1;
            else if (cmp > 0)
            high = mid - 1;
            else
            return mid; // key found
        }
        return -(low + 1);  // key not found
    }

    /**
     * @return  index of the first item in the series with a timestamp equal to or later than the supplied timestamp
     */
    public static int getIndexOfFirstItemAtOrAfter(long timestamp, TimeSeries timeSeries) {
        int index = binarySearchByTimestamp(timestamp, timeSeries);
        if ( index >= 0) {
            index = findLowestIndexWithTimestamp(timestamp, index, timeSeries);
        } else {
            index = -index-1;
            index = index < timeSeries.size() ? index : -1;
        }
        return index;
    }

    /**
     * @return  index of the first item in the series with a timestamp equal to or earlier than the supplied timestamp
     */
    public static int getIndexOfFirstItemAtOrBefore(long timestamp, TimeSeries timeSeries) {
        int index = binarySearchByTimestamp(timestamp, timeSeries);
        if ( index >= 0) {
            index = findHighestIndexWithTimestamp(timestamp, index, timeSeries);
        } else {
            index = -index - 2;
        }
        return index;
    }

    private static int findLowestIndexWithTimestamp(long timestamp, int index, TimeSeries timeSeries) {
        while ( index > 0) {
            if ( timeSeries.getItem(index - 1).getTimestamp() == timestamp) {
                index--;
            } else {
                break;
            }
        }
        return index;
    }

    private static int findHighestIndexWithTimestamp(long timestamp, int index, TimeSeries timeSeries) {
        while ( index + 1 < timeSeries.size()) {
            if ( timeSeries.getItem(index + 1).getTimestamp() == timestamp) {
                index++;
            } else {
                break;
            }
        }
        return index;
    }

    public static TimeSeriesItem getFirstItemAtOrBefore(long timestamp, TimeSeries timeSeries) {
        TimeSeriesItem result = null;
        int index = getIndexOfFirstItemAtOrBefore(timestamp, timeSeries);
        if ( index > -1 ) {
            result = timeSeries.getItem(index);
        }
        return result;
    }

    public static TimeSeriesItem getFirstItemAtOrAfter(long timestamp, TimeSeries timeSeries) {
        TimeSeriesItem result = null;
        int index = getIndexOfFirstItemAtOrAfter(timestamp, timeSeries);
        if ( index > -1 ) {
            result = timeSeries.getItem(index);
        }
        return result;
    }

    public static long getTimestampAfter(long timestamp, TimeSeries timeSeries) {
        TimeSeriesItem item = getFirstItemAtOrAfter(timestamp + 1, timeSeries);
        return item != null ? item.getTimestamp() : -1;
    }

    public static long getTimestampBefore(long timestamp, TimeSeries timeSeries) {
        TimeSeriesItem item = getFirstItemAtOrBefore(timestamp - 1, timeSeries);
        return item != null ? item.getTimestamp() : -1;
    }

    public static List<TimeSeriesItem> getItemsInRange(long earliest, long latest, TimeSeries timeSeries) {
        int startIndex = getIndexOfFirstItemAtOrAfter(earliest, timeSeries);
        int endIndex = getIndexOfFirstItemAtOrBefore(latest, timeSeries);
        List<TimeSeriesItem> result;
        if ( startIndex != -1 && endIndex != -1) {
            result = subList(startIndex,endIndex + 1, timeSeries);
        } else {
            result = Collections.emptyList();
        }
        return result;
    }

    private static List<TimeSeriesItem> subList(int startIndex, int endIndex, TimeSeries timeSeries) {
        List<TimeSeriesItem> items = new ArrayList<TimeSeriesItem>();
        for ( int loop=startIndex; loop < endIndex;loop ++) {
            items.add(timeSeries.getItem(loop));
        }
        return items;
    }

    public static int binarySearchByTimestamp(long timestamp, TimeSeries series) {
        return binarySearch(
            series,
            new DefaultTimeSeriesItem(timestamp, DoubleNumeric.valueOf(0)),
            new Comparator<TimeSeriesItem>() {
                public int compare(TimeSeriesItem o1, TimeSeriesItem o2) {
                    return o1.getTimestamp() == o2.getTimestamp() ? 0 :
                            o1.getTimestamp() < o2.getTimestamp() ? -1 : 1;
                }
            }
        );
    }

    /**
     *  Get all items from TimeSeries which have a timestamp >= t
     */
    public static List<TimeSeriesItem> getSubSeries(long t, IdentifiableTimeSeries timeSeries) {
        return timeSeries.getItemsInRange(t, Long.MAX_VALUE);
    }

    /**
     * Add all the items in the collection to the supplied timeseries
     */
    public static void addAll(Collection<TimeSeriesItem> items, TimeSeries s) {
        for (TimeSeriesItem item : items) {
            s.addItem(item);
        }
    }

    /**
     * Add all the items in source series to the supplied timeseries
     */
    public static void addAll(TimeSeries sourceSeries, TimeSeries s) {
        for (TimeSeriesItem item : sourceSeries) {
            s.addItem(item);
        }
    }

    public static boolean areTimeSeriesEqualByItems(TimeSeries one, TimeSeries two) {
        boolean result = one.size() == two.size();
        if (result) {
            Iterator<TimeSeriesItem> twoIterator = two.iterator();
            Iterator<TimeSeriesItem> oneIterator = one.iterator();
            while(oneIterator.hasNext()) {
                TimeSeriesItem oneItem = oneIterator.next();
                TimeSeriesItem twoItem = twoIterator.next();
                if ( ! oneItem.equals(twoItem)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    public static int hashCodeByItems(TimeSeries s) {
       int hashCode = 1;
       Iterator<TimeSeriesItem> i = s.iterator();
       while (i.hasNext()) {
           TimeSeriesItem obj = i.next();
           hashCode = 31*hashCode + (obj==null ? 0 : obj.hashCode());
       }
       return hashCode;
    }

    public static boolean fallsWithinRange(long timeStamp, long startTime, long endTime) {
        return timeStamp >= startTime && timeStamp <= endTime;
    }
}
