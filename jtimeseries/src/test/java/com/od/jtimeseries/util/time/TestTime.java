package com.od.jtimeseries.util.time;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 10-May-2010
 * Time: 22:46:45
 * To change this template use File | Settings | File Templates.
 */
public class TestTime extends TestCase {

    //31 days as millis overflows an int value which was a silly bug I fixed
    //check that never regresses
    public void testNoNumericOverflows() {
        long millisIn31Days = (long) 24 * 60 * 60 * 1000 * 31;

        long millis = Time.seconds(31 * 24 * 60 * 60).getLengthInMillis();
        assertEquals(millisIn31Days, millis);

        millis = Time.minutes(31 * 24 * 60).getLengthInMillis();
        assertEquals(millisIn31Days, millis);

        millis = Time.hours(31 * 24).getLengthInMillis();
        assertEquals(millisIn31Days, millis);

        millis = Time.days(31).getLengthInMillis();
        assertEquals(millisIn31Days, millis);
    }

}
