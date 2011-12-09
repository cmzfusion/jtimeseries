package com.od.jtimeseries.capture.function;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 08/12/11
 * Time: 21:02
 *
 * Whether value(s) from one CapturePeriod are used as an initial value for the
 * next CapturePeriod / CaptureFunction
 */
public enum ChainingMode {
    NO_CHAINING,
    LAST_VALUE_CHAINING
}
