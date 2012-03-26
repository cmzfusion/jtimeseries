package com.od.jtimeseries.identifiable;

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.context.TimeSeriesContext;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26/03/12
 * Time: 18:04
 */
public class TestIdentifiable extends TestCase {

    public void testDescriptionTooLong() {
        TimeSeriesContext c = JTimeSeries.createRootContext();

        boolean failed = false;
        try {
        c.createValueRecorder("test", "1234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
        } catch (IdentifierException i) {
            failed = true;
        }
        assertTrue("Should fail to create description > 256 chars", failed);
    }


}
