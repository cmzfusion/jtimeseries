package com.od.jtimeseries.timeseries.function;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Mar-2010
 * Time: 18:25:07
 *
 * Sort performance is important for functions such as Percentile where we have to order the
 * values in a series. The jtimeseries server uses these functions for summary stats generation
 * so they run periodically.
 *
 * Performance of the sort function should be loglinear nlogn
 * This class provides a harness to test how much time the sorting will take
 */
public class SortPerformanceHarness {

    private static ArrayList<Double> list = new ArrayList<Double>();
    private static ArrayList<Double> clonedList;

    static {
        //10000 items in each series
        for ( int loop=0; loop < 10000; loop ++) {
            list.add(Math.random());
        }
    }

    public void testSortPerformance() {
        long startTime = System.currentTimeMillis();
        //10000 series to sort, the clone will slow it down a bit but not too significantly
        for ( int loop=0; loop < 10000; loop++) {
            clonedList = (ArrayList<Double>)list.clone();
            Collections.sort(clonedList);
        }
        System.out.println("time " + (System.currentTimeMillis() - startTime));
    }

    public static void main(String[] args) {
        new SortPerformanceHarness().testSortPerformance();
    }

}
