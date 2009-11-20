package com.od.jtimeseries.capture;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 20-Nov-2009
 * Time: 19:27:29
 * To change this template use File | Settings | File Templates.
 *
 * Subclasses can extend this if they don't want to provide an implementation for both methods
 */
public class CaptureAdapter implements CaptureListener {

    public void captureStateChanged(Capture source, CaptureState oldState, CaptureState newState) {
    }

    public void captureTriggered(Capture source) {
    }
}
