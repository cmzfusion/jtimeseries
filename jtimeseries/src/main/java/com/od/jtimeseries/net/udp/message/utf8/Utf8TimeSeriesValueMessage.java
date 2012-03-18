package com.od.jtimeseries.net.udp.message.utf8;

import com.od.jtimeseries.net.udp.message.MessageType;
import com.od.jtimeseries.net.udp.message.TimeSeriesValueMessage;
import com.od.jtimeseries.timeseries.TimeSeriesItem;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 16/03/12
 * Time: 18:00
 *
 *
 */
public class Utf8TimeSeriesValueMessage extends AbstractUtf8Message implements TimeSeriesValueMessage {

    private static final String PATH_FIELD_KEY ="PATH";
    private static final String TIMESTAMP_FIELD_KEY ="TIMESTAMP";
    private static final String VALUE_FIELD_KEY ="VALUE";

    private String seriesPath;
    private TimeSeriesItem timeSeriesItem;

    public String getSeriesPath() {
        return seriesPath;
    }

    //Send a description as message type TS_DESCRIPTION instead, rather than sending with each value
    public String getDescription() {
        return null;
    }

    public TimeSeriesItem getTimeSeriesItem(int index) {
        if ( index != 0 ) {
            throw new RuntimeException("No timeseries item for index " + index);
        }
        return timeSeriesItem;
    }

    public int getItemCount() {
        return 1;
    }

    protected void writeBodyFieldsToOutputStream(Writer writer, StringBuilder sb) throws IOException {
        appendValue(writer, PATH_FIELD_KEY, seriesPath, sb );
        appendValue(writer, TIMESTAMP_FIELD_KEY, String.valueOf(timeSeriesItem.getTimestamp()), sb );
        appendValue(writer, VALUE_FIELD_KEY, String.valueOf(timeSeriesItem.doubleValue()), sb );
    }

    public MessageType getMessageType() {
        return MessageType.TS_VALUE;
    }
}
