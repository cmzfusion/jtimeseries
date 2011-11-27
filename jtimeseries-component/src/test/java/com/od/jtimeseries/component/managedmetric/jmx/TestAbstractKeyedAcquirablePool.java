package com.od.jtimeseries.component.managedmetric.jmx;

import junit.framework.TestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 25/11/11
 * Time: 08:32
 */
public class TestAbstractKeyedAcquirablePool extends TestCase {

    public void testPoolWithManyThreadsPoolSizeOne() {
        doTest(1, 5, 10, true);
    }

    public void testPoolWithPoolSizeSmallerThreadCount() {
        doTest(2, 5, 10, true);
    }

    public void testPoolWithPoolSizeLargerThreadCount() {
        doTest(8, 5, 10, true);
    }

    public void testPoolWithPoolSizeLargerKeyCount() {
        doTest(10, 5, 2, false);
    }

    public void testPoolWithPoolSizeLargerThreadCountAndKeyCount() {
        doTest(12, 5, 10, false);
    }

    public void doTest(int poolSize, int threads, final int keyCount, boolean shouldHaveToRemoveFromPool) {
        long startTime = System.currentTimeMillis();
        final IntegerStringAcquirablePool p = new IntegerStringAcquirablePool(poolSize);

        final CountDownLatch l = new CountDownLatch(threads);
        for ( int loop=0; loop < threads; loop++) {
            new Thread(new Runnable() {
                public void run() {
                    for ( int c = 0; c < 10000;  c++) {
                        int key = (int)(Math.random() * keyCount);
                        StringAcquirable s = null;
                        try {
                            s = p.getAcquirable(key);
                            assertNotNull(s);
                            assertEquals("Acquirable" + key, s.toString());
                        } catch (Throwable t ) {
                            t.printStackTrace();
                            fail("Exception getting Acquirable " + t);
                        } finally {
                            p.returnAcquirable(s);
                        }
                    }
                    l.countDown();
                }
            }).start();
        }
        try {
            l.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if ( shouldHaveToRemoveFromPool) {
            assertTrue(p.getRemoveCount() > 0);
        } else {
            assertTrue(p.getRemoveCount() == 0);
        }

        //more contention should make longer runtime
        System.out.println("Time to run " + ( System.currentTimeMillis() - startTime));
    }


    public static class IntegerStringAcquirablePool extends AbstractKeyedAcquirablePool<Integer,StringAcquirable> {

        private AtomicInteger removes = new AtomicInteger();

        public IntegerStringAcquirablePool(int maxPoolSize) {
            super(maxPoolSize);
        }

        @Override
        protected StringAcquirable createAcquirable(Integer key) throws Exception {
            return new StringAcquirable("Acquirable" + key);
        }

        @Override
        protected void doRemoveAcquirable(Integer key, StringAcquirable acquirable) {
            removes.incrementAndGet();
        }

        public int getRemoveCount() {
            return removes.get();
        }
    }

    public static class StringAcquirable implements Acquirable{

        private volatile int acquireCount;
        private Semaphore semaphore = new Semaphore(1);
        private String name;

        public StringAcquirable(String name) {
            this.name = name;
        }

        public void acquire() {
            semaphore.acquireUninterruptibly();
            acquireCount++;
        }

        public void release() {
            semaphore.release();
        }

        public int getAcquireCount() {
            return acquireCount;
        }

        public String toString() {
            return name;
        }
    }
}
