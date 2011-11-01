package com.od.jtimeseries.util;

import com.od.jtimeseries.capture.CaptureListenerAdapter;

import java.util.concurrent.CountDownLatch;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 01/11/11
 * Time: 19:32
 */
public class AbstractCaptureCountdown extends CaptureListenerAdapter {

    protected volatile CountDownLatch countDownLatch;
    protected final int captureCount;

    public AbstractCaptureCountdown(int captureCount) {
        this.captureCount = captureCount;
        this.countDownLatch = new CountDownLatch(captureCount);
    }

    public void waitForAll() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        countDownLatch = new CountDownLatch(captureCount);
    }
}
