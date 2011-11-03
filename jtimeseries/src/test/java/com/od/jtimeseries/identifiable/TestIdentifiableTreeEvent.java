package com.od.jtimeseries.identifiable;

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
 *
 * Test that we generate the expected events at each level in the tree when nodes are added
 *
 * Events are sent asynchronously but removing/adding nodes takes place immediately
 * We have to use countdown locks to make sure we receive the expected events before removing nodes since removing
 * nodes will stop the event propagation from their descendants
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
        expectedGrandChildEvents.add(new String[] {"add", "test.grandChildContext", "valueSource"});
        expectedGrandChildEvents.add(new String[] {"change", "test.grandChildContext", "valueSource"});
        expectedGrandChildEvents.add(new String[] {"remove", "test.grandChildContext", "valueSource"});

        //paths of events should be relative to grandchild node
        final LinkedList<String[]> expectedValueRecorderEvents = new LinkedList<String[]>();
        expectedValueRecorderEvents.add(new String[] {"change", "LOCAL", "valueSource"});

        int totalEventCount = expectedRootEvents.size() +
                expectedGrandChildEvents.size() +
                expectedValueRecorderEvents.size();

        final CountDownLatch allEventsCounter = new CountDownLatch(totalEventCount);
        final CountDownLatch allEventsApartFromRemovesCounter = new CountDownLatch(totalEventCount - 2);

        //when we receive an event, pass it to all the countdown latches so they all count down
        MultiplexingCountDownLatch multiplexingCounter = new MultiplexingCountDownLatch(allEventsCounter, allEventsApartFromRemovesCounter);

        //add listener to root and count events
        rootContext.addTreeListener(new TreeEventTestListener("rootContext", expectedRootEvents, rootContext, multiplexingCounter));
        TimeSeriesContext grandChild = rootContext.createContext(
            "test.grandChildContext"
        );

        //add listener to grandchild to count local grandchild events
        grandChild.addTreeListener(new TreeEventTestListener("grandChild", expectedGrandChildEvents, rootContext, multiplexingCounter));

        ValueRecorder r = grandChild.createValueRecorder("valueSource", "valueSource");
        //add listener to count local value recorder events
        r.addTreeListener(new TreeEventTestListener("valueSource", expectedValueRecorderEvents, rootContext, multiplexingCounter));

        r.fireNodeChanged("change");


        try {
            //wait for previous events to propagate before
            //firing tree structure remove, otherwise there is a danger we remove
            //before the event firing thread has fired all previous events to ancestors - we might never see the nodeChanged event
            boolean success = allEventsApartFromRemovesCounter.await(1000, TimeUnit.MILLISECONDS);
 
            grandChild.removeChild(r);

            success &= allEventsCounter.await(1000, TimeUnit.MILLISECONDS);
            if ( !success) {
                fail();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testEventFiredWhenAddingToParentCausesRemoveFromExistingParent() {
        final LinkedList<String[]> expectedRootEvents = new LinkedList<String[]>();
        expectedRootEvents.add(new String[] {"remove", "test", "valueRecorder"});

        CountDownLatch c = new CountDownLatch(1);
        TimeSeriesContext rootContext = JTimeSeries.createRootContext();
        ValueRecorder v = rootContext.createValueRecorder("test.valueRecorder", "test value recorder");
        rootContext.addTreeListener(new TreeEventTestListener("rootContext", expectedRootEvents, rootContext, c));

        TimeSeriesContext newRoot = JTimeSeries.createRootContext();
        newRoot.addChild(v);

        boolean success = false;
        try {
             success = c.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if ( ! success ) {
            fail("Failed to receive remove event");
        }
    }

    private class TreeEventTestListener implements IdentifiableTreeListener {
        private String name;
        private final LinkedList<String[]> expectedEvents;
        private Identifiable expectedRootContext;
        private MultiplexingCountDownLatch multiplexingCountDownLatch;
        private CountDownLatch countDownLatch;

        public TreeEventTestListener(String name, LinkedList<String[]> expectedEvents, Identifiable expectedRootContext, MultiplexingCountDownLatch multiplexingCountDownLatch) {
            this.name = name;
            this.expectedEvents = expectedEvents;
            this.expectedRootContext = expectedRootContext;
            this.multiplexingCountDownLatch = multiplexingCountDownLatch;
        }

        public TreeEventTestListener(String name, LinkedList<String[]> expectedEvents, Identifiable expectedRootContext, CountDownLatch countDownLatch) {
            this.name = name;
            this.expectedEvents = expectedEvents;
            this.expectedRootContext = expectedRootContext;
            this.countDownLatch = countDownLatch;
        }

        public void nodeChanged(Identifiable node, Object changeDescription) {
            checkExpectedEvent(name, "change", expectedRootContext, new IdentifiableTreeEvent(
                IdentifiableTreeEvent.TreeEventType.CHANGE,
                expectedRootContext,
                "LOCAL",
                node),
                expectedEvents
            );
        }

        public void descendantChanged(IdentifiableTreeEvent contextTreeEvent) {
            checkExpectedEvent(name, "change", expectedRootContext, contextTreeEvent, expectedEvents);
        }

        public void descendantAdded(IdentifiableTreeEvent contextTreeEvent) {
            checkExpectedEvent(name, "add", expectedRootContext, contextTreeEvent, expectedEvents);
        }

        public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent) {
            checkExpectedEvent(name, "remove", expectedRootContext, contextTreeEvent, expectedEvents);
        }

        //check this event matches the first in the list of expected events
        //remove the first event from the list
        private void checkExpectedEvent(String name, String type, Identifiable source, IdentifiableTreeEvent contextTreeEvent, LinkedList<String[]> expectedEvents) {
            assertFalse(name, expectedEvents.size() == 0);
            assertEquals(name, contextTreeEvent.getRootNode(), source);
            String[] event = expectedEvents.removeFirst();
            assertEquals(name, event[0], type);
            assertEquals(name, event[1], contextTreeEvent.getPath());
            assertEquals(name, event[2], contextTreeEvent.getNodes().iterator().next().getId());
            countDown();
        }

        private void countDown() {
            if ( multiplexingCountDownLatch != null)  {
                multiplexingCountDownLatch.countDown();
            } else {
                countDownLatch.countDown();
            }
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
