package com.od.jtimeseries.net.udp.message.utf8;

import com.od.jtimeseries.net.udp.message.MessageType;
import com.od.jtimeseries.net.udp.message.TimeSeriesValueMessage;
import com.od.jtimeseries.timeseries.DefaultTimeSeriesItem;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.util.numeric.DoubleNumeric;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 16/03/12
 * Time: 18:00
 *
 * A time series value message as UTF-8 encoded String of key value pairs, one pair per line
 * Each line should contain one key/value, delimited with '='
 * Any standard system line terminators may be used (e.g. CR/LF, LF)
 *
 * The first two lines must be ENCODING and then MSGTYPE
 * After that the ordering may be variable.
 *
 * e.g.
 * ENCODING=UTF-8
 * MSGTYPE=TS_VALUE
 * PATH=uat.test.component.memory
 * VALUE=512
 *
 * TIMESTAMP= can also be included to send a value for a specific time
 * This should be expressed as a System timestamp - the difference in ms between the current time and midnight, January 1, 1970 UTC
 *
 * If TIMESTAMP is not included, the current system timestamp of the receiving system will be used.
 */
public class Utf8TimeSeriesValueMessage extends AbstractUtf8Message implements TimeSeriesValueMessage {

    public static final String PATH_FIELD_KEY ="PATH";
    public static final String TIMESTAMP_FIELD_KEY ="TIMESTAMP";
    public static final String VALUE_FIELD_KEY ="VALUE";

    private String seriesPath;
    private TimeSeriesItem timeSeriesItem;

    private Double itemValue;
    private long itemTimestamp = -1;

    Utf8TimeSeriesValueMessage() {}

    Utf8TimeSeriesValueMessage(String hostname, String path, TimeSeriesItem timeSeriesItem) {
        super(hostname);
        this.seriesPath = path;
        this.timeSeriesItem = timeSeriesItem;
    }

    public String getSeriesPath() {
        return seriesPath;
    }

    //Send a description as message type TS_DESCRIPTION instead, rather than sending with each value
    public String getDescription() {
        return "";
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

    public Iterable<TimeSeriesItem> getTimeSeriesItems() {
        return Collections.singletonList(timeSeriesItem);
    }

    protected void writeBodyFieldsToOutputStream(Writer writer, StringBuilder sb) throws IOException {
        appendValue(writer, PATH_FIELD_KEY, seriesPath, sb );
        appendValue(writer, TIMESTAMP_FIELD_KEY, String.valueOf(timeSeriesItem.getTimestamp()), sb );
        appendValue(writer, VALUE_FIELD_KEY, String.valueOf(timeSeriesItem.doubleValue()), sb );
    }

    protected void doSetProperties(String key, String value) throws IOException {
        if ( PATH_FIELD_KEY.equals(key)) {
            seriesPath = value;
        } else if ( TIMESTAMP_FIELD_KEY.equals(key)) {
            itemTimestamp = parseLong(TIMESTAMP_FIELD_KEY, value);
        } else if ( VALUE_FIELD_KEY.equals(key)) {
            itemValue = parseDouble(VALUE_FIELD_KEY, value);
        }
    }

    protected void doPostDeserialize() throws IOException {
        if ( seriesPath == null ) {
            throw new IOException(PATH_FIELD_KEY + " field not set in message " + getMessageType());
        } else if ( itemValue == null ) {
            throw new IOException(VALUE_FIELD_KEY + " field not set in message " + getMessageType());
        }

        //take current timestamp as the time value if not set in message
        if ( itemTimestamp == -1 ) {
            itemTimestamp = System.currentTimeMillis();
        }

        timeSeriesItem = new DefaultTimeSeriesItem(
            itemTimestamp,
            DoubleNumeric.valueOf(itemValue)
        );
    }


    public MessageType getMessageType() {
        return MessageType.TS_VALUE;
    }
}
