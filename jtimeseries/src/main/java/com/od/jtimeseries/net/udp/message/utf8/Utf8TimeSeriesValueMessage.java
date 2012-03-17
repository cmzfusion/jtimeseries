package com.od.jtimeseries.net.udp.message.utf8;

import com.od.jtimeseries.net.udp.message.TimeSeriesValueMessage;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 16/03/12
 * Time: 18:00
 */
public class Utf8TimeSeriesValueMessage extends AbstractUtf8Message implements TimeSeriesValueMessage {

    private String seriesPath;
    private String description;
    private TimeSeriesItem timeSeriesItem;
    private String inetAddress;
    private String hostname;

    public String getSeriesPath() {
        return seriesPath;
    }

    public String getDescription() {
        return description;
    }

    public TimeSeriesItem getTimeSeriesItem() {
        return timeSeriesItem;
    }

    public String getSourceInetAddress() {
        return inetAddress;
    }

    public String getSourceHostname() {
        return hostname;
    }

    @Override
    public void serialize(OutputStream outputStream) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getMaxExpectedSize() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
