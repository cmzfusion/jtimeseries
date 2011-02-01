package com.od.jtimeseries.util.time;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 */
public class FixedTimeSource implements TimeSource {

    private long time;

    public FixedTimeSource(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
