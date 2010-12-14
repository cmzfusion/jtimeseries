package com.od.jtimeseries.util.identifiable;

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.source.ValueRecorder;
import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 14-Dec-2010
 * Time: 18:58:21
 * To change this template use File | Settings | File Templates.
 */
public class TestIdentifiableTreeEvent extends TestCase {


    public void testEventFiredWhenNodesAddedAndRemoved() {
        TimeSeriesContext rootContext = JTimeSeries.createRootContext();

        final LinkedList<String[]> expectedRootEvents = new LinkedList<String[]>();
        expectedRootEvents.add(new String[] {"add", "", "test"});
        expectedRootEvents.add(new String[] {"add", "test", "grandChildContext"});
        expectedRootEvents.add(new String[] {"add", "test.grandChildContext", "valueSource"});
        expectedRootEvents.add(new String[] {"remove", "test.grandChildContext", "valueSource"});

        //paths of events should be relative to grandchild node
        final LinkedList<String[]> expectedGrandChildEvents = new LinkedList<String[]>();
        expectedGrandChildEvents.add(new String[] {"add", "", "valueSource"});
        expectedGrandChildEvents.add(new String[] {"remove", "", "valueSource"});

        final CountDownLatch c = new CountDownLatch(expectedRootEvents.size() + expectedGrandChildEvents.size());

        rootContext.addTreeListener(new TreeEventTestListener(expectedRootEvents, c));

        TimeSeriesContext grandChild = rootContext.createContext(
            "test.grandChildContext"
        );

        //add a local listener to the grandchild context
        grandChild.addTreeListener(new TreeEventTestListener(expectedGrandChildEvents, c));

        ValueRecorder r = grandChild.createValueRecorder("valueSource", "valueSource");
        grandChild.removeChild(r);

        try {
            boolean success = c.await(1000, TimeUnit.MILLISECONDS);
            if ( !success) {
                fail();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //check this event matches the first in the list of expected events
    //remove the first event from the list
    private void checkExpectedEvent(String type, IdentifiableTreeEvent contextTreeEvent, LinkedList<String[]> expectedEvents, CountDownLatch c) {
        assertFalse(expectedEvents.size() == 0);
        String[] event = expectedEvents.removeFirst();
        assertEquals(event[0], type);
        assertEquals(event[1], contextTreeEvent.getPath());
        assertEquals(event[2], contextTreeEvent.getNodes().get(0).getId());
        c.countDown();
    }

    private class TreeEventTestListener implements IdentifiableTreeListener {
        private final LinkedList<String[]> expectedRootEvents;
        private final CountDownLatch c;

        public TreeEventTestListener(LinkedList<String[]> expectedRootEvents, CountDownLatch c) {
            this.expectedRootEvents = expectedRootEvents;
            this.c = c;
        }

        public void nodesChanged(IdentifiableTreeEvent contextTreeEvent) {
            checkExpectedEvent("change", contextTreeEvent, expectedRootEvents, c);
        }

        public void nodesAdded(IdentifiableTreeEvent contextTreeEvent) {
            checkExpectedEvent("add", contextTreeEvent, expectedRootEvents, c);
        }

        public void nodesRemoved(IdentifiableTreeEvent contextTreeEvent) {
            checkExpectedEvent("remove", contextTreeEvent, expectedRootEvents, c);
        }
    }
}
