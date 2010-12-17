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
        expectedRootEvents.add(new String[] {"change", "test.grandChildContext", "valueSource"});
        expectedRootEvents.add(new String[] {"remove", "test.grandChildContext", "valueSource"});

        //paths of events should be relative to grandchild node
        final LinkedList<String[]> expectedGrandChildEvents = new LinkedList<String[]>();
        expectedGrandChildEvents.add(new String[] {"add", "", "valueSource"});
        expectedGrandChildEvents.add(new String[] {"change", "", "valueSource"});
        expectedGrandChildEvents.add(new String[] {"remove", "", "valueSource"});

        //paths of events should be relative to grandchild node
        final LinkedList<String[]> expectedValueRecorderEvents = new LinkedList<String[]>();
        expectedValueRecorderEvents.add(new String[] {"change", "LOCAL", "valueSource"});

        int totalEventCount = expectedRootEvents.size() +
                expectedGrandChildEvents.size() +
                expectedValueRecorderEvents.size();

        final CountDownLatch cl1 = new CountDownLatch(totalEventCount);
        //total events minus the removes:
        final CountDownLatch cl2 = new CountDownLatch(totalEventCount - 2);
        MultiplexingCountDownLatch c = new MultiplexingCountDownLatch(cl1, cl2);

        rootContext.addTreeListener(new TreeEventTestListener("rootContext", expectedRootEvents, rootContext, c));

        TimeSeriesContext grandChild = rootContext.createContext(
            "test.grandChildContext"
        );
        grandChild.addTreeListener(new TreeEventTestListener("grandChild", expectedGrandChildEvents, grandChild, c));

        ValueRecorder r = grandChild.createValueRecorder("valueSource", "valueSource");
        r.addTreeListener(new TreeEventTestListener("valueSource", expectedValueRecorderEvents, r, c));

        r.fireNodeChanged("change");


        try {
            //wait for previous events to propagate before
            //firing tree structure remove, otherwise there is a danger we remove
            //before the event firing thread has fired all previous events to ancestors
            boolean success = cl2.await(1000, TimeUnit.MILLISECONDS);
 
            grandChild.removeChild(r);

            success &= cl1.await(1000, TimeUnit.MILLISECONDS);
            if ( !success) {
                fail();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //check this event matches the first in the list of expected events
    //remove the first event from the list
    private void checkExpectedEvent(String name, String type, Identifiable source, IdentifiableTreeEvent contextTreeEvent, LinkedList<String[]> expectedEvents, MultiplexingCountDownLatch c) {
        assertFalse(name, expectedEvents.size() == 0);
        assertEquals(name, contextTreeEvent.getSource(), source);
        String[] event = expectedEvents.removeFirst();
        assertEquals(name, event[0], type);
        assertEquals(name, event[1], contextTreeEvent.getPath());
        assertEquals(name, event[2], contextTreeEvent.getNodes().get(0).getId());
        c.countDown();
    }

    private class TreeEventTestListener implements IdentifiableTreeListener {
        private String name;
        private final LinkedList<String[]> expectedEvents;
        private Identifiable expectedSource;
        private final MultiplexingCountDownLatch c;

        public TreeEventTestListener(String name, LinkedList<String[]> expectedEvents, Identifiable expectedSource, MultiplexingCountDownLatch c) {
            this.name = name;
            this.expectedEvents = expectedEvents;
            this.expectedSource = expectedSource;
            this.c = c;
        }

        public void nodeChanged(Identifiable node, Object changeDescription) {
            checkExpectedEvent(name, "change", expectedSource, new IdentifiableTreeEvent(node, "LOCAL", node), expectedEvents, c);
        }

        public void descendantChanged(IdentifiableTreeEvent contextTreeEvent) {
            checkExpectedEvent(name, "change", expectedSource, contextTreeEvent, expectedEvents, c);
        }

        public void descendantAdded(IdentifiableTreeEvent contextTreeEvent) {
            checkExpectedEvent(name, "add", expectedSource, contextTreeEvent, expectedEvents, c);
        }

        public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent) {
            checkExpectedEvent(name, "remove", expectedSource, contextTreeEvent, expectedEvents, c);
        }
    }

    public class MultiplexingCountDownLatch {

        private CountDownLatch[] countDownLatches;

        public MultiplexingCountDownLatch(CountDownLatch... countDownLatches) {
            this.countDownLatches = countDownLatches;
        }

        public void countDown() {
            for ( CountDownLatch l : countDownLatches ) {
                l.countDown();
            }
        }
    }
}
